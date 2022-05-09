package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DistanceResponseEntity {
    private Double longitudeFrom;
    private Double latitudeFrom;

    private Double longitudeTo;
    private Double latitudeTo;

    private Double distance;
    private Double duration;
}
