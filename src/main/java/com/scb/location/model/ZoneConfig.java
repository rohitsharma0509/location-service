package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ZoneConfig {
	private Integer zoneId;
	@Size(max=100, message = "Zone name should not be more than 100 characters")
	private String zoneName;
	private Integer maxJobsForRider;
	private Integer maxRidersForJob;
	private Integer maxDistanceToBroadcast;

	public static ZoneConfig of(ZoneEntity entity) {
		return ZoneConfig.builder()
				.zoneId(entity.getZoneId())
				.zoneName(entity.getZoneName())
				.maxJobsForRider(entity.getMaxJobsForRider())
				.maxRidersForJob(entity.getMaxRidersForJob())
				.maxDistanceToBroadcast(entity.getMaxDistanceToBroadcast())
				.build();
	}
}
