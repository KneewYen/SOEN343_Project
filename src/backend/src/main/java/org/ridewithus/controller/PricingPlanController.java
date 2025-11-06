package org.ridewithus.controller;

import java.util.List;

import org.ridewithus.domain.entity.PricingPlan;
import org.ridewithus.domain.services.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingPlanController {
    
    private PricingService pricingService;

    @GetMapping("/plans")
    public ResponseEntity<List<PricingPlan>> getAllPlans() {
        return ResponseEntity.ok(pricingService.getAllPlans());
    }
}
