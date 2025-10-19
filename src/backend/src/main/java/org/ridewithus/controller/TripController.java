package org.ridewithus.controller;

import org.ridewithus.domain.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/{reservationId}")
    public Long startTrip(@PathVariable("reservationId") Long reservationId) throws Exception {
        return tripService.startTrip(reservationId);
    }

    @PutMapping("/{tripId}/{stationId}")
    public Long endTrip(@PathVariable("tripId") Long tripId, @PathVariable("stationId") Long stationId) throws Exception {
        return tripService.endTrip(tripId, stationId);
    }

}
