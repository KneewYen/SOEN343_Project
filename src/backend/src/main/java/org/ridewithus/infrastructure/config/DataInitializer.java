package org.ridewithus.infrastructure.config;

import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.entity.Dock;
import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.ridewithus.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Initializer
 * Populates the H2 database with test data for development and testing
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StationRepository stationRepository;
    
    @Autowired
    private DockRepository dockRepository;
    
    @Autowired
    private BikeRepository bikeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            //initializeTestData();
        }
    }

    private void initializeTestData() {
        System.out.println("Initializing test data...");

        // Create test users
        createTestUsers();
        
        // Create test stations
        createTestStations();
        
        // Create test bikes and docks
        createTestBikesAndDocks();

        System.out.println("Test data initialization completed!");
    }

    private void createTestUsers() {
        // Test operator user
        User operator = new User();
        operator.setFullName("Test Operator");
        operator.setUserName("jonny");
        operator.setEmail("jonathan2.frantzeskos@gmail.com");
        operator.setAddress("123 Maple Street");
        operator.setRole("operator");
        operator.setPassword(hashPassword("Password123"));
        userRepository.save(operator);

        // Test rider user
        User rider = new User();
        rider.setFullName("Test Rider");
        rider.setUserName("rider1");
        rider.setEmail("rider@example.com");
        rider.setAddress("456 Oak Avenue");
        rider.setRole("rider");
        rider.setPassword(hashPassword("Password123"));
        userRepository.save(rider);

        System.out.println("Created test users: operator and rider");
    }

    private void createTestStations() {
        // Station 1 - Concordia (from core.json)
        Station station1 = new Station();
        station1.setName("Concordia");
        station1.setAddress("1550 De Maisonneuve Blvd W");
        station1.setLatitude(45.496712);
        station1.setLongitude(-73.577934);
        station1.setCapacity(10);
        station1.setCount(7); // 7 occupied docks from core.json
        station1.setStatus(Station.StationStatus.ACTIVE);
        stationRepository.save(station1);

        // Station 2 - McGill (from core.json)
        Station station2 = new Station();
        station2.setName("McGill");
        station2.setAddress("845 Sherbrooke St W, Montreal, Quebec H3A 0G4");
        station2.setLatitude(45.5060);
        station2.setLongitude(-73.5783);
        station2.setCapacity(10);
        station2.setCount(7); // 7 occupied docks from core.json
        station2.setStatus(Station.StationStatus.ACTIVE);
        stationRepository.save(station2);

        System.out.println("Created test stations: Concordia, McGill");
    }

    private void createTestBikesAndDocks() {
        List<Station> stations = stationRepository.findAll();
        
        // Define dock configurations for each station (matching core.json)
        // Format: [bikeType, dockStatus] - null bikeType means empty dock
        String[][][] stationConfigs = {
            { // Concordia station
                {"e-bike", "occupied"},
                {null, "empty"},
                {"e-bike", "occupied"},
                {"e-bike", "occupied"},
                {"standard", "occupied"},
                {"standard", "occupied"},
                {"standard", "occupied"},
                {null, "out_of_service"},
                {null, "empty"},
                {null, "empty"}
            },
            { // McGill station
                {"e-bike", "occupied"},
                {null, "empty"},
                {"e-bike", "occupied"},
                {"e-bike", "occupied"},
                {"standard", "occupied"},
                {"standard", "occupied"},
                {"standard", "occupied"},
                {null, "out_of_service"},
                {null, "empty"},
                {null, "empty"}
            }
        };
        
        for (int stationIdx = 0; stationIdx < stations.size(); stationIdx++) {
            Station station = stations.get(stationIdx);
            String[][] dockConfig = stationConfigs[stationIdx];
            
            for (int dockIdx = 0; dockIdx < dockConfig.length; dockIdx++) {
                String bikeType = dockConfig[dockIdx][0];
                String dockStatusStr = dockConfig[dockIdx][1];
                
                // Create dock
                Dock dock = new Dock();
                dock.setStation(station);
                dock.setStatus(Dock.DockStatus.valueOf(dockStatusStr.toUpperCase()));
                dockRepository.save(dock);
                
                // Create bike if dock is occupied
                if (bikeType != null && "occupied".equals(dockStatusStr)) {
                    Bike bike = new Bike();
                    bike.setType(bikeType);
                    bike.setStatus(BikeStatus.AVAILABLE);
                    bike.setDock(dock);
                    bikeRepository.save(bike);
                }
            }
        }

        System.out.println("Created test bikes and docks matching core.json structure");
    }

    /**
     * Hash password using SHA-256 (same method as AuthenticationService)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
