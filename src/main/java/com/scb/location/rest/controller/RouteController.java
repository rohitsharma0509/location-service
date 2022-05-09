package com.scb.location.rest.controller;

import com.google.maps.errors.ApiException;
import com.scb.location.model.GsonResponse;
import com.scb.location.service.route.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@Slf4j
@RestController
@RequestMapping(value = "/api")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping(value = "/route", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GsonResponse> getRoute(@RequestParam("longitudeFrom") Double longitudeFrom,
                                                 @RequestParam("latitudeFrom") Double latitudeFrom,
                                                 @RequestParam("longitudeTo") Double longitudeTo,
                                                 @RequestParam("latitudeTo") Double latitudeTo) throws InterruptedException, ApiException, IOException {
        log.info("Fetching route from {},{} to {},{}", latitudeFrom, longitudeFrom, latitudeTo, longitudeTo);
        return ResponseEntity.ok(routeService.getRoute(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo));

    }
}
