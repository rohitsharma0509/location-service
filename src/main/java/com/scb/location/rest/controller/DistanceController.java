package com.scb.location.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.RiderLocationEntity;
import com.scb.location.model.SubDistricts.AddressResponse;
import com.scb.location.service.distance.DistanceService;
import com.scb.location.service.rider.RiderLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class DistanceController {

    @Autowired
    private DistanceService distanceService;

    @Autowired
    private RiderLocationService riderLocationService;

    @GetMapping (value = "/distance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DistanceResponseEntity> getDistance(@RequestParam("longitudeFrom") Double longitudeFrom,
                                                              @RequestParam("latitudeFrom") Double latitudeFrom, @RequestParam("longitudeTo") Double longitudeTo,
                                                              @RequestParam("latitudeTo") Double latitudeTo) {
        log.info("Fetching distance for {},{} and {},{}", latitudeFrom, longitudeFrom, latitudeTo, longitudeTo);
        return ResponseEntity.ok(distanceService.getDistance(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo));

    }

    @GetMapping (value = "/address", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressResponse> getAddress(@RequestParam("longitude") Double longitude,
                                                           @RequestParam("latitude") Double latitude) throws JsonProcessingException {
        log.info("Fetching address for {}, {}", latitude, longitude);
        return ResponseEntity.ok(distanceService.getAddress(longitude, latitude));

    }

    @GetMapping (value = "/distance/{riderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DistanceResponseEntity> getDistanceFromRidersCurrentLocation(
            @PathVariable("riderId") String riderId,
            @RequestParam("longitudeTo") Double longitudeTo,
            @RequestParam("latitudeTo") Double latitudeTo) {
        log.info("Fetching distance for riderId {} and {},{}", riderId, latitudeTo, longitudeTo);
        RiderLocationEntity riderLocation = riderLocationService.findById(riderId);
        Double riderLong = riderLocation.getGeom().getX();
        Double riderLat =  riderLocation.getGeom().getY();
        return ResponseEntity.ok(distanceService.getDistance(riderLong, riderLat, longitudeTo, latitudeTo));
    }

}
