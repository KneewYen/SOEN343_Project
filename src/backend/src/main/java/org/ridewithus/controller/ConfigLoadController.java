package org.ridewithus.controller;

import org.ridewithus.domain.entity.Bike;
import org.ridewithus.domain.entity.BikeStatus;
import org.ridewithus.domain.entity.Dock;
import org.ridewithus.domain.entity.Station;
import org.ridewithus.domain.entity.Dock.DockStatus;
import org.ridewithus.infrastructure.repository.BikeRepository;
import org.ridewithus.infrastructure.repository.DockRepository;
import org.ridewithus.infrastructure.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.io.InputStream;

@RestController
@RequestMapping("/import")
public class ConfigLoadController {
    
    // autowire tells spring to find a bean with that name
    @Autowired 
    private BikeRepository bikeRepository;
    @Autowired
    private DockRepository dockRepository;
    @Autowired
    private StationRepository stationRepository;

    @PostMapping("/core")
    @Transactional
    public String importOperators(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("add_station.json");


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
}
