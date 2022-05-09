package com.scb.location.rest.controller.mock.server;

import com.scb.location.LocationServiceApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@AutoConfigureMockMvc
@SpringBootTest(classes = LocationServiceApplication.class)
@ActiveProfiles("local")
public class DistanceControllerIntegrationTest extends AbstractRestApiIntegrationTest{
    @Autowired
    private MockMvc mockMvc;

    String distanceServicePath = "/api/distance";

    @Test
    void getDistanceSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(distanceServicePath)
                .param("longitudeFrom","100.537953" )
                .param("latitudeFrom","13.753007" )
                .param("longitudeTo","100.23" )
                .param("latitudeTo","13.54" )
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status, "Incorrect Response Status");
    }

    @Test
    void getDistanceFailure() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(distanceServicePath)
                .param("longitudeFrom","100.537953" )
                .param("latitudeFrom","13.753007" )
                .param("longitudeTo","100.392120" )
                .param("latitudeTo","13.268905" )
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), status, "Incorrect Response Status");
    }


}
