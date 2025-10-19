package org.ridewithus.controller;

import org.ridewithus.domain.entity.User;
import org.ridewithus.domain.services.OperatorService;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/bike/{id}/toggle")
    public String toggleBike(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return operatorService.toggleBikeStatus(id, operator);
    }

    @PostMapping("/dock/{id}/toggle")
    public String toggleDock(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return operatorService.toggleDockStatus(id, operator);
    }

    @PostMapping("/station/{id}/toggle")
    public String toggleStation(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return operatorService.toggleStationStatus(id, operator);
    }

    // Example request: /api/operator/moveBike?bikeId=5&sourceStationId=2&destinationStationId=4&userId=1
    @PostMapping("/moveBike")
    public String moveBike(@RequestParam("bikeId") Long bikeId, @RequestParam("sourceStationId") Long sourceStationId,
                           @RequestParam("destinationStationId") Long destinationSourceId, @RequestParam("userId") Long userId) {

        User operator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return operatorService.moveBike(bikeId, sourceStationId, destinationSourceId, operator);
    }

}
