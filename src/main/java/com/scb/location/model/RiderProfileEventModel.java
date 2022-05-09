package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderProfileEventModel {

  private String riderId;
  private String dateTime;
  private String event;
  private String serviceName;
  private RiderPreferredZones riderPreferredZones;
  private String id;
  private RiderStatus status;
  private AvailabilityStatus availabilityStatus;
  private boolean evBikeUser;
  private boolean rentingToday;

}
