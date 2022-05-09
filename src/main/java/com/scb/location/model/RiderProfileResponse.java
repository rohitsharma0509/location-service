package com.scb.location.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderProfileResponse {

  @JsonProperty("riderProfileDto")
  private RiderProfileDetails riderProfileDetails;


}
