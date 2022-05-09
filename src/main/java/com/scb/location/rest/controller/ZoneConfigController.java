package com.scb.location.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scb.location.model.ZoneConfig;
import com.scb.location.service.zone.ZoneConfigService;

@RestController
@RequestMapping("/api/zone/config")
public class ZoneConfigController {
	
	@Autowired
	private ZoneConfigService zoneConfigService;
	
	@GetMapping(value = "/{zoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ZoneConfig>> getConfigForMerchantZone(@PathVariable("zoneId") Integer zoneId){
		return ResponseEntity.ok(zoneConfigService.getMerchantZoneConfig(zoneId));
	}

	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ZoneConfig>> updateConfigForMerchantZone(@RequestBody ZoneConfig zoneConfig){
		return ResponseEntity.ok(zoneConfigService.updateConfigForMerchantZone(zoneConfig));
	}
}
