package com.scb.location.service.rider;

import java.util.List;

import com.scb.location.model.RiderLocation;
import com.scb.location.model.RiderLocationEntity;
import com.scb.location.model.RiderRequestEntity;
import com.scb.location.model.RiderResponseEntity;

public interface RiderLocationService {

  List<RiderLocationEntity> findAll();

  RiderLocationEntity findById(String riderId);

  RiderResponseEntity addNewRiderLocationEntity(String riderId, Double lon, Double lat);

  void updateInsertRiderLocationEntity(String riderId, Double lon, Double lat,
                                       String dateTime);

  void deleteById(String riderId);

  List<RiderResponseEntity> findAvailableRiders(Double longitude, Double latitude, Integer limit);

  List<RiderResponseEntity> findAvailableExpressRiders(Double longitude, Double latitude, Integer limit);

  List<RiderResponseEntity> findAvailableRidersByBoxType(Double longitude, Double latitude, String foodBoxType, Boolean isMartRider, Integer limit);

  RiderLocation postRiderLocation(String riderId, RiderLocation riderLocation);

  List<RiderResponseEntity> findAvailablePointxRiders(Double longitude, Double latitude, Integer limit);
}
