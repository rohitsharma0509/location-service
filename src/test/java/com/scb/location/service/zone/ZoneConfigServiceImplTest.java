package com.scb.location.service.zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.model.ZoneConfig;
import com.scb.location.model.ZoneEntity;
import com.scb.location.repository.zone.ZoneRepository;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class ZoneConfigServiceImplTest {

	@InjectMocks
	private ZoneConfigServiceImpl configService = new ZoneConfigServiceImpl();
	
	@Mock
	private ZoneRepository repository;
	
	private List<ZoneConfig> zoneConfigList;
	private List<ZoneEntity> zoneList;

	@BeforeEach
	void setup() {
		zoneConfigList = new ArrayList<>();
		zoneList = new ArrayList<>();
		
		ZoneEntity zoneEntity = ZoneEntity.builder().zoneId(1).maxJobsForRider(5).maxRidersForJob(25).maxDistanceToBroadcast(15).build();
		ZoneEntity zoneEntity2 = ZoneEntity.builder().zoneId(2).maxJobsForRider(7).maxRidersForJob(5).maxDistanceToBroadcast(16).build();
		ZoneEntity zoneEntity3 = ZoneEntity.builder().zoneId(3).maxJobsForRider(3).maxRidersForJob(35).maxDistanceToBroadcast(17).build();
		zoneList.add(zoneEntity);
		zoneList.add(zoneEntity2);
		zoneList.add(zoneEntity3);

		ReflectionTestUtils.setField(configService, "minJobsForRider","1");
		ReflectionTestUtils.setField(configService, "minRidersForJob","1");
		ReflectionTestUtils.setField(configService, "maxJobsForRider","10");
		ReflectionTestUtils.setField(configService, "maxRidersForJob","100");

	}

	@Test
	void test_GetAllZonesConfig() {
		Mockito.when(repository.findAll(Sort.by(Sort.Direction.ASC, "zoneId"))).thenReturn(zoneList);
		List<ZoneConfig> allZoneList = configService.getMerchantZoneConfig(0);
		
		Assertions.assertNotNull(allZoneList);
		Assertions.assertEquals(3, allZoneList.size());
		Assertions.assertEquals(5, allZoneList.get(0).getMaxJobsForRider());
		Assertions.assertEquals(15, allZoneList.get(0).getMaxDistanceToBroadcast());
	}
	
	@Test
	void test_UpdateAllZonesConfig() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(0)
				.maxRidersForJob(5)
				.maxJobsForRider(7)
				.build();
		Mockito.when(repository.findAll()).thenReturn(zoneList);
		List<ZoneConfig> allZoneList = configService.updateConfigForMerchantZone(config);
		
		Assertions.assertNotNull(allZoneList);
		Assertions.assertEquals(3, allZoneList.size());
		Assertions.assertEquals(5, allZoneList.get(0).getMaxJobsForRider());
		Assertions.assertEquals(15, allZoneList.get(0).getMaxDistanceToBroadcast());
	}

	@Test
	void test_GetZonesConfig() {
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(zoneList.get(0)));
		List<ZoneConfig> allZoneList = configService.getMerchantZoneConfig(1);
		
		Assertions.assertNotNull(allZoneList);
		Assertions.assertEquals(1, allZoneList.size());
		Assertions.assertEquals(5, allZoneList.get(0).getMaxJobsForRider());
	}
	
	@Test
	void test_UpdateZonesConfig() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxRidersForJob(5)
				.maxJobsForRider(7)
				.maxDistanceToBroadcast(50)
				.build();
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(zoneList.get(0)));
		List<ZoneConfig> allZoneList = configService.updateConfigForMerchantZone(config);
		
		Assertions.assertNotNull(allZoneList);
		Assertions.assertEquals(1, allZoneList.size());
		Assertions.assertEquals(7, allZoneList.get(0).getMaxJobsForRider());
		Assertions.assertEquals(50, allZoneList.get(0).getMaxDistanceToBroadcast());
	
	}

	@Test
	void test_UpdateZonesConfig_InvalidInputExceptionMin() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxRidersForJob(500)
				.maxJobsForRider(7)
				.build();
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(zoneList.get(0)));
		Assertions.assertThrows(InvalidInputException.class, () -> configService.updateConfigForMerchantZone(config));
	}

	@Test
	void test_UpdateZonesConfig_InvalidInputExceptionMax() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxRidersForJob(-1)
				.build();
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(zoneList.get(0)));
		Assertions.assertThrows(InvalidInputException.class, () -> configService.updateConfigForMerchantZone(config));
	}

	@Test
	void test_UpdateZonesConfig_InvalidInputExceptionMin2() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxJobsForRider(-1)
				.build();
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(zoneList.get(0)));
		Assertions.assertThrows(InvalidInputException.class, () -> configService.updateConfigForMerchantZone(config));
	}
	@Test
	void test_UpdateZonesConfig_InvalidInputExceptionMax2() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(1)
				.maxRidersForJob(8)
				.maxJobsForRider(80)
				.build();
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(zoneList.get(0)));
		Assertions.assertThrows(InvalidInputException.class, () -> configService.updateConfigForMerchantZone(config));
	}

	@Test
	void test_UpdateZonesConfig_NoResultException() {
		ZoneConfig config = ZoneConfig.builder()
				.zoneId(101)
				.maxRidersForJob(8)
				.maxJobsForRider(80)
				.build();
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		Assertions.assertThrows(NoResultFoundException.class, () -> configService.updateConfigForMerchantZone(config));
	}

	@Test
	void test_GetZonesConfig_NoResultException() {
		Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		Assertions.assertThrows(NoResultFoundException.class, () -> configService.getMerchantZoneConfig(101));
	}



}
