package com.scb.location.service.route;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.model.GsonResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RouteServiceImpl implements RouteService{

    @Autowired
    private GoogleMapsDirectionsService directionsService;
    @Override
    public GsonResponse getRoute(Double longitudeFrom, Double latitudeFrom, Double longitudeTo, Double latitudeTo) {

        String origin = latitudeFrom + "," + longitudeFrom;
        String destination = latitudeTo + "," + longitudeTo;

        List<LatLng> path = null;
        try {
            log.info("directions api call: route from {} to {}", origin, destination );
            path = directionsService.getDirections(origin, destination);
            log.info("directions api invocation successful");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage());
            throw new NoResultFoundException("No route exists");
        } catch (ApiException | IOException e) {
            log.error(e.getMessage());
            throw new NoResultFoundException("No route exists");
        }
        List<List<Double>> coordinates = new ArrayList<>();
        for(LatLng coordinate : path){
            List<Double> point = new ArrayList<>();
            point.add(coordinate.lat);
            point.add(coordinate.lng);

            coordinates.add(point);
        }

        return GsonResponse.builder()
                .type("LineString")
                .coordinates(coordinates)
                .build();
    }

}
