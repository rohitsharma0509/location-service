package com.scb.location.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.errors.ApiException;
import com.scb.location.model.GsonResponse;
import com.scb.location.service.route.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteControllerTest {
    @InjectMocks
    private RouteController routeController;

    @Mock
    private RouteService routeService;

    @Test
    public void testGetRoute() throws InterruptedException, ApiException, IOException {
        when(routeService.getRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(getLatLng());
        ResponseEntity<GsonResponse> responseEntity = routeController.getRoute(75.699274, 29.158769, 75.716419, 29.165224);
        assertEquals(200, responseEntity.getStatusCode().value());
    }

    private GsonResponse getLatLng() throws JsonProcessingException {
        String result = "{\n" +
                "    \"type\": \"LineString\",\n" +
                "    \"coordinates\": [\n" +
                "        [\n" +
                "            26.712840000000003,\n" +
                "            73.85501000000001\n" +
                "        ],\n" +
                "        [\n" +
                "            26.71786,\n" +
                "            73.86021000000001\n" +
                "        ],\n" +
                "        [\n" +
                "            26.7205,\n" +
                "            73.88108000000001\n" +
                "        ],\n" +
                "        [\n" +
                "            26.720720000000004,\n" +
                "            73.91422\n" +
                "        ],\n" +
                "        [\n" +
                "            26.725830000000002,\n" +
                "            73.92451000000001\n" +
                "        ]\n" +
                "    ]\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, GsonResponse.class);
    }
}

