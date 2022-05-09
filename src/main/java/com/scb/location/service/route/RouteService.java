package com.scb.location.service.route;

import com.google.maps.errors.ApiException;
import com.scb.location.model.GsonResponse;

import java.io.IOException;

public interface RouteService {
    GsonResponse getRoute(Double longitudeFrom, Double latitudeFrom, Double longitudeTo, Double latitudeTo) throws InterruptedException, ApiException, IOException;

}
