package org.ridewithus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.ridewithus.domain.services.PricingService;
import org.ridewithus.domain.services.TripService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for Flex Dollar API endpoints
 * Tests the REST API layer for flex dollar feature
 */
@WebMvcTest(TripController.class)
class FlexDollarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripService tripService;

    @MockBean
    private PricingService pricingService;

    private Map<String, Object> tripResultWithFlexDollar;
    private Map<String, Object> tripResultNoFlexDollar;

    @BeforeEach
    void setUp() {
        // Result when flex dollar is awarded
        tripResultWithFlexDollar = new HashMap<>();
        tripResultWithFlexDollar.put("tripId", 1L);
        tripResultWithFlexDollar.put("flexDollarBalance", 5);
        tripResultWithFlexDollar.put("flexDollarAwarded", true);

        // Result when flex dollar is NOT awarded
        tripResultNoFlexDollar = new HashMap<>();
        tripResultNoFlexDollar.put("tripId", 2L);
        tripResultNoFlexDollar.put("flexDollarBalance", 3);
        tripResultNoFlexDollar.put("flexDollarAwarded", false);
    }

    /**
     * Test Case 1: API should return flex dollar data when awarded
     * Expected: Response includes flexDollarBalance and flexDollarAwarded = true
     */
    @Test
    void testEndTrip_ReturnsFlexDollarData_WhenAwarded() throws Exception {
        // Arrange
        when(tripService.endTrip(1L, 1L)).thenReturn(tripResultWithFlexDollar);

        // Act & Assert
        mockMvc.perform(put("/api/trip/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.flexDollarBalance").value(5))
                .andExpect(jsonPath("$.flexDollarAwarded").value(true))
                .andExpect(jsonPath("$.message").value("Trip ended successfully"));
    }

    /**
     * Test Case 2: API should return flex dollar data when NOT awarded
     * Expected: Response includes flexDollarBalance and flexDollarAwarded = false
     */
    @Test
    void testEndTrip_ReturnsFlexDollarData_WhenNotAwarded() throws Exception {
        // Arrange
        when(tripService.endTrip(2L, 2L)).thenReturn(tripResultNoFlexDollar);

        // Act & Assert
        mockMvc.perform(put("/api/trip/2/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.tripId").value(2))
                .andExpect(jsonPath("$.flexDollarBalance").value(3))
                .andExpect(jsonPath("$.flexDollarAwarded").value(false))
                .andExpect(jsonPath("$.message").value("Trip ended successfully"));
    }

    /**
     * Test Case 3: API should handle service exceptions gracefully
     * Expected: Returns error response with success = false
     */
    @Test
    void testEndTrip_HandlesExceptions() throws Exception {
        // Arrange
        when(tripService.endTrip(anyLong(), anyLong()))
                .thenThrow(new Exception("Station does not exist"));

        // Act & Assert
        mockMvc.perform(put("/api/trip/999/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Station does not exist"));
    }


    /**
     * Test Case 4: API should return all required fields in response
     * Expected: Response contains success, tripId, flexDollarBalance, flexDollarAwarded, message
     */
    @Test
    void testEndTrip_ReturnsAllRequiredFields() throws Exception {
        // Arrange
        when(tripService.endTrip(1L, 1L)).thenReturn(tripResultWithFlexDollar);

        // Act & Assert
        mockMvc.perform(put("/api/trip/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.tripId").exists())
                .andExpect(jsonPath("$.flexDollarBalance").exists())
                .andExpect(jsonPath("$.flexDollarAwarded").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test Case 5: API should accept valid path parameters
     * Expected: Endpoint accessible with tripId and stationId
     */
    @Test
    void testEndTrip_AcceptsValidPathParameters() throws Exception {
        // Arrange
        when(tripService.endTrip(123L, 456L)).thenReturn(tripResultWithFlexDollar);

        // Act & Assert
        mockMvc.perform(put("/api/trip/123/456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Test Case 6: Response structure should be consistent regardless of flex dollar award
     * Expected: Both awarded and not-awarded responses have same structure
     */
    @Test
    void testEndTrip_ConsistentResponseStructure() throws Exception {
        // Test with flex dollar awarded
        when(tripService.endTrip(1L, 1L)).thenReturn(tripResultWithFlexDollar);

        mockMvc.perform(put("/api/trip/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.flexDollarBalance").isNumber())
                .andExpect(jsonPath("$.flexDollarAwarded").isBoolean());

        // Test without flex dollar awarded
        when(tripService.endTrip(2L, 2L)).thenReturn(tripResultNoFlexDollar);

        mockMvc.perform(put("/api/trip/2/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.flexDollarBalance").isNumber())
                .andExpect(jsonPath("$.flexDollarAwarded").isBoolean());
    }
}

