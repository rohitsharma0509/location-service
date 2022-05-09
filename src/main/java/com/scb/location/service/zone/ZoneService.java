package com.scb.location.service.zone;

import com.scb.location.model.ProvinceDto;
import com.scb.location.model.ZoneEntity;
import com.scb.location.model.ZoneName;

import java.util.List;

public interface ZoneService {

  ZoneEntity getMerchantZone(double lon, double lat);
  List<ZoneName> getAllZones();
  List<Integer> getAllZoneIdByProvince(Integer provinceId);
  ProvinceDto getProvinceIdByZoneId(Integer zoneId);
}
