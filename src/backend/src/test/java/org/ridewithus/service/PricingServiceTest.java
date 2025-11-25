package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ridewithus.domain.dto.BillingDTO;
import org.ridewithus.domain.dto.ChargeDTO;
import org.ridewithus.domain.entity.*;
import org.ridewithus.domain.services.PricingService;
import org.ridewithus.infrastructure.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Set strictness to LENIENT for complex logic involving Strategy/Decorator patterns
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private BikeRepository bikeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PricingPlanRepository pricingPlanRepository;
    @Mock
    private BillingRepository billingRepository;
    @Mock
    private ChargeRepository chargeRepository;


    @InjectMocks
    private PricingService pricingService;


    private User testUser;
    private Trip testTrip;
    private Bike standardBike;
    private Bike eBike;
    private PricingPlan standardPlan;
    private PricingPlan distancePlan;
    private Station startStation;
    private Station endStation;

    @BeforeEach
    void setup() {
        // 1. Pricing Plans
        standardPlan = new PricingPlan();
        standardPlan.setPricingPlanId(1L);
        standardPlan.setName("Standard plan");

        distancePlan = new PricingPlan();
        distancePlan.setPricingPlanId(2L);
        distancePlan.setName("Distance plan");

        //Concordia
        startStation = mock(Station.class);
        when(startStation.getLatitude()).thenReturn(45.496712);
        when(startStation.getLongitude()).thenReturn(-73.577934);

        //Mcgill
        endStation = mock(Station.class);
        when(endStation.getLatitude()).thenReturn(45.506);
        when(endStation.getLongitude()).thenReturn(-73.5783);

        // 2. User
        testUser = new User();
        testUser.setId(100L);
        testUser.setPricingPlan(standardPlan); // Default plan for setup

        // 3. Bikes
        standardBike = new Bike();
        standardBike.setId(1L);
        standardBike.setType("standard");

        eBike = new Bike();
        eBike.setId(2L);
        eBike.setType("e-bike");

        // 4. Trip
        testTrip = new Trip();
        testTrip.setTripId(500L);
        testTrip.setUser(testUser);
        testTrip.setBike(standardBike);
        testTrip.setStartTime(LocalDateTime.now().minusMinutes(30));
        testTrip.setEndTime(LocalDateTime.now());

        testTrip.setStartStation(startStation);
        testTrip.setEndStation(endStation);

        // Reset mocks before each test
        reset(tripRepository, userRepository, pricingPlanRepository, billingRepository, chargeRepository);
    }

    // -------------------------------------------------------------------------
    //                              calculatePricingPlan Tests
    // -------------------------------------------------------------------------

    //  Standard Plan Calculation (Base Rate)

    @Test
    void calculatePricingPlan_StandardPlan_NewBilling() {
        // Setup user/trip to use Standard plan and standard bike
        testUser.setPricingPlan(standardPlan);
        testTrip.setBike(standardBike);

        // Stub findTrip and check for existing billing
        when(tripRepository.findByTripId(500L)).thenReturn(testTrip);
        when(billingRepository.findByTrip(testTrip)).thenReturn(Optional.empty());

        // Stub save for new billing and charges
        Billing newBilling = Billing.builder().billingId(10L).trip(testTrip).charges(new ArrayList<>()).build();
        when(billingRepository.save(any(Billing.class))).thenReturn(newBilling);
        when(chargeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        BillingDTO result = pricingService.calculatePricingPlan(500L);

        // Assertions
        assertNotNull(result);
        assertEquals(10L, result.getBillingId());
        assertEquals(1, result.getCharges().size(), "Should have exactly one charge (Base Rate)");

        ChargeDTO baseCharge = result.getCharges().get(0);
        assertEquals("Standard plan", baseCharge.getName());
        assertTrue(baseCharge.getCost() > 0, "Base rate cost should be calculated");

        assertTrue(result.getTotalAmount() > 0);

        verify(billingRepository, times(2)).save(any(Billing.class));
        verify(chargeRepository).saveAll(anyList());
    }

    // Distance Plan Calculation

    @Test
    void calculatePricingPlan_DistancePlan_NewBilling() {
        // Setup user/trip to use Distance plan and standard bike
        testUser.setPricingPlan(distancePlan);
        testTrip.setBike(standardBike);

        // Stub findTrip and check for existing billing
        when(tripRepository.findByTripId(500L)).thenReturn(testTrip);
        when(billingRepository.findByTrip(testTrip)).thenReturn(Optional.empty());

        // Stub save for new billing and charges
        Billing newBilling = Billing.builder().billingId(10L).trip(testTrip).charges(new ArrayList<>()).build();
        when(billingRepository.save(any(Billing.class))).thenReturn(newBilling);
        when(chargeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        BillingDTO result = pricingService.calculatePricingPlan(500L);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getCharges().size(), "Should have exactly one charge (Distance Rate)");

        ChargeDTO distanceCharge = result.getCharges().get(0);
        assertEquals("Distance plan", distanceCharge.getName());
        assertTrue(distanceCharge.getCost() > 0, "Distance rate cost should be calculated");
    }

    // E-Bike Surcharge Test (Decorator Pattern)

    @Test
    void calculatePricingPlan_StandardPlan_WithEbikeSurcharge() {
        // Setup user/trip to use Standard plan and E-bike
        testUser.setPricingPlan(standardPlan);
        testTrip.setBike(eBike);

        // Stub findTrip and check for existing billing
        when(tripRepository.findByTripId(500L)).thenReturn(testTrip);
        when(billingRepository.findByTrip(testTrip)).thenReturn(Optional.empty());

        // Stub save for new billing and charges
        Billing newBilling = Billing.builder().billingId(10L).trip(testTrip).charges(new ArrayList<>()).build();
        when(billingRepository.save(any(Billing.class))).thenReturn(newBilling);
        when(chargeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        BillingDTO result = pricingService.calculatePricingPlan(500L);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getCharges().size(), "Should have two charges (Base Rate + E-bike Surcharge)");

        ChargeDTO baseCharge = result.getCharges().get(0);
        assertEquals("Standard plan", baseCharge.getName());

        ChargeDTO surcharge = result.getCharges().get(1);
        assertEquals("Surcharge", surcharge.getName());
        assertTrue(surcharge.getCost() > 0, "Surcharge cost should be calculated");

        assertTrue(result.getTotalAmount() > baseCharge.getCost(), "Total amount should include surcharge");
    }

    // Existing Billing Record

    @Test
    void calculatePricingPlan_ExistingBilling() {
        // Setup an existing Billing record
        Billing existingBilling = Billing.builder()
                .billingId(20L)
                .trip(testTrip)
                .charges(List.of(Charge.builder().cost(5.0).build())) // Mock an existing charge
                .build();

        // Stub findTrip and return existing billing
        when(tripRepository.findByTripId(500L)).thenReturn(testTrip);
        when(billingRepository.findByTrip(testTrip)).thenReturn(Optional.of(existingBilling));

        // Execute
        BillingDTO result = pricingService.calculatePricingPlan(500L);

        // Assertions
        assertNotNull(result);
        assertEquals(20L, result.getBillingId(), "Should use the existing billing ID");

        // Verification
        verify(billingRepository, never()).save(any(Billing.class));
        verify(chargeRepository, never()).saveAll(anyList());
    }

    // -------------------------------------------------------------------------
    //                              assignPlanToUser Tests
    // -------------------------------------------------------------------------

    // Assign Plan Success

    @Test
    void assignPlanToUser_success() throws Exception {
        // New plan to assign
        PricingPlan newPlan = new PricingPlan();
        newPlan.setPricingPlanId(3L);
        newPlan.setName("Standard Plan");

        // Stub repository calls
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pricingPlanRepository.findById(anyLong())).thenReturn(Optional.of(newPlan));

        // Execute
        pricingService.assignPlanToUser(100L, 3L);

        // Assertions (Check state change on the test user object)
        assertEquals(newPlan, testUser.getPricingPlan(), "The user object's pricing plan should be updated.");

        // Verification (Ensure save was called)
        verify(userRepository).save(testUser);
    }

   // Assign Plan User Not Found

    @Test
    void assignPlanToUser_UserNotFound() {
        // Stub repository call to return empty Optional
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute and Assert Exception
        Exception exception = assertThrows(Exception.class, () -> {
            pricingService.assignPlanToUser(999L, 1L);
        });

        assertEquals("User not found", exception.getMessage());

        // Verification (Ensure save was NOT called)
        verify(userRepository, never()).save(any(User.class));
        verify(pricingPlanRepository, never()).findById(anyLong());
    }
}

