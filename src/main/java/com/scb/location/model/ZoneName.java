package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ZoneName {
	private final Integer id;
	private final String name;
	private final String zone;
	private final String province;
	private final Integer provinceId;
}
