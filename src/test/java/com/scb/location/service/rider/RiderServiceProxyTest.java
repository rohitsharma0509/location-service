package com.scb.location.service.rider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.exception.ErrorResponse;
import com.scb.location.exception.RiderProfileServiceException;
import com.scb.location.model.RiderProfileDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RiderServiceProxyTest {

    private RiderServiceProxy riderServiceProxy;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;



    @BeforeEach
    public void setup(){
        riderServiceProxy = new RiderServiceProxy(restTemplate, "riderProfilePath",objectMapper);
    }

    @Test
    public void validateRiderTestWithValidResponse(){
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(getRiderProfile());
        boolean response = riderServiceProxy.validateRider("test-rider-id", "phone-number");
        assertNotNull(response);


    }
    @Test
    public void validateRiderTestWithException(){
        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        boolean response = riderServiceProxy.validateRider("test-rider-id", "phone-number");
        assertNotNull(response);
        assertFalse(response);


    }
    @Test
    public void getRiderProfileTest(){
        RiderProfileDetails riderProfileDetails = RiderProfileDetails.builder()
                .id("id")
                .riderId("test-rider-id")
                .availabilityStatus("available")
                .build();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(riderProfileDetails);
        RiderProfileDetails riderProfileDetail = riderServiceProxy.getRiderProfile("id");
        assertNotNull(riderProfileDetail);
    }

    @Test()
    public void getRiderProfileTestWithException() throws JsonProcessingException {
        String errorResponse = "{\"errorMessage\":\"Failure\"}";
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8))
                .when(restTemplate)
                .getForObject(anyString(), any());
        ErrorResponse error = new ErrorResponse(errorResponse);
        when(objectMapper.readValue(errorResponse, ErrorResponse.class)).thenReturn(error);
        RiderProfileServiceException ex = assertThrows(RiderProfileServiceException.class, () -> riderServiceProxy.getRiderProfile("id"));
        assertNotNull(ex);
    }
    private ResponseEntity<Object> getRiderProfile() {
        RiderProfileDetails riderProfileDetails = RiderProfileDetails.builder()
                .id("id")
                .riderId("test-rider-id")
                .availabilityStatus("available")
                .firstName("rider-name")
                .lastName("last-name")
                .phoneNumber("phone-number")
                .build();
        return ResponseEntity.ok().body(riderProfileDetails);

    }
}
