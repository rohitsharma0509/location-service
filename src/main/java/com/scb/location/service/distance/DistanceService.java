package com.scb.location.service.distance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.SubDistricts.AddressResponse;

public interface DistanceService {
    DistanceResponseEntity getDistance(Double longitudeFrom, Double latitudeFrom, Double longitudeTo, Double latitudeTo);

    AddressResponse getAddress(Double longitude, Double latitude) throws JsonProcessingException;

}
