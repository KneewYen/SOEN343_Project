package org.ridewithus.domain.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ridewithus.domain.dto.TripDTO;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class TripService {

    @Autowired
    private BronzeHandler bronzeHandler;

    @Autowired
    private SilverHandler silverHandler;

    @Autowired
    private GoldHandler goldHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DockRepository dockRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DomainEventService eventService;
    @Autowired
    private StationService stationService;
    @Autowired
    private FlexDollarService flexDollarService;

    @PostConstruct
    public void initChain() {
        bronzeHandler.setNext(silverHandler);
        silverHandler.setNext(goldHandler);
    }

    @Transactional
    public Long startTrip(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findByReservationId(reservationId).orElseThrow();

        if (reservation == null || reservation.getExpiryDateTime().isBefore(LocalDateTime.now())){
            throw new Exception("Reservation is Invalid or has Expired");
        }

        // Check if user is an operator - operators cannot start trips
        if ("operator".equals(reservation.getUser().getRole())) {
            throw new Exception("Operators cannot start trips. Only riders can use bikes.");
        }

        //Finds the station where the bike currently sits
        Station station = reservation.getBike().getDock().getStation();

        if (station == null) {
            throw new Exception("Station does not Exists");
        }
        //Decrements available bikes at that station and marks the dock as empty
        station.setCount(station.getCount() - 1);

        reservation.getBike().getDock().setStatus(Dock.DockStatus.EMPTY);

        dockRepository.save(reservation.getBike().getDock());

        String oldStatus = reservation.getBike().getStatus().toString();

        reservation.getBike().checkOut();

        String newStatus = reservation.getBike().getStatus().toString();

        bikeRepository.save(reservation.getBike());

        stationRepository.save(station);

        Trip trip = Trip.builder()
                .startStation(station)
                .reservation(reservation)
                .user(reservation.getUser())
                .bike(reservation.getBike())
                .startTime(LocalDateTime.now())
                .build();

        tripRepository.save(trip);

        eventService.emitEvent(trip.getReservation().getUser(),"TRIP_STARTED", String.format("Trip %d started - Bike %d: %s -> %s", trip.getTripId(), reservation.getBike().getId(), oldStatus, newStatus));

        stationService.checkRebalance(trip.getUser(), station.getId());

        return trip.getTripId();

    }

    @Transactional
    public Map<String, Object> endTrip(Long tripId, Long endStationId) throws Exception {

        Trip trip = tripRepository.findByTripId(tripId);

        if (trip == null) {
            throw new Exception("Trip does not Exists");
        }

        Optional<Station> station = stationRepository.findById(endStationId);

        if (station.isEmpty()) {
            throw new Exception("Station does not Exists");
        }

        List<Dock> docks = dockRepository.findByStationAndStatus(station.get(), Dock.DockStatus.EMPTY);


        if (docks.isEmpty()) {
            throw new Exception("No empty places available to Dock");
        }

        // Check capacity BEFORE returning the bike to determine if Flex Dollars should be awarded
        // We need to check if the station will be below 25% AFTER we return this bike
        // So we check current capacity + 1 (the bike we're about to return)
        Station stationEntity = station.get();
        int currentBikesBeforeReturn = stationService.getBikesAvailable(endStationId).size();
        int bikesAfterReturn = currentBikesBeforeReturn + 1; // +1 for the bike we're returning
        int totalCapacity = stationEntity.getCapacity();
        double occupancyAfterReturn = (double) bikesAfterReturn / totalCapacity;
        boolean willBeBelowThreshold = occupancyAfterReturn < 0.25;

        // Debug logging
        System.out.println(String.format(
            "Flex Dollar Check - Station: %s (ID: %d), Current bikes: %d, After return: %d, Capacity: %d, Occupancy: %.2f%%, Below threshold: %s",
            stationEntity.getName(), endStationId, currentBikesBeforeReturn, bikesAfterReturn,
            totalCapacity, occupancyAfterReturn * 100, willBeBelowThreshold
        ));

        //increment available bikes at station
        //Marks one more bike as present at the station and assigns the bike to an empty dock.
        stationEntity.setCount(stationEntity.getCount() + 1);

        Dock assignedDock = docks.getFirst();
        trip.getReservation().getBike().setDock(assignedDock);

        String oldStatus = trip.getReservation().getBike().getStatus().toString();

        trip.getReservation().getBike().returnBike();

        // Set dock status to OCCUPIED since bike is now in the dock
        assignedDock.setStatus(Dock.DockStatus.OCCUPIED);

        dockRepository.save(assignedDock);

        bikeRepository.save(trip.getReservation().getBike());

        stationRepository.save(stationEntity);

        trip.setEndTime(LocalDateTime.now());
        trip.setEndStation(stationEntity);
        trip.setTripComplete(true);

        //Store user for event
        User user = trip.getReservation().getUser();

        // Store reservation reference before clearing it
        Reservation reservation = trip.getReservation();


        tripRepository.save(trip);

        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        // Award Flex Dollars if station will be below minimum capacity (< 25%) after return
        // This implements UC1: Award Flex Dollars
        Map<String, Object> flexDollarAward = awardFlexDollars(user, endStationId, station, trip.getTripId(), willBeBelowThreshold);
        boolean flexDollarAwarded = (Boolean) flexDollarAward.get("awarded");

        // Reload user to get updated balance if Flex Dollars were awarded
        if (flexDollarAwarded) {
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        String newStatus = reservation.getBike().getStatus().toString();

        eventService.emitEvent(user,"TRIP_ENDED", String.format("Trip %d ended - Bike %d: %s -> %s", trip.getTripId(), reservation.getBike().getId(), oldStatus, newStatus));

        // Get free docks at destination
        List<Dock> freeDocks = dockRepository.findAllByStationAndStatus(station.get(), Dock.DockStatus.EMPTY);

        if (freeDocks.isEmpty()){
            eventService.emitEvent(user, "STATION_FULL", String.format("%s station is full", station.get().getName()));
        }

        //updateLoyaltyTier(user);

        stationService.checkRebalance(user, trip.getStartStation().getId());

        // Prepare response with trip ID, flex dollar balance, and award information
        Map<String, Object> result = new HashMap<>();
        result.put("tripId", trip.getTripId());
        result.put("flexDollarBalance", user.getFlexDollars());
        result.put("flexDollarAwarded", flexDollarAwarded);
        result.put("flexDollarAmountAwarded", flexDollarAward.get("amountAwarded"));
        result.put("flexDollarConfirmationMessage", flexDollarAward.get("confirmationMessage"));

        return result;

    }

    /**
     * Award Flex Dollars to a rider when they return a bike to a low-occupancy station.
     * Implements UC1: Award Flex Dollars
     *
     * @param user The rider
     * @param endStationId The station where the bike was returned
     * @param stationOpt Optional station object
     * @param tripId The trip ID for ledger reference
     * @param isBelowThreshold Whether the station will be below 25% after the bike is returned
     * @return Map containing award information: awarded (boolean), amountAwarded (int), newBalance (int), confirmationMessage (String)
     */
    private Map<String, Object> awardFlexDollars(User user, Long endStationId, Optional<Station> stationOpt, Long tripId, boolean isBelowThreshold) {
        Map<String, Object> result = new HashMap<>();
        result.put("awarded", false);
        result.put("amountAwarded", 0);
        result.put("newBalance", user.getFlexDollars());
        result.put("confirmationMessage", "");

        try {
            // Award Flex Dollars if station will be below 25% threshold after return
            if (isBelowThreshold) {
                // Award 1 Flex Dollar using the service (creates ledger entry)
                int amountAwarded = 1;
                int newBalance = flexDollarService.creditFlexDollars(user.getId(), amountAwarded, endStationId, tripId);

                // Use the balance returned from creditFlexDollars (it's already updated)
                // No need to reload user - the service method already refreshed it

                Station station = stationOpt.orElseThrow(() -> new RuntimeException("Station not found"));

                // Create confirmation message
                String confirmationMessage = String.format(
                    "You earned %d Flex Dollar%s for helping balance station capacity at %s. Your new balance is %d Flex Dollar%s.",
                    amountAwarded,
                    amountAwarded == 1 ? "" : "s",
                    station.getName(),
                    newBalance,
                    newBalance == 1 ? "" : "s"
                );

                // Emit event
                eventService.emitEvent(user, "FLEX_DOLLAR_AWARDED", confirmationMessage);

                // Update result
                result.put("awarded", true);
                result.put("amountAwarded", amountAwarded);
                result.put("newBalance", newBalance);
                result.put("confirmationMessage", confirmationMessage);

                return result;
            }
        } catch (Exception e) {
            // Log but don't fail the trip if flex dollar award fails
            System.err.println("Failed to award flex dollars: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public Tier getTier(Long tripId) throws Exception {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip == null) {
            throw new Exception("Trip does not Exists");
        }
        if (trip.getUser().getLoyaltyTier() != trip.getUser().getPrevLoyaltyTier()) {
            return trip.getUser().getLoyaltyTier();
        } else {
            return null;
        }
    }

    public Tier getTierByUser(User user) {
        Tier updatedTier = bronzeHandler.handle(user);
        Tier currentTier = user.getLoyaltyTier();

        if (updatedTier != currentTier){
            user.setLoyaltyTier(updatedTier);
            user.setPrevLoyaltyTier(updatedTier);
            userService.save(user);
            return updatedTier;
        } else {
            user.setPrevLoyaltyTier(updatedTier);
            userService.save(user);
            return null;
        }
    }

    // update tier of user
    public void updateLoyaltyTier(User user){
        Tier updatedTier = bronzeHandler.handle(user);
        Tier currentTier = user.getLoyaltyTier();

        // only update if the tier changes
        if (updatedTier != currentTier){
            user.setLoyaltyTier(updatedTier);
            userService.save(user);
        }
    }

    public List<TripDTO> getUserTrips(Long userId) {
        List<Trip> trips = tripRepository.findByUserId(userId);
        return trips.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<TripDTO> getIncompleteUserTrips(Long userId) {
        List<Trip> trips = tripRepository.findByUserIdAndTripComplete(userId, false);
        return trips.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Page<TripDTO> getAllTrips(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").descending());
        Page<Trip> trips = tripRepository.findAll(pageable);

        return trips.map(this::mapToDTO);
    }

    public Page<TripDTO> getUserTrips(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").descending());
        Page<Trip> trips = tripRepository.findByUserId(userId, pageable);

        return trips.map(this::mapToDTO);
    }

    private TripDTO mapToDTO(Trip trip) {
        return TripDTO.builder()
                .tripId(trip.getTripId())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .tripComplete(trip.isTripComplete())
                .bikeType(trip.getBike().getType())
                .userName(trip.getUser().getUserName())
                .startStationName(trip.getStartStation() != null ? trip.getStartStation().getName() : null)
                .endStationName(trip.getEndStation() != null ? trip.getEndStation().getName() : null)
                .startStationId(trip.getStartStation() != null ? trip.getStartStation().getId() : null)
                .endStationId(trip.getEndStation() != null ? trip.getEndStation().getId() : null)
                .reservationId(trip.getReservation() != null ? trip.getReservation().getReservationId() : null)
                .bikeId(trip.getBike() != null ? trip.getBike().getId() : null)
                .build();
    }

}
