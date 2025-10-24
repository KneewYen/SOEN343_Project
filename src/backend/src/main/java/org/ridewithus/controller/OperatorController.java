package org.ridewithus.controller;

import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.OperatorService;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/operator")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;
    @Autowired
    private UserRepository userRepository;

    // Example: /api/operator/bike/2/toggle?userId=1
    @PostMapping("/bike/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleBike(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        try {
            User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            String result = operatorService.toggleBikeStatus(id, operator);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/dock/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleDock(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        try {
            User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            String result = operatorService.toggleDockStatus(id, operator);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/station/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleStation(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        try {
            User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            String result = operatorService.toggleStationStatus(id, operator);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Example: /api/operator/moveBike?bikeId=5&sourceStationId=2&destinationStationId=4&userId=1
    @PostMapping("/moveBike")
    public ResponseEntity<Map<String, Object>> moveBike(@RequestParam("bikeId") Long bikeId,
                           @RequestParam("sourceStationId") Long sourceStationId,
                           @RequestParam("destinationStationId") Long destinationSourceId,
                           @RequestParam("userId") Long userId) {
        try {
            User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            String result = operatorService.moveBike(bikeId, sourceStationId, destinationSourceId, operator);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Example: /api/operator/rebalanceBikes?sourceStationId=2&destinationId=5&numberOfBikes=3&userId=1
    @PostMapping("/rebalanceBikes")
    public ResponseEntity<Map<String, Object>> rebalanceBikes(@RequestParam("sourceStationId") Long sourceStationId,
                                 @RequestParam("destinationStationId") Long destionationStationId,
                                 @RequestParam("numberOfBikes") int numberOfBikes,
                                 @RequestParam("userId") Long userId) {
        try {
            User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            String result = operatorService.rebalanceBikes(sourceStationId, destionationStationId, numberOfBikes, operator);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
