package com.scb.location.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.locationtech.jts.geom.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rider_location")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiderLocationEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  @Column(name = "rider_id")
  private String riderId;

  @Column(name = "geom")
  private Point geom;

  @Column(name = "distance")
  private Double distance;

  @Column(name = "rider_status")
  private String riderStatus;

  @Column(name = "profile_status")
  private String profileStatus;

  @Column(name = "ev_bike_user")
  private Boolean evBikeUser;

  @Column(name = "renting_today")
  private Boolean rentingToday;

  @Column(name = "preferred_zone")
  private String preferredZone;

  @Column(name = "food_box_size")
  private String foodBoxSize;

  @Column(name = "is_express_rider")
  private Boolean isExpressRider;
  
  @Column(name = "is_pointx_rider")
  private Boolean isPointxRider;

  @Column(name = "is_mart_rider")
  private Boolean isMartRider;

}

