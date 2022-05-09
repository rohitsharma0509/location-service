package com.scb.location.service.route;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.model.GsonResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class RouteServiceImplTest {

    @Mock
    private GoogleMapsDirectionsService directionsService;

    @InjectMocks
    private RouteServiceImpl routeService;



   @Test
    public void getRouteTestWithValidRoute() throws InterruptedException, ApiException, IOException {
        List<LatLng> result = new ArrayList<>();
        LatLng latLng = new LatLng(13.2, 100.2);
        result.add(latLng);
        when(directionsService.getDirections(anyString(), anyString())).thenReturn(result);
        GsonResponse gsonResponse = routeService.getRoute(100.23,12.2,100.23,12.2);
        assertEquals("LineString", gsonResponse.getType());
        assertEquals(1, gsonResponse.getCoordinates().size());
    }

    @Test
    public void getRouteTestWithNoRoute() throws InterruptedException, ApiException, IOException {
        when(directionsService.getDirections(anyString(), anyString())).thenReturn(new ArrayList<>());
        GsonResponse gsonResponse = routeService.getRoute(100.23,12.2,100.23,12.2);
        assertEquals("LineString", gsonResponse.getType());
        assertEquals(0, gsonResponse.getCoordinates().size());
    }

    @Test
    public void testGetLocationWhenNoRoutesExist() throws InterruptedException, ApiException, IOException {
        when(directionsService.getDirections(anyString(), anyString())).thenThrow(new IOException());
        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
                () -> routeService.getRoute(100.53,13.75, 100.423442,13.430346));
        assertEquals("No route exists", exception.getMessage());

    }
}


