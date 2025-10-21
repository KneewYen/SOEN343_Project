package org.ridewithus.controller;

import org.ridewithus.domain.dto.ReservationDTO;
import org.ridewithus.domain.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{reservationId}")
    public ReservationDTO getReservation(@PathVariable("reservationId") Long reservationId) {
        return reservationService.getReservation(reservationId);
    }

    @GetMapping("/valid/{reservationId}")
    public boolean isReservationValid(@PathVariable("reservationId") Long reservationId) {
        return reservationService.isReservationValid(reservationId);
    }

    @DeleteMapping("/delete/{reservationId}")
    public void deleteReservation(@PathVariable("reservationId") Long reservationId) throws Exception{
        reservationService.deleteReservation(reservationId);
    }

    @PostMapping("/createReservation/{bikeId}/{userId}")
    public Long createReservation(@PathVariable("bikeId") Long bikeId, @PathVariable("userId")  Long userId) throws Exception {
        return reservationService.createReservation(bikeId, userId);
    }

}
