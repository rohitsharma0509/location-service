package com.scb.location.service.zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.model.ZoneConfig;
import com.scb.location.model.ZoneEntity;
import com.scb.location.repository.zone.ZoneRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZoneConfigServiceImpl implements ZoneConfigService {

	@Autowired
	private ZoneRepository zoneRepository;
	
	@Value("${configurable.minimum.jobs-for-rider}")
	private String minJobsForRider;
	@Value("${configurable.maximum.jobs-for-rider}")
	private String maxJobsForRider;
	@Value("${configurable.minimum.riders-for-job}")
	private String minRidersForJob;
	@Value("${configurable.maximum.riders-for-job}")
	private String maxRidersForJob;

	public List<ZoneConfig> getMerchantZoneConfig(Integer zoneId) {
		if (zoneId == 0) {
			log.info("Fetching the config details for all zones");
			List<ZoneEntity> zoneList = zoneRepository.findAll(Sort.by(Sort.Direction.ASC, "zoneId"));
			return  zoneList.stream().map(zone -> ZoneConfig.of(zone)).collect(Collectors.toList());
		}

		log.info("Fetching the config details for zone : " + zoneId);
		ZoneEntity zoneEntity = zoneRepository.findById(zoneId)
			.orElseThrow(() -> new NoResultFoundException("No corresponding zone for the provided zone-id -: " + zoneId));
		return Arrays.asList(ZoneConfig.of(zoneEntity));
	}

	
	@Transactional
	public List<ZoneConfig> updateConfigForMerchantZone(ZoneConfig zoneConfig) {
		if (zoneConfig.getZoneId() == 0) {

			if(zoneConfig.getMaxJobsForRider() != null)	zoneRepository.updateAllConfigMaxJobsForRider(zoneConfig.getMaxJobsForRider());
			
			if(zoneConfig.getMaxRidersForJob() != null) zoneRepository.updateAllConfigMaxRidersForJob(zoneConfig.getMaxRidersForJob());

			if(zoneConfig.getMaxDistanceToBroadcast() != null) zoneRepository.updateAllConfigMaxDistanceToBroadcast(zoneConfig.getMaxDistanceToBroadcast());
			
			List<ZoneEntity> allZones = zoneRepository.findAll();
			List<ZoneConfig> updatedConfig = new ArrayList<>();
			for(ZoneEntity zone: allZones) {
				updatedConfig.add(ZoneConfig.of(zone));
			}
			return  updatedConfig;
	
		} else {
			ZoneEntity zoneEntity = zoneRepository.findById(zoneConfig.getZoneId())
					.orElseThrow(() -> new NoResultFoundException("No zone found with the zoneId -: " + zoneConfig.getZoneId()));

			updateConfigFields(zoneConfig, zoneEntity);
			return Arrays.asList(zoneConfig);
		}
	}
	
	private ZoneConfig updateConfigFields(ZoneConfig zoneConfig, ZoneEntity zoneEntity) {		
		if(zoneConfig.getMaxRidersForJob() == null) zoneConfig.setMaxRidersForJob(zoneEntity.getMaxRidersForJob());
		if(zoneConfig.getMaxJobsForRider() == null) zoneConfig.setMaxJobsForRider(zoneEntity.getMaxJobsForRider());
		if(zoneConfig.getMaxDistanceToBroadcast() == null) zoneConfig.setMaxDistanceToBroadcast(zoneEntity.getMaxDistanceToBroadcast());

		validateConfigInput(zoneConfig);

		zoneRepository.updateConfigForJobById(zoneConfig.getMaxRidersForJob(), 
				zoneConfig.getMaxJobsForRider(),zoneEntity.getZoneId(), zoneConfig.getMaxDistanceToBroadcast());
		
		zoneConfig.setZoneId(zoneEntity.getZoneId());
		zoneConfig.setZoneName(zoneEntity.getZoneName());
		return zoneConfig;
	}


	private void validateConfigInput(ZoneConfig zoneConfig) {
		if (zoneConfig.getMaxJobsForRider() < Integer.parseInt(minJobsForRider) || 
				zoneConfig.getMaxJobsForRider() > Integer.parseInt(maxJobsForRider) || 
				zoneConfig.getMaxRidersForJob() < Integer.parseInt(minRidersForJob) || 
				zoneConfig.getMaxRidersForJob() > Integer.parseInt(maxRidersForJob) )
			throw new InvalidInputException("The provided input for configs violate the prescribed range");
	}

}
