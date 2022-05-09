package com.scb.location.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "bangkok_zone")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  @Column(name = "zone_id")
  private Integer zoneId;

  @Column(name = "postal_code")
  private Integer postalCode;

  @Column(name = "name")
  private String zoneName;

  @Column(name = "geom")
  @JsonIgnore
  private Geometry geom;
  
  @Column(name = "max_jobs_for_rider")
  private Integer maxJobsForRider;
  
  @Column(name = "max_riders_for_job")
  private Integer maxRidersForJob;

  @Column(name = "max_distance_to_broadcast")
  private Integer maxDistanceToBroadcast;

  @ManyToOne
  @JoinColumn(name = "province_id")
  private ProvinceEntity province;

  @Column(name = "zone_group")
  private Integer zoneGroup;

  public String getZoneName() {
    String provinceName = province  != null && province.getName() != null ? province.getName() : "";
    return provinceName + ": " + zoneName;
  }

}
