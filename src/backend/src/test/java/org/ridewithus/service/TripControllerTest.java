package org.ridewithus.service;

import org.junit.jupiter.api.Test;
import org.ridewithus.controller.TripController;
import org.ridewithus.domain.dto.TripDTO;
import org.ridewithus.domain.services.PricingService;
import org.ridewithus.domain.services.TripService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @MockBean
    private PricingService pricingService;

    @Test
    void testGetAllTripsSuccess() throws Exception {

        TripDTO dto = TripDTO.builder()
                .tripId(1L)
                .startTime(LocalDateTime.now())
                .tripComplete(true)
                .userName("Ria")
                .build();

        Page<TripDTO> mockPage = new PageImpl<>(List.of(dto));

        when(tripService.getAllTrips(anyInt(), anyInt()))
                .thenReturn(mockPage);

        mockMvc.perform(
                        get("/api/trip/AllTrips")
                                .param("page", "0")
                                .param("size", "3")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.trips.content[0].tripId").value(1L))
                .andExpect(jsonPath("$.trips.content[0].userName").value("Ria"));
    }

    @Test
    void testGetAllTripsException() throws Exception {

        when(tripService.getAllTrips(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(
                        get("/api/trip/AllTrips")
                                .param("page", "0")
                                .param("size", "3")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    void testGetUserTripsSuccess() throws Exception {

        TripDTO dto = new TripDTO();
        List<TripDTO> trips = List.of(dto);

        when(tripService.getUserTrips(5L))
                .thenReturn(trips);

        mockMvc.perform(get("/api/trip/user/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.trips").isArray())
                .andExpect(jsonPath("$.trips.length()").value(1));
    }

    @Test
    void testGetUserTripsFailure() throws Exception {

        when(tripService.getUserTrips(5L))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/trip/user/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

}
