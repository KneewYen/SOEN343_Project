package org.ridewithus.controller;

import org.ridewithus.domain.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/availableBikes/{stationId}")
    public int getReservation(@PathVariable("stationId") Long stationId) throws Exception {
        return stationService.getBikesAvailable(stationId);
    }

    @GetMapping("/freeDocks/{stationId}")
    public int isReservationValid(@PathVariable("stationId") Long stationId) throws Exception {
        return stationService.getFreeDocks(stationId);
    }
}
