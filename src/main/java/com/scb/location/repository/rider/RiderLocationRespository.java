package com.scb.location.repository.rider;

import com.scb.location.model.RiderLocationEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderLocationRespository extends JpaRepository<RiderLocationEntity, String> {

    @Query(value = "SELECT r.rider_Id, r.geom, ST_Distance(ST_Transform(r.geom, 3857),"
            + "ST_Transform(ST_SetSRID(ST_Point(:longitude,:latitude),4326),3857)"
            + ") "
            + "AS distance, rider_status AS rider_status, profile_status as profile_status, evbike_user as ev_bike_user,"
            + " renting_today AS renting_today, preferred_zone AS preferred_zone,food_box_size AS food_box_size, is_express_rider AS is_express_rider,"
            +" is_pointx_rider AS is_pointx_rider, is_mart_rider AS is_mart_rider"
            +" FROM rider_location r "
            +" WHERE r.rider_status = 'Active' and r.profile_status = 'AUTHORIZED'"
            + " and r.geom is not null"
            +" ORDER BY r.geom  <-> ST_SetSRID(ST_Point(:longitude,:latitude),4326) "
            + "LIMIT :limit", nativeQuery = true)
    List<RiderLocationEntity> getNearbyAvailableRiders( @Param("longitude") Double longitude,
                                                        @Param("latitude") Double latitude, @Param("limit") Integer limit);

    @Query(value = "SELECT r.rider_Id, r.geom, ST_Distance(ST_Transform(r.geom, 3857),"
            + "ST_Transform(ST_SetSRID(ST_Point(:longitude,:latitude),4326),3857)"
            + ") "
            + "AS distance, rider_status AS rider_status, profile_status as profile_status, evbike_user as ev_bike_user,"
            + " renting_today AS renting_today, preferred_zone AS preferred_zone,food_box_size AS food_box_size, is_express_rider AS is_express_rider,"
            +" is_pointx_rider AS is_pointx_rider, is_mart_rider AS is_mart_rider"
            +" FROM rider_location r "
            +" WHERE r.rider_status = 'Active' and r.profile_status = 'AUTHORIZED' and r.food_box_size = :foodBoxSize"
            + " and r.geom is not null"
            +" ORDER BY r.geom  <-> ST_SetSRID(ST_Point(:longitude,:latitude),4326) "
            + "LIMIT :limit", nativeQuery = true)
    List<RiderLocationEntity> getNearbyAvailableRidersByBoxType( @Param("longitude") Double longitude,
                                                                 @Param("latitude") Double latitude, @Param("foodBoxSize") String foodBoxSize,
                                                                 @Param("limit") Integer limit);

    @Query(value = "SELECT r.rider_Id, r.geom, ST_Distance(ST_Transform(r.geom, 3857),"
            + "ST_Transform(ST_SetSRID(ST_Point(:longitude,:latitude),4326),3857)"
            + ") "
            + "AS distance, rider_status AS rider_status, profile_status as profile_status, evbike_user as ev_bike_user,"
            + " renting_today AS renting_today, preferred_zone AS preferred_zone,food_box_size AS food_box_size, is_express_rider AS is_express_rider,"
            +" is_pointx_rider AS is_pointx_rider, is_mart_rider AS is_mart_rider"
            +" FROM rider_location r "
            +" WHERE r.rider_status = 'Active' and r.profile_status = 'AUTHORIZED' and r.food_box_size = :foodBoxSize and r.is_mart_rider = :isMartRider"
            + " and r.geom is not null"
            +" ORDER BY r.geom  <-> ST_SetSRID(ST_Point(:longitude,:latitude),4326) "
            + "LIMIT :limit", nativeQuery = true)
    List<RiderLocationEntity> getNearbyAvailableRidersByBoxTypeAndIsMartRider(
            @Param("longitude") Double longitude, @Param("latitude") Double latitude,
            @Param("foodBoxSize") String foodBoxSize, @Param("isMartRider") Boolean isMartRider, @Param("limit") Integer limit);

    @Query(value = "SELECT r.rider_Id, r.geom, ST_Distance(ST_Transform(r.geom, 3857),"
            + "ST_Transform(ST_SetSRID(ST_Point(:longitude,:latitude),4326),3857)"
            + ") "
            + "AS distance, rider_status AS rider_status, profile_status as profile_status, evbike_user as ev_bike_user,"
            + " renting_today AS renting_today, preferred_zone AS preferred_zone,food_box_size AS food_box_size, is_express_rider AS is_express_rider,"
            +" is_pointx_rider AS is_pointx_rider, is_mart_rider AS is_mart_rider"
            +" FROM rider_location r "
            +" WHERE r.rider_status = 'Active' and r.profile_status = 'AUTHORIZED' and r.is_express_rider = :isExpressRider"
            + " and r.geom is not null"
            +" ORDER BY r.geom  <-> ST_SetSRID(ST_Point(:longitude,:latitude),4326) "
            + "LIMIT :limit", nativeQuery = true)
    List<RiderLocationEntity> getNearbyAvailableExpressRiders( @Param("longitude") Double longitude,
                                                               @Param("latitude") Double latitude, @Param("isExpressRider") boolean isExpressRider,
                                                               @Param("limit") Integer limit);

    @Query(value = "SELECT r.rider_Id, r.geom, null AS distance, rider_status AS rider_status, "
            + " profile_status as profile_status, evbike_user as ev_bike_user,"
            + " renting_today AS renting_today, preferred_zone AS preferred_zone,"
            + " food_box_size AS food_box_size, is_express_rider AS is_express_rider, is_pointx_rider AS is_pointx_rider, is_mart_rider AS is_mart_rider "
            +" FROM rider_location r"
            +" WHERE r.rider_Id = :riderId"
            + " LIMIT 1", nativeQuery = true)
    Optional<RiderLocationEntity> findById(@Param("riderId") String riderId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location" + "(rider_id, geom, rider_status)"
            + " VALUES (:riderId , ST_SetSRID(ST_MakePoint(:lon,:lat),4326), 'Active')", nativeQuery = true)
    void addNewRider(@Param("riderId") String riderId, @Param("lon") Double lon,
                     @Param("lat") Double lat);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location" + "(rider_id, geom, updated_timestamp)"
            + " VALUES (:riderId , ST_SetSRID(ST_MakePoint(:lon,:lat),4326), :updated_timestamp) "
            + "ON CONFLICT (rider_id) "
            + "DO UPDATE SET geom = ST_SetSRID(ST_MakePoint(:lon,:lat),4326),updated_timestamp = :updated_timestamp",
            nativeQuery = true)
    void updateInsertRider(@Param("riderId") String riderId, @Param("lon") Double lon,
                           @Param("lat") Double lat, @Param("updated_timestamp") ZonedDateTime dateTime);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location(rider_id, rider_status, profile_status, evbike_user, renting_today, preferred_zone)"
            + " VALUES (:riderId ,:riderStatus, :profileStatus, :evBikeUser, :rentingToday, :preferredZone)"
            + " ON CONFLICT (rider_id) "
            + " DO UPDATE set rider_status=:riderStatus , profile_status=:profileStatus"
            + ", evbike_user=:evBikeUser,  renting_today=:rentingToday, preferred_zone=CAST(CAST(:preferredZone AS TEXT) AS integer)",
            nativeQuery = true)
    void updateRiderProfileData(@Param("riderId") String riderId, @Param("riderStatus") String riderStatus,
                                @Param("profileStatus") String profileStatus, @Param("evBikeUser") boolean evBikeUser,
                                @Param("rentingToday") boolean rentingToday, @Param("preferredZone") String preferredZone);
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location(rider_id, food_box_size)"
            + " VALUES (:riderId ,:foodBoxSize)"
            + " ON CONFLICT (rider_id) "
            + " DO UPDATE set food_box_size=:foodBoxSize",
            nativeQuery = true)
    void updateFoodBoxSize(@Param("riderId") String riderId, @Param("foodBoxSize") String foodBoxSize);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location(rider_id, is_express_rider)"
            + " VALUES (:riderId ,:isExpressRider)"
            + " ON CONFLICT (rider_id) "
            + " DO UPDATE set is_express_rider=:isExpressRider",
            nativeQuery = true)
    void updateRiderToExpressRider(@Param("riderId") String riderId, @Param("isExpressRider") boolean isExpressRider);

    @Query(value = "SELECT r.rider_Id, r.geom, ST_Distance(ST_Transform(r.geom, 3857),"
            + "ST_Transform(ST_SetSRID(ST_Point(:longitude,:latitude),4326),3857)"
            + ") "
            + "AS distance, rider_status AS rider_status, profile_status as profile_status, evbike_user as ev_bike_user,"
            + " renting_today AS renting_today, preferred_zone AS preferred_zone,food_box_size AS food_box_size,is_express_rider AS is_express_rider,"
            + " is_pointx_rider AS is_pointx_rider, is_mart_rider AS is_mart_rider"
            +" FROM rider_location r "
            +" WHERE r.rider_status = 'Active' and r.profile_status = 'AUTHORIZED' and r.is_pointx_rider = :isPointxRider"
            + " and r.geom is not null"
            +" ORDER BY r.geom  <-> ST_SetSRID(ST_Point(:longitude,:latitude),4326) "
            + "LIMIT :limit", nativeQuery = true)
    List<RiderLocationEntity> getNearbyAvailablePointxRiders( @Param("longitude") Double longitude,
                                                               @Param("latitude") Double latitude, @Param("isPointxRider") boolean isPointxRider,
                                                               @Param("limit") Integer limit);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location(rider_id, is_pointx_rider)"
            + " VALUES (:riderId ,:isPointxRider)"
            + " ON CONFLICT (rider_id) "
            + " DO UPDATE set is_pointx_rider=:isPointxRider",
            nativeQuery = true)
    void updateRiderToPointXRider(@Param("riderId") String riderId, @Param("isPointxRider") boolean isPointxRider);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rider_location(rider_id, is_mart_rider)"
            + " VALUES (:riderId ,:isMartRider)"
            + " ON CONFLICT (rider_id) "
            + " DO UPDATE set is_mart_rider=:isMartRider",
            nativeQuery = true)
    void updateRiderToMartRider(@Param("riderId") String riderId, @Param("isMartRider") boolean isMartRider);

}

