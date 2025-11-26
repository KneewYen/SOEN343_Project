package org.ridewithus.controller;

import java.util.HashMap;
import java.util.Map;

import org.ridewithus.domain.services.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bike")
public class BikeController {
    
    @Autowired
    private BikeService bikeService;

    @PostMapping("/rating/{bikeId}/{rating}")
    public ResponseEntity<Map<String, Object>> submitBikeRating(@PathVariable("bikeId") Long bikeId, @PathVariable("rating") Long rating){
        try {
            bikeService.calculateRating(bikeId, rating);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("response", "Plan has been selected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
        
