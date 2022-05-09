package com.scb.location.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.scb.location.model.ZoneConfig;
import com.scb.location.service.zone.ZoneConfigService;
import com.scb.location.service.zone.ZoneConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
class ZoneConfigControllerTest {

	@InjectMocks
	private ZoneConfigController configController;
	
	@Mock
	private ZoneConfigService configService;
	
	private List<ZoneConfig> zoneList;

	
	@BeforeEach
	void setup() {
		zoneList = new ArrayList<>();
		
		ZoneConfig config = ZoneConfig.builder().zoneId(1).maxJobsForRider(5).maxRidersForJob(25).build();
		ZoneConfig config2 = ZoneConfig.builder().zoneId(2).maxJobsForRider(7).maxRidersForJob(5).build();
		ZoneConfig config3 = ZoneConfig.builder().zoneId(3).maxJobsForRider(3).maxRidersForJob(35).build();
		zoneList.add(config);
		zoneList.add(config2);
		zoneList.add(config3);
	}
	
	@Test
	void test_GetConfigs() {
		Mockito.when(configService.getMerchantZoneConfig(Mockito.anyInt())).thenReturn(zoneList);
		
		ResponseEntity<List<ZoneConfig>> respGetConfig = configController.getConfigForMerchantZone(0);
		assertEquals(200, respGetConfig.getStatusCode().value());
		assertEquals(3, respGetConfig.getBody().size());
		
	}

	@Test
	void test_UpdateConfigs() {
		Mockito.when(configService.updateConfigForMerchantZone(Mockito.any())).thenReturn(zoneList);
		
		ResponseEntity<List<ZoneConfig>> respGetConfig = configController.updateConfigForMerchantZone(zoneList.get(0));
		assertEquals(200, respGetConfig.getStatusCode().value());
		assertEquals(3, respGetConfig.getBody().size());
		
	}
	
}
