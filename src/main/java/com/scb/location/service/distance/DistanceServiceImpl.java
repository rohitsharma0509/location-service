package com.scb.location.service.distance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scb.location.exception.InvalidInputException;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.SubDistricts.AddressComponent;
import com.scb.location.model.SubDistricts.AddressResponse;
import com.scb.location.model.SubDistricts.GeocodeObject;
import com.scb.location.model.SubDistricts.GeocodeResult;
import com.scb.location.model.ZoneEntity;
import com.scb.location.repository.zone.ZoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DistanceServiceImpl implements DistanceService{

    private final String regex = "\u0E41\u0E02\u0E27\u0E07";
    @Autowired
    private GoogleMatrixRequest googleMatrixRequest;

    @Autowired
    private ZoneRepository zoneRepository;

    @Override
    public DistanceResponseEntity getDistance(Double longitudeFrom, Double latitudeFrom, Double longitudeTo, Double latitudeTo) {

        if(longitudeFrom == null || latitudeFrom == null || longitudeTo == null || latitudeTo == null)
            throw new InvalidInputException("Mandatory fields missing");

        try {
            return googleMatrixRequest.getDistance(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);
        } catch (IOException e) {
            throw new InvalidInputException(e.getMessage());
        }

    }

    @Override
    public AddressResponse getAddress(Double longitude, Double latitude) throws JsonProcessingException {
        if(longitude == null || latitude == null)
            throw new InvalidInputException("Mandatory fields missing");

        GeocodeResult response =  googleMatrixRequest.getAddress(latitude, longitude);
        AddressResponse addressResponse = null;
        String subLocalityLevel1 = "";
        String administrativeAreaLevel2 = "";
        String administrativeAreaLevel1 = "";
        String subdistrict = "";
        Integer merchantZone = getMerchantZone(longitude, latitude);
        for(GeocodeObject address: response.getResults()) {
            List<AddressComponent> addressComponents = address.getAddressComponents();
            log.info("Merchant Zone Fetched ZoneId:{} in DistanceService", merchantZone);
            for (AddressComponent addressComponent : addressComponents) {
                List<String> type = addressComponent.getTypes();
                log.info("type details -> {}", type);
                if (merchantZone > 50 && type.contains("locality")) {
                    subdistrict = addressComponent.getLongName();
                    log.info("Locality Found longName:{}, merchantZone:{}", subdistrict, merchantZone);
                    break;
                } else if (type.contains("sublocality_level_2")) {
                    subdistrict = addressComponent.getLongName();
                    break;
                } else if (StringUtils.isEmpty(subLocalityLevel1) && type.contains("sublocality_level_1")) {
                    subLocalityLevel1 = addressComponent.getLongName();
                } else if (StringUtils.isEmpty(administrativeAreaLevel2) && type.contains("administrative_area_level_2") ) {
                    administrativeAreaLevel2 = addressComponent.getLongName();
                } else if (StringUtils.isEmpty(administrativeAreaLevel1) && type.contains("administrative_area_level_1")) {
                    administrativeAreaLevel1 = addressComponent.getLongName();
                }
            }
            if(!StringUtils.isBlank(subdistrict)){
                break;
            }
        }

        if( StringUtils.isEmpty(subdistrict) && StringUtils.isNotEmpty(subLocalityLevel1 )){
            subdistrict = subLocalityLevel1;
        } else if (StringUtils.isEmpty(subdistrict) && StringUtils.isNotEmpty(administrativeAreaLevel2)) {
            subdistrict = administrativeAreaLevel2;
        } else if (StringUtils.isEmpty(subdistrict) && StringUtils.isNotEmpty(administrativeAreaLevel1)) {
            subdistrict = administrativeAreaLevel1;
        }

        log.info("Subdistrict:{}, MerchantZoneId:{}, Long:{}, Lat:{}", subdistrict, merchantZone,
                longitude, latitude);
        String[] result = subdistrict.split(" ");
        if (result.length > 0 && result[0].equals(regex)) {
            subdistrict = String.join(" ", Arrays.copyOfRange(result, 1, result.length));
        }

        addressResponse = AddressResponse.builder()
                .subDistrict(subdistrict)
                .build();

        return addressResponse;
    }


    public Integer getMerchantZone(double lon, double lat) {
      log.info("Fetching Merchant Zone in DistanceService - Lon:{}, lat: {}", lon, lat);
      Optional<ZoneEntity> zoneEntity = zoneRepository.getMerchantZone(lon, lat);
      if(zoneEntity.isPresent()) {
        return zoneEntity.get().getZoneId();
      }
      // Zone Not found
      return 0;
  }
}

