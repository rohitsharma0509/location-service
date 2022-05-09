package com.scb.location.rest.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.model.ZoneConfig;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Disabled
public class ZoneConfigControllerIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	String baseURL = "/api/zone/config";
	
	@Test
	void test_GetAllZoneConfig() throws Exception {
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/0")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.OK.value(), getResult.getResponse().getStatus());
		assertNotNull(getResult.getResponse());
		
		ZoneConfig[] zoneConfig = objectMapper.readValue(getResult.getResponse().getContentAsString(), ZoneConfig[].class);
		assertNotNull(zoneConfig);
	}
	
	@Test
	void test_GetZoneConfig() throws Exception {
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get(baseURL + "/1")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.OK.value(), getResult.getResponse().getStatus());
		assertNotNull(getResult.getResponse());
		
		ZoneConfig[] zoneConfig = objectMapper.readValue(getResult.getResponse().getContentAsString(), ZoneConfig[].class);
		assertNotNull(zoneConfig);
	}
	
	@Test
	void test_UpdateAllZoneConfig() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(0)
				.maxJobsForRider(8)
				.maxRidersForJob(12)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.OK.value(), getResult.getResponse().getStatus());
		assertNotNull(getResult.getResponse());
		
		ZoneConfig[] zoneConfig = objectMapper.readValue(getResult.getResponse().getContentAsString(), ZoneConfig[].class);
		assertNotNull(zoneConfig);
	}
	
	@Test
	void test_UpdateZoneConfig() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxJobsForRider(8)
				.maxRidersForJob(12)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.OK.value(), getResult.getResponse().getStatus());
		assertNotNull(getResult.getResponse());
		
		ZoneConfig[] zoneConfig = objectMapper.readValue(getResult.getResponse().getContentAsString(), ZoneConfig[].class);
		assertNotNull(zoneConfig);
	}
	
	@Test
	void test_UpdateZoneConfigExceptionMax() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxRidersForJob(200)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.BAD_REQUEST.value(), getResult.getResponse().getStatus());
		
	}
	@Test
	void test_UpdateZoneConfigExceptionMax2() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxJobsForRider(15)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.BAD_REQUEST.value(), getResult.getResponse().getStatus());
	}
	
	@Test
	void test_UpdateZoneConfigExceptionMin() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxRidersForJob(-1)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.BAD_REQUEST.value(), getResult.getResponse().getStatus());
	}
	
	@Test
	void test_UpdateZoneConfigExceptionMin2() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxJobsForRider(-1)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.BAD_REQUEST.value(), getResult.getResponse().getStatus());
	}
	
	@Test
	void test_UpdateZoneConfigNotFoundException() throws Exception {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(101)
				.maxJobsForRider(1)
				.build();
		
		String json = objectMapper.writeValueAsString(config);
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.put(baseURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print()).andReturn();
		
		assertEquals(HttpStatus.BAD_REQUEST.value(), getResult.getResponse().getStatus());
	}
	
}
