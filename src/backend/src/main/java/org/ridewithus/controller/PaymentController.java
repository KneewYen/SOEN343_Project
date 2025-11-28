package org.ridewithus.controller;

import org.ridewithus.domain.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            Long tripId = Long.valueOf(paymentData.get("tripId").toString());
            String paymentMethod = paymentData.get("paymentMethod") != null ? 
                    paymentData.get("paymentMethod").toString() : "card";
            Map<String, Object> paymentDetails = paymentData.get("paymentDetails") != null ? 
                    (Map<String, Object>) paymentData.get("paymentDetails") : new HashMap<>();

            // Use the new method that automatically applies Flex Dollars
            Map<String, Object> result = paymentService.processTripPaymentWithFlexDollars(
                    tripId, paymentMethod, paymentDetails);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status/{tripId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long tripId) {
        try {
            Map<String, Object> status = paymentService.getPaymentStatus(tripId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/external")
    public ResponseEntity<Map<String, Object>> redirectToExternalPayment(
            @RequestParam Long tripId,
            @RequestParam Double amount) {
        try {
            // This endpoint would typically redirect to an external payment service
            // For now, we'll return a URL that the frontend can use
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("redirectUrl", "/api/payment/external/process?tripId=" + tripId + "&amount=" + amount);
            response.put("message", "Redirecting to external payment service");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

