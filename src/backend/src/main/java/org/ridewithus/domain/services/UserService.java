package org.ridewithus.domain.services;

import java.time.LocalDateTime;

import org.ridewithus.domain.entity.Trip;
import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.BronzeHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.GoldHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.SilverHandler;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.Tier;
import org.ridewithus.domain.loyaltyProgram.ChainOfR.TierHandler;
import org.ridewithus.infrastructure.repository.EventRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired 
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TripRepository tripRepository;

    public boolean hasMissedReservations(User user){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusYears(1);

        boolean condition = eventRepository.existsByUserIdAndEventTypeAndTimestampAfter(user, "RESERVATION_EXPIRED", start_search);

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
    public boolean hasAmountReservationInYear(User user, int amount){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusYears(1);

        return true;
    }

    public boolean hasXtripPerMonthForXMonths(User user, int trip_amount, int frequency){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusMonths(1);

        for(int i=0; i < frequency; i++){
            List<Trip> trips = tripRepository.findByUserIdAndStartTimeAfterAndTripCompleteTrue(user.getId(), start_search);

            if (trips.size() < trip_amount){
                return false;
            }

            start_search = start_search.minusMonths(1);
        }
        return true;
    }

    // GOLD Tier 
    public boolean hasXtripPerWeekForXWeeks(User user, int trip_amount, int frequency){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start_search = now.minusWeeks(1);

        for(int i=0; i < frequency; i++){
            List<Trip> trips = tripRepository.findByUserIdAndStartTimeAfterAndTripCompleteTrue(user.getId(), start_search);

            if (trips.size() < trip_amount){
                return false;
            }

            start_search = start_search.minusWeeks(1);
        }
        return true;
    }

    // update tier of user 
    public void updateLoyaltyTier(User user){
        TierHandler bronzeHandler = new BronzeHandler();
        TierHandler silverHandler = new SilverHandler();
        TierHandler goldHandler = new GoldHandler();

        bronzeHandler.setNext(silverHandler);
        silverHandler.setNext(goldHandler);

        Tier updatedTier = bronzeHandler.handle(user);
        Tier currentTier = user.getLoyaltyTier();

        // only update if the tier changes
        if (updatedTier != currentTier){
            user.setLoyaltyTier(updatedTier);
            userRepository.save(user);
        }
    }
}
