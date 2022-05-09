package com.scb.location.rest.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.scb.location.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.service.rider.RiderLocationService;
import com.scb.location.service.zone.ZoneService;


@ExtendWith(MockitoExtension.class)
public class ZoneControllerTest {

    @InjectMocks
    private ZoneController zoneController;

    @Mock
    private ZoneService zoneService;

    @Mock
    private RiderLocationService riderLocationService;

    @Test
    void testFindAll(){
        List<Double> userLocation = new ArrayList<>();
        userLocation.add(-36.829);
        userLocation.add(174.896);
        Point expectedPoint = new GeometryFactory().createPoint(new Coordinate(-36.829, 174.896));
        when(zoneService.getMerchantZone(-36.829, 174.896)).thenReturn(getZoneRecord());
        ResponseEntity<ZoneEntity> responseEntity = zoneController.getMerchantZone(userLocation);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(1, responseEntity.getBody().getZoneId());
        assertEquals(10001, responseEntity.getBody().getPostalCode());
        assertEquals("province_0: zone_0", responseEntity.getBody().getZoneName());
        assertEquals(expectedPoint, responseEntity.getBody().getGeom());
    }

    @Test
    void testGetMerchantZoneException(){
        List<Double> userLocation = new ArrayList<>();
        userLocation.add(-36.829);

        InvalidInputException exception= assertThrows(InvalidInputException.class,
                () -> zoneController.getMerchantZone(userLocation));
        assertEquals("Invalid Merchant Location", exception.getMessage());
    }

    private ZoneEntity getZoneRecord() {
        return new ZoneEntity(1,10001, "zone_0",
                new GeometryFactory().createPoint(new Coordinate(-36.829, 174.896)), 10, 2, 50,
                new ProvinceEntity(1 ,"province_0"),1);
    }

    @Test
    void testGetAllZones() {
    	List<ZoneName> zoneList = new ArrayList<ZoneName>() ;
    	zoneList.add(new ZoneName(1, "Bangkok", "Bangkok: Khlong Toei", "Khlong Toei", 1));
    	zoneList.add(new ZoneName(2, "Bangkok", "Bangkok: Chatuchak" ,"Chatuchak", 1));
    	when(zoneService.getAllZones()).thenReturn(zoneList);
    	ResponseEntity<List<ZoneName>> responseAllZones = zoneController.getAllZones();
    	assertEquals(200, responseAllZones.getStatusCode().value());
    	assertEquals(2, responseAllZones.getBody().size());
    }

    @Test
    void getRiderActiveZone() {
    	RiderLocationEntity riderLocation = new RiderLocationEntity();
    	Coordinate c = new Coordinate(100.2, 100.2);
    	Point geom = new Point(c, new PrecisionModel(), 1);
    	riderLocation.setGeom(geom);
    	ZoneEntity zoneEntity = ZoneEntity.builder().zoneName("asd").zoneId(1).build();
    	when(riderLocationService.findById(anyString())).thenReturn(riderLocation);
    	when(zoneService.getMerchantZone(riderLocation.getGeom().getX()
				, riderLocation.getGeom().getY())).thenReturn(zoneEntity);
    	ResponseEntity<ZoneEntity> responseAllZones = zoneController.getRiderActiveZone("123124");
    	assertEquals(200, responseAllZones.getStatusCode().value());
    	assertNotNull(responseAllZones.getBody());
    }
    @Test
    void getAllZoneIdsByProvinceTest(){
        List<Integer> zoneIdList = Arrays.asList(1, 2);
        when(zoneService.getAllZoneIdByProvince(1)).thenReturn(zoneIdList);
        ResponseEntity<List<Integer>> responseAllZones = zoneController.getAllZoneIdsByProvince(1);
        assertEquals(200, responseAllZones.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(responseAllZones.getBody()).size());
    }

    @Test
    void getProvinceIdByZoneTest(){
        ProvinceDto provinceDto = new ProvinceDto(){
            public Integer getProvinceId(){
                return 1;
            }
            public String getName(){
                return "abc";
            }
        };
        when(zoneService.getProvinceIdByZoneId(1)).thenReturn(provinceDto);
        ResponseEntity<ProvinceDto> responseProvinceId = zoneController.getProvinceIdByZone(1);
        assertEquals(200, responseProvinceId.getStatusCode().value());
    }
}
