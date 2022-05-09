package com.scb.location.rest.controller;

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
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = LocationServiceApplication.class)
public class ZoneControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    String zoneServicePath = "/api/zone";

    @Test
    void getZoneSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(zoneServicePath)
                .param("location", "100.577463,13.737896")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status, "Incorrect Response Status");
    }

    @Test
    void getZoneFailure() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(zoneServicePath)
                .param("location", "100.577463,3.137896")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), status, "Incorrect Response Status");
    }

}
