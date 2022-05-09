package com.scb.location.service.zone;

import java.util.List;

import com.scb.location.model.ZoneConfig;

public interface ZoneConfigService {

	List<ZoneConfig> getMerchantZoneConfig(Integer zoneId);
	List<ZoneConfig> updateConfigForMerchantZone(ZoneConfig zoneConfig);
}
