package com.scb.location.rest.controller;


import com.scb.location.model.RiderFoodBoxSize;
import com.scb.location.service.rider.RiderProfileUpdateService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
public class RiderProfileControllerTest {
    @Mock
    private RiderProfileUpdateService riderProfileUpdateService;

    @InjectMocks
    private RiderProfileController riderProfileController;

    @Test
    public void testUpdateFoodBoxSize(){
        RiderFoodBoxSize riderFoodBoxSize = RiderFoodBoxSize.builder()
                .foodBoxSize("LARGE")
                .riderId("riderId")
                .build();
        Mockito.when(riderProfileUpdateService.updateRiderFoodCartSize(Mockito.any())).thenReturn(riderFoodBoxSize);
        Object response = riderProfileController.updateFoodBoxSize(riderFoodBoxSize);
        Assertions.assertNotNull(response);
    }

    @Test
    public void testUpdateExpressRiders(){
        Mockito.doNothing().when(riderProfileUpdateService).updateRiderToExpress(Mockito.any(),Mockito.any());
        Object response = riderProfileController.updateRiderToExpressRider("riderId",false);
        Assertions.assertNotNull(response);
    }

    @Test
    public void testUpdatePointXRiders(){
        Mockito.doNothing().when(riderProfileUpdateService).updateRiderToPointX(Mockito.any(),Mockito.any());
        Object response = riderProfileController.updateRiderToPointXRider("riderId",false);
        Assertions.assertNotNull(response);
    }

    @Test
    public void testUpdateMartRiders(){
        Mockito.doNothing().when(riderProfileUpdateService).updateRiderToMart(Mockito.any(),Mockito.any());
        Object response = riderProfileController.updateRiderToMartRider("riderId",false);
        Assertions.assertNotNull(response);
    }
}
