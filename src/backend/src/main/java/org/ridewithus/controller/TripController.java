package org.ridewithus.controller;

import org.ridewithus.domain.dto.TripDTO;
import org.ridewithus.domain.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/{reservationId}")
    public ResponseEntity<Map<String, Object>> startTrip(@PathVariable("reservationId") Long reservationId) {
        try {
            Long tripId = tripService.startTrip(reservationId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tripId", tripId);
            response.put("message", "Trip started successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{tripId}/{stationId}")
    public ResponseEntity<Map<String, Object>> endTrip(@PathVariable("tripId") Long tripId, @PathVariable("stationId") Long stationId) {
        try {
            Long endTripId = tripService.endTrip(tripId, stationId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tripId", endTripId);
            response.put("message", "Trip ended successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTrips(@PathVariable("userId") Long userId) {
        try {
            List<TripDTO> trips = tripService.getUserTrips(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("trips", trips);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
