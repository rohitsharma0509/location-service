package com.scb.location.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RiderPreferredZones implements Serializable {

	private String preferredZoneId;
	private String preferredZoneName;
	private String updatedBy;
}


