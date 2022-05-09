package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RiderLocation {

  @NotEmpty(message = "Rider Id Should not Empty")
  @NotNull(message = "Rider Id Should not Null")
  @Size(max = 60, message = "Rider ID should not be more than 60 characters")
  private String riderId;

  @NotNull(message = "Latitude Should not Null")
  private Double lat;

  @NotNull(message = "Longitude Should not Null")
  private Double lon;

  @NotEmpty(message = "DateTime Should not Empty")
  @NotNull(message = "DateTime Should not Null")
  @Size(max = 60, message = "Date-Time should not be more than 60 characters")
  private String dateTime;

  private String phoneDateTime;
  private String channel;

  private AvailabilityStatus availabilityStatus;

}
