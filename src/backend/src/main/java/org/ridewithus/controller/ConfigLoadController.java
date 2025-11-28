package org.ridewithus.controller;

import jakarta.persistence.EntityManager;
import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.entity.Dock;
import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.Dock.DockStatus;
import org.ridewithus.domain.services.DomainEventService;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/import")
public class ConfigLoadController {
    
    // autowire tells spring to find a bean with that name
    @Autowired 
    private BikeRepository bikeRepository;
    @Autowired
    private DockRepository dockRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DomainEventService eventService;


    @PostMapping("/core")
    @Transactional
    public String importOperators(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("core.json");


            JsonNode stationNodes = mapper.readTree(inputStream).get("stations");

            //System.out.println(stationNodes != null);

            if(stationNodes != null && stationNodes.isArray()){
                for(JsonNode stationNode: stationNodes){

                    // convert status to StationStatus enum
                    String stationStatusStr = stationNode.get("status").asText();
                    Station.StationStatus stationStatus = Station.StationStatus.valueOf(stationStatusStr.toUpperCase());

                    Station station = new Station(
                        stationNode.get("latitude").asDouble(),
                        stationNode.get("longitude").asDouble(),
                        stationNode.get("address").asText(),
                        stationNode.get("name").asText(),
                        stationStatus,
                        stationNode.get("capacity").asInt()
                    );
                    stationRepository.save(station);

                    System.out.println("suc");

                    // Get docks for a station now
                    if(stationNode.has("docks")){
                        for(JsonNode dockNode : stationNode.get("docks")){

                            // convert status to DockStatus enum
                            String status = dockNode.get("status").asText();
                            DockStatus dockStatus = DockStatus.valueOf(status.toUpperCase());

                            Dock dock = new Dock(
                                dockStatus,
                                station
                            );
                            dockRepository.save(dock);
                            
                            // check if a dock has a bike, then get that bike
                            if(dockNode.has("bike")){
                                JsonNode bikeNode = dockNode.get("bike");

                                //convert status to BikeStatus enum
                                String bikeStatusStr = bikeNode.get("status").asText();
                                BikeStatus bikeStatus = BikeStatus.valueOf(bikeStatusStr.toUpperCase());
                                
                                Bike bike = Bike.builder().status(bikeStatus).type(bikeNode.get("type").asText()).dock(dock).build();

                                bikeRepository.save(bike);
                                
                            }
                        }
                    }
                }
            }
            return "success";
        }
        catch(Exception e){
            System.err.println("An error occurred: " + e.getMessage());
            return "failure" ;
        }

    }

    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<Map<String, Object>> resetSystem(){
        Map<String, Object> response = new HashMap<>();

        try {
            List<Bike> badBikes = bikeRepository.findAll().stream()
                    .filter(b -> b.getStatus() == BikeStatus.ON_TRIP ||
                            b.getStatus() == BikeStatus.RESERVED)
                    .toList();

            if (!badBikes.isEmpty()) {
                response.put("success", false);
                response.put("message", "Some bikes are still ON_TRIP or RESERVED.");
                return ResponseEntity.badRequest().body(response);
            }

            bikeRepository.clearAllDockAssignments();
            entityManager.flush();
            entityManager.clear();

            ObjectMapper mapper = new ObjectMapper();
            InputStream file = getClass().getClassLoader().getResourceAsStream("core.json");
            JsonNode root = mapper.readTree(file);

            for (JsonNode stationNode : root.get("stations")) {

                Long stationId = stationNode.get("id").asLong();
                Station station = stationRepository.getReferenceById(stationId);

                station.setName(stationNode.get("name").asText());
                station.setStatus(Station.StationStatus.valueOf(stationNode.get("status").asText().toUpperCase()));
                station.setLatitude(stationNode.get("latitude").asDouble());
                station.setLongitude(stationNode.get("longitude").asDouble());
                station.setAddress(stationNode.get("address").asText());
                station.setCapacity(stationNode.get("capacity").asInt());
                stationRepository.save(station);

                for (JsonNode dockNode : stationNode.get("docks")) {

                    Long dockId = dockNode.get("id").asLong();
                    Dock dock = dockRepository.findById(dockId).orElseThrow();

                    dock.setStatus(Dock.DockStatus.valueOf(dockNode.get("status").asText().toUpperCase()));
                    dockRepository.save(dock);

                    if (dockNode.has("bike") && !dockNode.get("bike").isNull()) {

                        JsonNode bikeNode = dockNode.get("bike");
                        Bike bike = bikeRepository.findById(bikeNode.get("id").asLong()).orElseThrow();

                        bike.setType(bikeNode.get("type").asText());
                        bike.setStatus(BikeStatus.valueOf(bikeNode.get("status").asText().toUpperCase()));
                        bike.setDock(dock);
                        bikeRepository.save(bike);

                    }
                }
            }

            eventService.emitEvent("SYSTEM_RESET", "System has been reset.");

            response.put("success", true);
            response.put("message", "System reset successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Reset failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
