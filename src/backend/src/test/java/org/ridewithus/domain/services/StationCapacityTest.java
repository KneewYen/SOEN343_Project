package org.ridewithus.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ridewithus.domain.dto.BikeDTO;
import org.ridewithus.domain.entity.*;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Station capacity check in StationService
 * Tests the station occupancy calculation for flex dollar eligibility
 */
@ExtendWith(MockitoExtension.class)
class StationCapacityTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private DockRepository dockRepository;

    @Spy
    @InjectMocks
    private StationService stationService;

    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station();
        station.setId(1L);
        station.setName("Test Station");
        station.setCapacity(10); // Total capacity of 10 bikes
    }

    /**
     * Test Case 1: Station at 20% capacity (2/10) should be below minimum
     * Expected: minimumCapacityReached returns true
     */
    @Test
    void testMinimumCapacityReached_At20Percent() throws Exception {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        // Mock getBikesAvailable to return 2 bikes
        List<BikeDTO> bikes = createBikeDTOList(2);
        doReturn(bikes).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertTrue(result, "20% capacity (2/10) should be below minimum (25%)");
    }

    /**
     * Test Case 2: Station at 30% capacity should NOT be below minimum
     * Expected: minimumCapacityReached returns false
     */
    @Test
    void testMinimumCapacityNotReached_At30Percent() throws Exception {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(3)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertFalse(result, "30% capacity should NOT be below minimum (25%)");
    }

    /**
     * Test Case 3: Station at 10% capacity (1/10) should be below minimum
     * Expected: minimumCapacityReached returns true
     */
    @Test
    void testMinimumCapacityReached_At10Percent() throws Exception {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(1)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertTrue(result, "10% capacity (1/10) should be below minimum (25%)");
    }

    /**
     * Test Case 4: Empty station (0% capacity) should be below minimum
     * Expected: minimumCapacityReached returns true
     */
    @Test
    void testMinimumCapacityReached_AtZeroPercent() throws Exception {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(Collections.emptyList()).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertTrue(result, "0% capacity (0/10) should be below minimum (25%)");
    }

    /**
     * Test Case 5: Station at 50% capacity should NOT be below minimum
     * Expected: minimumCapacityReached returns false
     */
    @Test
    void testMinimumCapacityNotReached_At50Percent() throws Exception {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(5)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertFalse(result, "50% capacity (5/10) should NOT be below minimum (25%)");
    }

    /**
     * Test Case 6: Full station (100% capacity) should NOT be below minimum
     * Expected: minimumCapacityReached returns false
     */
    @Test
    void testMinimumCapacityNotReached_At100Percent() throws Exception {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(10)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertFalse(result, "100% capacity (10/10) should NOT be below minimum (25%)");
    }

    /**
     * Test Case 7: Station not found should throw exception
     * Expected: Exception thrown
     */
    @Test
    void testMinimumCapacityReached_StationNotFound() {
        // Arrange
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> stationService.minimumCapacityReached(999L));

        assertEquals("Station does not exist", exception.getMessage());
    }

    /**
     * Test Case 8: Large capacity station at 24% should be below minimum
     * Expected: minimumCapacityReached returns true
     */
    @Test
    void testMinimumCapacityReached_LargeStation24Percent() throws Exception {
        // Arrange
        station.setCapacity(100);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(24)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertTrue(result, "24% capacity should be below minimum (25%)");
    }

    /**
     * Test Case 9: Boundary test - 2 bikes in 8-capacity station (25% exactly)
     * Expected: minimumCapacityReached returns false
     */
    @Test
    void testMinimumCapacityNotReached_Boundary25Percent() throws Exception {
        // Arrange
        station.setCapacity(8);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(2)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertFalse(result, "Exactly 25% capacity should NOT trigger flex dollar award");
    }

    /**
     * Test Case 10: Boundary test - Just below 25%
     * Expected: minimumCapacityReached returns true
     */
    @Test
    void testMinimumCapacityReached_JustBelow25Percent() throws Exception {
        // Arrange
        station.setCapacity(20);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doReturn(createBikeDTOList(4)).when(stationService).getBikesAvailable(1L);

        // Act
        boolean result = stationService.minimumCapacityReached(1L);

        // Assert
        assertTrue(result, "20% capacity should be below minimum (25%)");
    }

    // Helper methods
    private List<BikeDTO> createBikeDTOList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> BikeDTO.builder()
                        .id((long) i)
                        .type("REGULAR")
                        .status(BikeStatus.AVAILABLE)
                        .dockId((long) i)
                        .build())
                .collect(Collectors.toList());
    }
}

