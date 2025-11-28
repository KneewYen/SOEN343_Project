package org.ridewithus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ridewithus.controller.BillingController;
import org.ridewithus.domain.dto.BillingDTO;
import org.ridewithus.domain.dto.TripDTO;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.BillingRepository;
import org.ridewithus.infrastructure.repository.TripRepository;
import org.ridewithus.domain.services.TripService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class HistoryTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private BillingController billingController;

    @InjectMocks
    private TripService tripService;

    private User user;
    private Bike bike;
    private Trip trip1;
    private Trip trip2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUserName("testRider");
        user.setEmail("rider@test.com");
        user.setFlexDollars(0);

        bike = new Bike();
        bike.setId(1L);
        bike.setType("standard");
        bike.setStatus(BikeStatus.AVAILABLE);


        trip1 = Trip.builder()
                .tripId(1L)
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().minusHours(1))
                .tripComplete(true)
                .bike(bike)
                .user(user)
                .build();

        trip2 = Trip.builder()
                .tripId(2L)
                .startTime(LocalDateTime.now().minusHours(3))
                .endTime(LocalDateTime.now().minusHours(2))
                .tripComplete(true)
                .bike(bike)
                .user(user)
                .build();
    }

    @Test
    void getAllTrips_success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("endTime").descending());
        Page<Trip> pageResult = new PageImpl<>(List.of(trip1, trip2), pageable, 2);

        when(tripRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        Page<TripDTO> result = tripService.getAllTrips(0, 10);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTripId()).isEqualTo(1L);
        assertThat(result.getContent().get(1).getTripId()).isEqualTo(2L);
    }

    @Test
    void getUserTrips_success() {
        Long userId = 100L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("endTime").descending());
        Page<Trip> pageResult = new PageImpl<>(List.of(trip1), pageable, 1);

        when(tripRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(pageResult);

        Page<TripDTO> result = tripService.getUserTrips(userId, 0, 5);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTripId()).isEqualTo(1L);
    }

    @Test
    void getBillingByTripId_success() {
        // Arrange
        Long tripId = 10L;

        Trip trip = new Trip();
        trip.setTripId(tripId);

        Charge charge1 = Charge.builder().name("Usage Fee").description("30 min ride").cost(5.0).build();
        Charge charge2 = Charge.builder().name("Service Fee").description("Maintenance").cost(2.0).build();

        Billing billing = Billing.builder()
                .billingId(1L)
                .trip(trip)
                .charges(Arrays.asList(charge1, charge2))
                .flexDollarDiscount(0)
                .finalAmount(0)
                .build();

        when(billingRepository.findByTrip_TripId(tripId)).thenReturn(Optional.of(billing));

        // Act
        ResponseEntity<Map<String, Object>> response = billingController.getBillingByTripId(tripId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));

        BillingDTO dto = (BillingDTO) body.get("billing");

        assertEquals(1L, dto.getBillingId());
        assertEquals(10L, dto.getTripId());
        assertEquals(2, dto.getCharges().size());
        assertEquals(7.0, dto.getTotalAmount());
        assertEquals(7, dto.getFinalAmount()); // rounded
    }

    @Test
    void getBillingByTripId_notFound() {
        // Arrange
        Long tripId = 99L;
        when(billingRepository.findByTrip_TripId(tripId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = billingController.getBillingByTripId(tripId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();

        assertFalse((Boolean) body.get("success"));
        assertEquals("Billing not found for this trip", body.get("message"));
    }

    @Test
    void getBillingByTripId_exceptionThrown() {
        // Arrange
        Long tripId = 5L;
        when(billingRepository.findByTrip_TripId(tripId))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        ResponseEntity<Map<String, Object>> response = billingController.getBillingByTripId(tripId);

        // Assert
        assertEquals(400, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();

        assertFalse((Boolean) body.get("success"));
        assertEquals("DB error", body.get("message"));
    }
}
