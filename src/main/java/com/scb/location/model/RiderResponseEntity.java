package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiderResponseEntity {


    private String riderId;
    private Double lat;
    private Double lon;
    private Double distance;
    private String riderStatus;
    private String profileStatus;
    private boolean evBikeUser;
    private boolean rentingToday;
    private String preferredZone;

}
