package com.scb.location.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.LocationServiceApplication;
import com.scb.location.model.RiderRequestEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocationServiceApplication.class)
@DirtiesContext
@EmbeddedKafka(topics = {"rider-location"})
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.auto-offset-reset=earliest"})
@ActiveProfiles("test")
public class RiderLocationControllerIntegrationTest {

  String locationServicePath = "/api/rider";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateRiderLocationEntity() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(locationServicePath + "/new")
            .param("riderId", "rider-test-1")
            .param("lon", "100.23")
            .param("lat", "12.23")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

  }

  @Test
  public void testDeleteById() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(locationServicePath + "/")
            .param("riderId", "rider-test-1")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status, "Incorrect Response Status");

  }

  @Test
  public void testDeleteByIdNotPresent() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(locationServicePath + "/")
            .param("riderId", "rider-test-not-present")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.BAD_REQUEST.value(), status, "Incorrect Response Status");

  }

  @Test
  public void testFindAll() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(locationServicePath + "/all")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status, "Incorrect Response Status");
  }

  @Test
  public void testGetByIdNotPresent() throws Exception {

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(locationServicePath + "/")
            .param("riderId", "rider-test-not-present")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.BAD_REQUEST.value(), status, "Incorrect Response Status");

  }

  @Test
  public void testGetNearByAvailableRidersInvalidInput() throws Exception {

    List<String> riderIdList = Arrays.asList("rider_0", "rider_1", "rider_2");
    RiderRequestEntity riderRequestEntity = new RiderRequestEntity();
    riderRequestEntity.setRiderList(riderIdList);
    String json = objectMapper.writeValueAsString(riderRequestEntity);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(locationServicePath + "/nearby-available-riders")
            .param("riderId", "rider-test-1")
            .param("lon", "100.23")
            .accept(MediaType.APPLICATION_JSON)
            .content(json))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.BAD_REQUEST.value(), status, "Incorrect Response Status");

  }

  @Test
  public void testGetNearByAvailableRiders() throws Exception {

    List<String> riderIdList = Arrays.asList("rider_0", "rider_1", "rider_2");
    RiderRequestEntity riderRequestEntity = new RiderRequestEntity();
    riderRequestEntity.setRiderList(riderIdList);
    String json = objectMapper.writeValueAsString(riderRequestEntity);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(locationServicePath + "/nearby-available-riders")
            .param("limit", "5")
            .param("longitude", "100.23")
            .param("latitude", "12.23")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(json))
            .andReturn();

    int status = result.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status, "Incorrect Response Status");

  }
}

