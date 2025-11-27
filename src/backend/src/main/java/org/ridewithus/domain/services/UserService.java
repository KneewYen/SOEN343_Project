package org.ridewithus.domain.services;

import java.time.LocalDateTime;

import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.entity.Reservation.ReservationStatus;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.BronzeHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.GoldHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.SilverHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.Tier;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.TierHandler;
import org.ridewithus.infrastructure.repository.EventRepository;
import org.ridewithus.infrastructure.repository.ReservationRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired 
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TripRepository tripRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    @Transactional
    public boolean hasMissedReservations(User user){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusYears(1);

        boolean condition = reservationRepository.existsByUserAndStatusAndExpiryDateTimeAfter(user, ReservationStatus.EXPIRED, start_search);

        return condition;
    }

    
    public boolean returnAllBikesForLife(User user){
        List<Trip> trips = tripRepository.findByUserId(user.getId());

        for(Trip trip : trips){
            if(!trip.isTripComplete()){
                return false;
            }
        }
        return true;
    }
    @Transactional
    public boolean hasCompletedAmountTripsInYear(User user, int amount){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusYears(1);

        List<Trip> trips = tripRepository.findByUserIdAndStartTimeAfterAndTripCompleteTrue(user.getId(), start_search);

        if (trips.size() >= amount){
            return true;
        }
        else{
            return false;
        }
    }

    //SILVER TIER
    @Transactional
    public boolean hasAmountReservationInYear(User user, int amount){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusYears(1);

        return true;
    }

    @Transactional
    public boolean hasXtripPerMonthForXMonths(User user, int trip_amount, int frequency){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusMonths(1);

        int add_amount = trip_amount;
        for(int i=0; i < frequency; i++){
            List<Trip> trips = tripRepository.findByUserIdAndStartTimeAfterAndTripCompleteTrue(user.getId(), start_search);

            if (trips.size() < trip_amount){
                return false;
            }

            start_search = start_search.minusMonths(1);
            trip_amount += add_amount;
        }
        return true;
    }

    // GOLD Tier 
    @Transactional
    public boolean hasXtripPerWeekForXWeeks(User user, int trip_amount, int frequency){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusWeeks(1);

        int add_amount = trip_amount;
        for(int i=0; i < frequency; i++){
            List<Trip> trips = tripRepository.findByUserIdAndStartTimeAfterAndTripCompleteTrue(user.getId(), start_search);

            System.out.println("I" + i + " size:" + trips.size());
            if (trips.size() < trip_amount){
                System.out.println("Ifalse" + i );
                return false;
            }

            start_search = start_search.minusWeeks(1);
            trip_amount += add_amount;
        }
        return true;
    }


}
