package org.ridewithus.controller;

import org.ridewithus.domain.dto.BikeDTO;
import org.ridewithus.domain.dto.DockDTO;
import org.ridewithus.domain.dto.StationDTO;
import org.ridewithus.domain.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/station")
@CrossOrigin(originPatterns = "*")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/allStations")
    public List<StationDTO> getAllStations() {
        return stationService.getAllStations();
    }

    @GetMapping("/numberOfAvailableBikes/{stationId}")
    public int getNumberOfAvailableBikes(@PathVariable("stationId") Long stationId) throws Exception {
        return stationService.getBikesAvailable(stationId).size();
    }

    @GetMapping("/numberOfFreeDocks/{stationId}")
    public int getNumberOfFreeDocks(@PathVariable("stationId") Long stationId) throws Exception {
        return stationService.getFreeDocks(stationId).size();
    }

    @GetMapping("/availableBikes/{stationId}")
    public List<BikeDTO> getAvailableBikes(@PathVariable("stationId") Long stationId) throws Exception {
        return stationService.getBikesAvailable(stationId);
    }

    @GetMapping("/freeDocks/{stationId}")
    public List<DockDTO> getFreeDocks(@PathVariable("stationId") Long stationId) throws Exception {
        return stationService.getFreeDocks(stationId);
    }

}
