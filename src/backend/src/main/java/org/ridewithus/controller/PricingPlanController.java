package org.ridewithus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ridewithus.domain.dto.ReservationDTO;
import org.ridewithus.domain.entity.PricingPlan;
import org.ridewithus.domain.services.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingPlanController {
    
    @Autowired
    private PricingService pricingService;

    @GetMapping("/plans")
    public ResponseEntity<List<PricingPlan>> getAllPlans() {
        return ResponseEntity.ok(pricingService.getAllPlans());
    }

   @PostMapping("/select/{userId}/{planId}")
   public ResponseEntity<Map<String, Object>> selectPlan(@PathVariable("userId") Long userId, @PathVariable("planId") Long planId ) {
        
        try {
            pricingService.assignPlanToUser(userId, planId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("response", "Plan has been selected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
