package org.ridewithus.controller;

import org.ridewithus.domain.loyaltyProgram.ChainOfR.Tier;
import org.ridewithus.domain.services.TripService;
import org.ridewithus.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/UserRequest")
public class UserRequest {

    @Autowired
    private TripService tripService;
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getLoyaltyTierUpdate(@PathVariable("userId") Long userId) {
        try {
            Tier tier = tripService.getTierByUser(userService.getUserById(userId));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tier", tier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
