package com.scb.location.rest.controller;

import com.scb.location.model.*;
import com.scb.location.service.rider.RiderLocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RiderLocationControllerTest {

    @InjectMocks
    private RiderLocationController riderLocationController;

    @Mock
    private RiderLocationService riderLocationService;

    @Test
    void testFindAll(){
        when(riderLocationService.findAll()).thenReturn(getListOfRiderLocationRecords(3));
        ResponseEntity<List<RiderLocationEntity>> responseEntity = riderLocationController.findAll();
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(3, responseEntity.getBody().size());
        assertEquals("rider_0", responseEntity.getBody().get(0).getRiderId());
        assertEquals("rider_1", responseEntity.getBody().get(1).getRiderId());
        assertEquals("rider_2", responseEntity.getBody().get(2).getRiderId());
    }

    @Test
    void testFindById(){
        when(riderLocationService.findById("rider_0")).thenReturn(getListOfRiderLocationRecords(1).get(0));
        ResponseEntity<RiderLocationEntity> responseEntity = riderLocationController.findById("rider_0");
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("rider_0", responseEntity.getBody().getRiderId());
    }

    @Test
    void testAddNewRider(){
        String rider = "rider_0";
        when(riderLocationService.addNewRiderLocationEntity(rider,-36.829, 174.896)).thenReturn(getListOfRiderResponseRecords(1).get(0));
        riderLocationController.addNewRider(rider,-36.829, 174.896);
        assertDoesNotThrow(
                () -> riderLocationController.addNewRider(rider,-36.829, 174.896));
    }

    @Test
    void testDeleteById(){
        String rider = "rider_0";
        doNothing().when(riderLocationService).deleteById(rider);
        riderLocationController.addNewRider(rider,-36.829, 174.896);
        assertDoesNotThrow(
                () -> riderLocationController.deleteById(rider));
    }

    @Test
    void testFindAvailableRiders(){
        Double longitude = 100.2;
        Double latitude = 12.2;
        Integer limit = 3;
        List<String> riderList = Arrays.asList("rider_1", "rider_2", "rider_3");
        RiderRequestEntity riderRequestEntity = new RiderRequestEntity(riderList);
        when(riderLocationService.findAvailableRiders(longitude, latitude, limit )).thenReturn(getListOfRiderResponseRecords(3));
        ResponseEntity<List<RiderResponseEntity>> responseEntity = riderLocationController.findAvailableRiders(longitude,latitude, limit);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(3, responseEntity.getBody().size());
        assertEquals("rider_0", responseEntity.getBody().get(0).getRiderId());
        assertEquals("rider_1", responseEntity.getBody().get(1).getRiderId());
        assertEquals("rider_2", responseEntity.getBody().get(2).getRiderId());
    }

    @Test
    void testFindAvailableRidersByBoxType(){
        Double longitude = 100.2;
        Double latitude = 12.2;
        Integer limit = 3;
        String foodBoxType = "LARGE";
        when(riderLocationService.findAvailableRidersByBoxType(longitude, latitude, foodBoxType, Boolean.TRUE, limit )).thenReturn(getListOfRiderResponseRecords(3));
        ResponseEntity<List<RiderResponseEntity>> responseEntity = riderLocationController.findAvailableRidersByBoxType(longitude,latitude, foodBoxType,Boolean.TRUE,limit);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(3, responseEntity.getBody().size());
        assertEquals("rider_0", responseEntity.getBody().get(0).getRiderId());
        assertEquals("rider_1", responseEntity.getBody().get(1).getRiderId());
        assertEquals("rider_2", responseEntity.getBody().get(2).getRiderId());
    }

    @Test
    void testFindAvailableExpressRiders(){
        Double longitude = 100.2;
        Double latitude = 12.2;
        Integer limit = 3;
        String foodBoxType = "LARGE";
        List<String> riderList = Arrays.asList("rider_1", "rider_2", "rider_3");
        RiderRequestEntity riderRequestEntity = new RiderRequestEntity(riderList);
        when(riderLocationService.findAvailableExpressRiders(longitude, latitude, limit )).thenReturn(getListOfRiderResponseRecords(3));
        ResponseEntity<List<RiderResponseEntity>> responseEntity = riderLocationController.findAvailableRidersByExpressRiders(longitude,latitude,limit);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(3, responseEntity.getBody().size());
        assertEquals("rider_0", responseEntity.getBody().get(0).getRiderId());
        assertEquals("rider_1", responseEntity.getBody().get(1).getRiderId());
        assertEquals("rider_2", responseEntity.getBody().get(2).getRiderId());
    }

    @Test
    void getRiderLocationTest(){
        RiderLocation riderLocation = RiderLocation.builder()
                .riderId("test-rider-id")
                .lon(102.23)
                .lat(12.2)
                .channel("api")
                .dateTime(LocalDateTime.now().toString())
                .phoneDateTime(LocalDateTime.now().toString())
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("lon", 102.23);
        request.addHeader("lat", 12.2);
        request.addHeader("batteryPercentage", 90);

        when(riderLocationService.postRiderLocation("test-rider-id", riderLocation)).thenReturn(riderLocation);

        ResponseEntity<RiderLocation> responseEntity = riderLocationController.getRiderLocation("test-rider-id", riderLocation, request);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

    }
    private List<RiderLocationEntity> getListOfRiderLocationRecords(int n) {
        List<RiderLocationEntity> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            RiderLocationEntity otpRecord = new RiderLocationEntity("rider_"+i,
                    new GeometryFactory().createPoint(new Coordinate(-36.829-i, 174.896-i)), 10.0, AvailabilityStatus.Active.name(),RiderStatus.AUTHORIZED.name(),false, false, "1","LARGE",false,false,false);
            list.add(otpRecord);
        }
        return list;
    }

    private List<RiderResponseEntity> getListOfRiderResponseRecords(int n) {
        List<RiderResponseEntity> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            RiderResponseEntity otpRecord = new RiderResponseEntity("rider_"+i,
                    12.23,100.23,
                    100.20, "Active",RiderStatus.AUTHORIZED.name(),false, false, "1");
            list.add(otpRecord);
        }
        return list;
    }

    @Test
    void testFindAvailablePointXRiders(){
        Double longitude = 100.2;
        Double latitude = 12.2;
        Integer limit = 3;
        List<String> riderList = Arrays.asList("rider_1", "rider_2", "rider_3");
        RiderRequestEntity riderRequestEntity = new RiderRequestEntity(riderList);
        when(riderLocationService.findAvailablePointxRiders(longitude, latitude, limit )).thenReturn(getListOfRiderResponseRecords(3));
        ResponseEntity<List<RiderResponseEntity>> responseEntity = riderLocationController.findAvailableRidersByPointXRiders(longitude,latitude,limit);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(3, responseEntity.getBody().size());
        assertEquals("rider_0", responseEntity.getBody().get(0).getRiderId());
        assertEquals("rider_1", responseEntity.getBody().get(1).getRiderId());
        assertEquals("rider_2", responseEntity.getBody().get(2).getRiderId());
    }
}
