package com.scb.location.rest.controller;

import java.util.List;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scb.location.service.rider.RiderLocationService;
import com.scb.location.service.zone.ZoneService;

@RestController
@RequestMapping(value = "/api/zone")
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	@Autowired
	private RiderLocationService riderLocationService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ZoneEntity> getMerchantZone(@RequestParam("location") List<Double> location) {
		double lat, lon;
		try {
			lon = location.get(0);
			lat = location.get(1);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid Merchant Location");
		}

		return ResponseEntity.ok(zoneService.getMerchantZone(lon, lat));
	}

	@GetMapping(value = "/getZone/{riderId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ZoneEntity> getRiderActiveZone(@PathVariable("riderId") String riderId) {

		RiderLocationEntity riderLocation = riderLocationService.findById(riderId);

		return ResponseEntity
				.ok(zoneService.getMerchantZone(riderLocation.getGeom().getX()
						, riderLocation.getGeom().getY()));
	}

	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ZoneName>> getAllZones() {
		return ResponseEntity.ok(zoneService.getAllZones());
	}

	@GetMapping(value = "/allByProvince/{provinceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Integer>> getAllZoneIdsByProvince(@PathVariable("provinceId") Integer provinceId) {
		return ResponseEntity.ok(zoneService.getAllZoneIdByProvince(provinceId));
	}

	@GetMapping(value = "/provinceByZone/{zoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProvinceDto> getProvinceIdByZone(@PathVariable("zoneId") Integer zoneId) {
		return ResponseEntity.ok(zoneService.getProvinceIdByZoneId(zoneId));
	}
}
