package com.scb.location.rest.controller;

import com.scb.location.model.RiderLocation;
import com.scb.location.model.RiderLocationEntity;
import com.scb.location.model.RiderRequestEntity;
import com.scb.location.model.RiderResponseEntity;
import com.scb.location.service.rider.RiderLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/rider")
public class RiderLocationController {

    @Autowired
    private RiderLocationService riderLocationService;

    public ResponseEntity<List<RiderLocationEntity>> findAll() {
        return ResponseEntity.ok(riderLocationService.findAll());
    }

    @GetMapping(value = "/{riderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RiderLocationEntity> findById(@PathVariable("riderId") String riderId) {
        log.info("fetching location for rider: {}", riderId);
        return ResponseEntity.ok(riderLocationService.findById(riderId));

    }

    @PostMapping(value = "/{riderId}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RiderLocation> getRiderLocation(@PathVariable("riderId") String riderId, @RequestBody RiderLocation riderLocation,
                                                          HttpServletRequest request) {
        log.info("posting location for rider: {}", riderId);
        log.info("new location coordinates : {},{}", riderLocation.getLat(), riderLocation.getLon());
        String latitude = request.getHeader("lat");
        String longitude = request.getHeader("lon");
        String batteryPercentage = request.getHeader("batteryPercentage");
        log.info("rider Id {} latitude {} longitude {} batteryPercentage {}", riderId, latitude, longitude, batteryPercentage);
        return ResponseEntity.ok(riderLocationService.postRiderLocation(riderId, riderLocation));

    }

    @GetMapping(value = "/nearby-available-riders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RiderResponseEntity>> findAvailableRiders(
            @RequestParam("longitude") Double longitude, @RequestParam("latitude") Double latitude,
            @RequestParam("limit") Integer limit) {
        log.info("finding {} nearby riders for {}, {}", limit, longitude, latitude);
        return ResponseEntity.ok(riderLocationService.findAvailableRiders(longitude, latitude, limit));
    }

    @GetMapping(value = "/nearby-available-riders-boxtype", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RiderResponseEntity>> findAvailableRidersByBoxType(
            @RequestParam("longitude") Double longitude, @RequestParam("latitude") Double latitude,
            @RequestParam("foodBoxType") String foodBoxType,
            @RequestParam("isMartRider") Boolean isMartRider,
            @RequestParam("limit") Integer limit) {
        log.info("finding {} nearby riders by box type {} for {}, {}", limit, foodBoxType, longitude, latitude);
        return ResponseEntity.ok(riderLocationService.findAvailableRidersByBoxType(longitude, latitude, foodBoxType, isMartRider, limit));
    }

    @GetMapping(value = "/nearby-available-express-riders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RiderResponseEntity>> findAvailableRidersByExpressRiders(
            @RequestParam("longitude") Double longitude, @RequestParam("latitude") Double latitude, @RequestParam("limit") Integer limit) {
        log.info("finding near by express riders  {} {} {}", limit, longitude, latitude);
        return ResponseEntity.ok(riderLocationService.findAvailableExpressRiders(longitude, latitude, limit));
    }

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addNewRider(@RequestParam("riderId") String riderId, @RequestParam("lon") Double lon,
                                      @RequestParam("lat") Double lat) {
        return new ResponseEntity<>(riderLocationService.addNewRiderLocationEntity(riderId, lon, lat), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/")
    public void deleteById(@RequestParam("riderId") String riderId) {
        riderLocationService.deleteById(riderId);
    }
    
    @GetMapping(value = "/nearby-available-pointx-riders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RiderResponseEntity>> findAvailableRidersByPointXRiders(
            @RequestParam("longitude") Double longitude, @RequestParam("latitude") Double latitude, @RequestParam("limit") Integer limit) {
        log.info("finding near by pointx riders  {} {} {}", limit, longitude, latitude);
        return ResponseEntity.ok(riderLocationService.findAvailablePointxRiders(longitude, latitude, limit));
    }



}
