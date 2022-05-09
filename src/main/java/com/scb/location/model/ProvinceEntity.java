package com.scb.location.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "province")
@Getter @Setter @AllArgsConstructor  @NoArgsConstructor
public class ProvinceEntity {
    @Id
    @GeneratedValue
    @Column(name = "province_id")
    Integer provinceId;
    @Column(name = "name")
    String name;

}
