package org.ridewithus.controller;

import org.ridewithus.domain.dto.BillingDTO;
import org.ridewithus.domain.dto.ChargeDTO;
import org.ridewithus.domain.entity.Billing;
import org.ridewithus.domain.entity.Charge;
import org.ridewithus.domain.services.PricingService;
import org.ridewithus.infrastructure.repository.BillingRepository;
import org.ridewithus.infrastructure.repository.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PricingService pricingService;

    @PostMapping("/create/{tripId}")
    public ResponseEntity<Map<String, Object>> createBillingForTrip(@PathVariable Long tripId) {
        try {
            // Call calculatePricingPlan which will create billing and charges
            pricingService.calculatePricingPlan(tripId);
            
            // Get the created billing
            Optional<Billing> billingOpt = billingRepository.findByTrip_TripId(tripId);
            
            Map<String, Object> response = new HashMap<>();
            if (billingOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Failed to create billing");
                return ResponseEntity.badRequest().body(response);
            }
            
            Billing billing = billingOpt.get();
            List<ChargeDTO> chargeDTOs = billing.getCharges().stream()
                    .map(charge -> ChargeDTO.builder()
                            .name(charge.getName())
                            .description(charge.getDescription())
                            .cost(charge.getCost())
                            .build())
                    .collect(Collectors.toList());
            
            double totalAmount = billing.getCharges().stream()
                    .mapToDouble(Charge::getCost)
                    .sum();
            
            BillingDTO billingDTO = BillingDTO.builder()
                    .billingId(billing.getBillingId())
                    .tripId(billing.getTrip().getTripId())
                    .charges(chargeDTOs)
                    .totalAmount(totalAmount)
                    .build();
            
            response.put("success", true);
            response.put("billing", billingDTO);
            response.put("message", "Billing created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<Map<String, Object>> getBillingByTripId(@PathVariable("tripId") Long tripId) {
        try {
            Optional<Billing> billingOpt = billingRepository.findByTrip_TripId(tripId);
            
            Map<String, Object> response = new HashMap<>();
            if (billingOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Billing not found for this trip");
                return ResponseEntity.ok(response);
            }
            
            Billing billing = billingOpt.get();
            List<ChargeDTO> chargeDTOs = billing.getCharges().stream()
                    .map(charge -> ChargeDTO.builder()
                            .name(charge.getName())
                            .description(charge.getDescription())
                            .cost(charge.getCost())
                            .build())
                    .collect(Collectors.toList());
            
            double totalAmount = billing.getCharges().stream()
                    .mapToDouble(Charge::getCost)
                    .sum();
            
            BillingDTO billingDTO = BillingDTO.builder()
                    .billingId(billing.getBillingId())
                    .tripId(billing.getTrip().getTripId())
                    .charges(chargeDTOs)
                    .totalAmount(totalAmount)
                    .build();
            
            response.put("success", true);
            response.put("billing", billingDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getBillingsByUserId(@PathVariable Long userId) {
        try {
            List<Billing> billings = billingRepository.findByTrip_User_Id(userId);
            
            List<BillingDTO> billingDTOs = billings.stream()
                    .map(billing -> {
                        List<ChargeDTO> chargeDTOs = billing.getCharges().stream()
                                .map(charge -> ChargeDTO.builder()
                                        .name(charge.getName())
                                        .description(charge.getDescription())
                                        .cost(charge.getCost())
                                        .build())
                                .collect(Collectors.toList());
                        
                        double totalAmount = billing.getCharges().stream()
                                .mapToDouble(Charge::getCost)
                                .sum();
                        
                        return BillingDTO.builder()
                                .billingId(billing.getBillingId())
                                .tripId(billing.getTrip().getTripId())
                                .charges(chargeDTOs)
                                .totalAmount(totalAmount)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("billings", billingDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

