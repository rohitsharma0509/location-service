package com.scb.location.service.route;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.scb.location.config.GoogleMapsConfig;
import com.scb.location.exception.NoResultFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GoogleMapsDirectionsService {


    private final String apiKey;
    @SneakyThrows
    @Autowired
    public GoogleMapsDirectionsService(GoogleMapsConfig googleMapsConfig) {
        this.apiKey = googleMapsConfig.getApiKey();
    }

    public List<LatLng> getDirections(String origin, String destination) throws InterruptedException, ApiException, IOException {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();
            DirectionsResult directionsResult = DirectionsApi.getDirections(context,origin,destination).await();
            DirectionsRoute[] routes = directionsResult.routes;
            if (routes.length == 0)
                throw new NoResultFoundException("No route exists");
            EncodedPolyline overviewPolyline = routes[0].overviewPolyline;
            List<LatLng> path = overviewPolyline.decodePath();
            return path;

    }

}

