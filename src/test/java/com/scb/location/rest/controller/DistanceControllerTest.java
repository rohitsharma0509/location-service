package com.scb.location.rest.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.scb.location.model.AvailabilityStatus;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.RiderLocationEntity;
import com.scb.location.model.RiderStatus;
import com.scb.location.model.SubDistricts.AddressResponse;
import com.scb.location.service.distance.DistanceService;
import com.scb.location.service.rider.RiderLocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DistanceControllerTest {

    private static final String RIDER_ID = "rider_0";

    @InjectMocks
    private DistanceController distanceController;

    @Mock
    private DistanceService distanceService;

    @Mock
    private RiderLocationService riderLocationService;

    @Test
    public void testGetDistance(){
        when(distanceService.getDistance(100.53,13.75,100.23,13.54)).
                thenReturn(getDistanceResponseEntity());

        ResponseEntity<DistanceResponseEntity> responseEntity =  distanceController.getDistance(100.53,13.75,
                100.23,13.54);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(62551.0, Objects.requireNonNull(responseEntity.getBody()).getDistance());
        assertEquals(3372.0, responseEntity.getBody().getDuration());
    }

    @Test
    public void testGetAddress() throws JsonProcessingException {
        when(distanceService.getAddress(100.53,13.75)).
                thenReturn(getAddressResponseEntity());

        ResponseEntity<AddressResponse> responseEntity =  distanceController.getAddress(100.53,13.75);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody().getSubDistrict());
    }

    @Test
    public void testGetDistanceFromRidersCurrentLocation() {
        when(riderLocationService.findById(RIDER_ID)).thenReturn(getRiderLocation());
        when(distanceService.getDistance(100.53,13.75,100.23,13.54)).
                thenReturn(getDistanceResponseEntity());
        ResponseEntity<DistanceResponseEntity> responseEntity = distanceController.getDistanceFromRidersCurrentLocation(RIDER_ID, 100.23,13.54);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(62551.0, Objects.requireNonNull(responseEntity.getBody()).getDistance());
        assertEquals(3372.0, responseEntity.getBody().getDuration());
    }

    private RiderLocationEntity getRiderLocation() {
        return new RiderLocationEntity(RIDER_ID,
                new GeometryFactory().createPoint(new Coordinate(100.53, 13.75)), 10.0, AvailabilityStatus.Active.name(), RiderStatus.AUTHORIZED.name(),false, false, "1","LARGE",false,false,false);
    }

    private AddressResponse getAddressResponseEntity() {
        return AddressResponse.builder()
                .subDistrict("sub-district")
                .build();
    }

    private DistanceResponseEntity getDistanceResponseEntity() {
        return DistanceResponseEntity.
                builder().
                longitudeFrom(100.53).
                latitudeFrom(13.75).
                longitudeTo(100.23).
                latitudeTo(13.54).
                distance(62551.0).
                duration(3372.0).
                build();
    }
}
