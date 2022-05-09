package com.scb.location.repository.zone;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.scb.location.model.ProvinceDto;
import com.scb.location.model.ProvinceEntity;
import com.scb.location.model.ProvinceZoneProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.scb.location.model.ZoneEntity;

@Repository
public interface ZoneRepository extends JpaRepository<ZoneEntity, Integer> {

    @Query(value = "select * from bangkok_zone bz where ST_Contains(bz.geom, ST_SetSRID(ST_Point(:lon ,:lat),4326)) LIMIT 1"
            , nativeQuery = true)
    Optional<ZoneEntity> getMerchantZone(@Param("lon") Double lon,@Param("lat")  Double lat);

    @Query(value = "select zone_id as zoneId, b.name as zoneName, p.name as provinceName, b.province_id as provinceId from bangkok_zone b inner join province p on p.province_id = b.province_id", nativeQuery = true)
    List<ProvinceZoneProjection> getAllZones();

    @Query(value = "select zone_id from bangkok_zone b inner join province p on p.province_id = b.province_id and p.province_id = :provinceId" , nativeQuery = true)
    Optional<List<Integer>> getAllZoneIdByProvince(@Param("provinceId") Integer provinceId);

    @Modifying
    @Transactional
    @Query(value = "update bangkok_zone set max_riders_for_job = :riders_for_job, max_jobs_for_rider = :jobs_for_rider, max_distance_to_broadcast = :distance_to_broadcast where zone_id = :id", nativeQuery = true)
    void updateConfigForJobById(@Param("riders_for_job") Integer maxRidersForJob, @Param("jobs_for_rider")Integer maxJobsForRider,@Param("id") Integer zoneId, @Param("distance_to_broadcast") Integer maxDistanceToBroadcast);

    @Modifying
    @Transactional
    @Query(value = "update bangkok_zone set max_riders_for_job = :riders_for_job", nativeQuery = true)
    void updateAllConfigMaxRidersForJob(@Param("riders_for_job") Integer maxRidersForJob);

    @Modifying
    @Transactional
    @Query(value = "update bangkok_zone set max_distance_to_broadcast = :distance_to_broadcast", nativeQuery = true)
    void updateAllConfigMaxDistanceToBroadcast(@Param("distance_to_broadcast") Integer maxDistanceToBroadcast);

    @Modifying
    @Transactional
    @Query(value = "update bangkok_zone set  max_jobs_for_rider = :jobs_for_rider", nativeQuery = true)
    void updateAllConfigMaxJobsForRider(@Param("jobs_for_rider")Integer maxJobsForRider);

    @Query(value = "select p.province_id as provinceId , p.name as name from bangkok_zone b inner join province p on p.province_id = b.province_id where b.zone_id = :zoneId", nativeQuery = true)
    ProvinceDto getProvinceByZoneId(@Param("zoneId") Integer zoneId);

    
}