package org.ridewithus.domain.services;

import java.util.Optional;

import org.ridewithus.domain.entity.Bike;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BikeService {
    
    @Autowired
    private BikeRepository bikeRepository;

    public void calculateRating(Long bikeId, Long score) throws Exception{
        Optional<Bike> b = bikeRepository.findById(bikeId);

        if (b.isEmpty()) {
            throw new Exception("User not found");
        }

        Bike bike = b.get();

        Long currentScore = bike.getRating();
        Long totaReviews = bike.getNumReviews();

        totaReviews = totaReviews+1;

        Long Updatedscore = (currentScore + score) / totaReviews;
        // System.out.print("current score", currentScore);
        // System.out.println("update score", Updatedscore);

        bike.setNumReviews(totaReviews);
        bike.setRating(Updatedscore);
        bikeRepository.save(bike);
    }
}
