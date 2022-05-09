package com.scb.location.model.SubDistricts;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddressComponent {

   @JsonProperty("long_name")
   String longName;
   @JsonProperty("short_name")
   String shortName;
   List<String> types;
}