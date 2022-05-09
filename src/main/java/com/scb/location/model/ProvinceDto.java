package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

public interface ProvinceDto {
    Integer getProvinceId();
    String getName();
}
