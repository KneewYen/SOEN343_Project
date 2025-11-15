package org.ridewithus.controller;

import org.apache.coyote.Response;
import org.ridewithus.domain.dto.BikeDTO;
import org.ridewithus.domain.dto.ReservationDTO;
import org.ridewithus.domain.entity.Reservation;
import org.ridewithus.domain.services.ReservationService;
import org.ridewithus.infrastructure.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/{reservationId}")
    public ReservationDTO getReservation(@PathVariable("reservationId") Long reservationId) {
        return reservationService.getReservation(reservationId);
    }

    @GetMapping("/valid/{reservationId}")
    public boolean isReservationValid(@PathVariable("reservationId") Long reservationId) {
        return reservationService.isReservationValid(reservationId);
    }

    @DeleteMapping("/delete/{reservationId}")
    public ResponseEntity<Map<String, Object>> deleteReservation(@PathVariable("reservationId") Long reservationId) throws Exception{
        reservationService.deleteReservation(reservationId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Reservation cancelled");

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/createReservation/{bikeId}/{userId}")
    public ResponseEntity<?> createReservation(@PathVariable("bikeId") Long bikeId, @PathVariable("userId")  Long userId) {
        try {
            Long reservationId = reservationService.createReservation(bikeId, userId);

            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new RuntimeException("Reservation not found"));

            ReservationDTO reservationDTO = ReservationDTO.fromEntity(reservation);

            return ResponseEntity.ok(reservationDTO);


        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserReservations(@PathVariable("userId") Long userId) {
        try {
            List<ReservationDTO> reservations = reservationService.getUserReservations(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reservations", reservations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
