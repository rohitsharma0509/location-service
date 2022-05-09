package com.scb.location.model;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GsonResponse {
    @NonNull
    private String type;
    @NonNull
    private List<List<Double>> coordinates;
}
