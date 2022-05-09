package com.scb.location.service.zone;

import java.util.*;
import java.util.stream.Collectors;

import com.scb.location.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scb.location.exception.NoResultFoundException;
import com.scb.location.repository.zone.ZoneRepository;
import org.springframework.util.CollectionUtils;

@Service
public class ZoneServiceImpl implements ZoneService {

  private final ZoneRepository zoneRepository;
  private static final String BANGKOK_PREFIX = "กรุงเทพฯ";
  @Autowired
  public ZoneServiceImpl(ZoneRepository zoneRepository) {
      this.zoneRepository = zoneRepository;
  }

  public ZoneEntity getMerchantZone(double lon, double lat) {
      Optional<ZoneEntity> zoneEntity = zoneRepository.getMerchantZone(lon, lat);
      if(zoneEntity.isPresent()) {
        return zoneEntity.get();
      }
      throw new NoResultFoundException("Zone of the merchant not found");
  }

  public List<ZoneName> getAllZones(){
      List<ProvinceZoneProjection> allZonesList = zoneRepository.getAllZones();
      if(CollectionUtils.isEmpty(allZonesList)){
          return new ArrayList<>();
      }
      Comparator<ZoneName> compareByName = (ZoneName obj1, ZoneName obj2) -> {
          if (obj1.getProvince().equalsIgnoreCase(BANGKOK_PREFIX)){
              if (obj2.getProvince().equalsIgnoreCase(BANGKOK_PREFIX)) {
                  return 0;
              }
              return -1;
          }
          if(obj2.getProvince().equalsIgnoreCase(BANGKOK_PREFIX)) {
              return 1;
          }
          return obj1.getName().compareTo(obj2.getName());
      };

	  return allZonesList.stream().map(zoneStr -> new ZoneName(
	          zoneStr.getZoneId(),zoneStr.getProvinceName() + ": " + zoneStr.getZoneName(), zoneStr.getZoneName(), zoneStr.getProvinceName(), zoneStr.getProvinceId()))
              .sorted(compareByName).collect(Collectors.toList());
  }

    /**
     * To get the list of all the zones falling in a province
     * @param provinceId - Province Id for which zones are required
     * @return - List of zone Ids
     */
    @Override
    public List<Integer> getAllZoneIdByProvince(Integer provinceId) {
        Optional<List<Integer>> zoneList = zoneRepository.getAllZoneIdByProvince(provinceId);
        if (zoneList.isPresent()) return zoneList.get();
        throw new NoResultFoundException("No zones found");
    }

    /**
     * To get the province details of a given zone
     * @param zoneId - Zone Id for which province is required
     * @return - Province name and Id
     */
    @Override
    public ProvinceDto getProvinceIdByZoneId(Integer zoneId) {
        ProvinceDto province = zoneRepository.getProvinceByZoneId(zoneId);
        if (province != null ) return province;
        throw new NoResultFoundException("No province found for given zone Id");
    }

}
