package com.scb.location.service.rider;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.kafka.producer.Sender;
import com.scb.location.model.*;
import com.scb.location.repository.rider.RiderLocationRespository;
import com.scb.location.service.zone.ZoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class RiderLocationServiceImpl implements RiderLocationService {


    private final RiderLocationRespository riderLocationRepository;
    private final ZoneService zoneService;
    private final Sender sender;
    private final RiderServiceProxy riderServiceProxy;


    @Autowired
    public RiderLocationServiceImpl(RiderLocationRespository riderLocationRepository,
                                    ZoneService zoneService, Sender sender, RiderServiceProxy riderServiceProxy) {
        this.riderLocationRepository = riderLocationRepository;
        this.zoneService = zoneService;
        this.sender = sender;
        this.riderServiceProxy = riderServiceProxy;
    }

    public List<RiderLocationEntity> findAll() {
        return  riderLocationRepository.findAll();
    }

    public RiderLocationEntity findById(String riderId) {

        Optional<RiderLocationEntity>  riderLocation = riderLocationRepository.findById(riderId);
        if(riderLocation.isPresent()) {
            log.info("extracted rider location for rider id {} : {}",riderId,riderLocation);
            return riderLocation.get();
        }
        throw new NoResultFoundException(String.format("Rider %s not found", riderId));
    }

    public void deleteById(String riderId) {
        Optional<RiderLocationEntity> rider = riderLocationRepository.findById(riderId);
        if(!rider.isPresent())
            throw new NoResultFoundException(String.format("Rider %s not found", riderId));
        riderLocationRepository.deleteById(riderId);
    }


    @Override
    public List<RiderResponseEntity> findAvailableRiders(Double longitude, Double latitude, Integer limit) {
        if(longitude == null || latitude == null || limit == null )
            throw new InvalidInputException("Invalid Input");

        List<RiderLocationEntity> riderLocationEntityList = riderLocationRepository.getNearbyAvailableRiders( longitude, latitude, limit);
        return getRiderResponseEntityList(riderLocationEntityList,longitude, latitude);
    }

    @Override
    public List<RiderResponseEntity> findAvailableExpressRiders(Double longitude, Double latitude, Integer limit) {
        if(longitude == null || latitude == null || limit == null)
            throw new InvalidInputException("Invalid Input for finding nearby express riders");

        List<RiderLocationEntity> riderLocationEntityList = riderLocationRepository.getNearbyAvailableExpressRiders( longitude, latitude,Boolean.TRUE, limit);
        return getRiderResponseEntityList(riderLocationEntityList,longitude, latitude);
    }

    @Override
    public List<RiderResponseEntity> findAvailableRidersByBoxType(Double longitude, Double latitude, String foodBoxType, Boolean isMartRider, Integer limit) {
        if(longitude == null || latitude == null || limit == null || foodBoxType == null)
            throw new InvalidInputException("Invalid Input for finding nearby riders");

        List<RiderLocationEntity> riderLocationEntityList;
        if(isMartRider) {
            riderLocationEntityList = riderLocationRepository.getNearbyAvailableRidersByBoxTypeAndIsMartRider(longitude, latitude, foodBoxType, true, limit);
        } else {
            riderLocationEntityList = riderLocationRepository.getNearbyAvailableRidersByBoxType(longitude, latitude,
                    foodBoxType, limit);
        }
        return getRiderResponseEntityList(riderLocationEntityList,longitude, latitude);
    }

    @Override
    public RiderLocation postRiderLocation(String riderId, RiderLocation riderLocation) {
        riderLocation.setRiderId(riderId);
        riderLocation.setDateTime(Instant.now().toString());
        if(Objects.isNull(riderLocation.getAvailabilityStatus())){
            riderLocation.setAvailabilityStatus(AvailabilityStatus.valueOf(riderServiceProxy.getRiderProfile(riderId).getAvailabilityStatus()));
        }
        sender.send(riderLocation);
        log.info("[{}] - sending updated location for rider: {}", riderLocation.getDateTime(), riderId);
        return riderLocation;
    }

    public RiderResponseEntity addNewRiderLocationEntity(String riderId , Double lon, Double lat) {
        riderLocationRepository.addNewRider(riderId, lon, lat);
        return RiderResponseEntity.builder()
                .riderId(riderId)
                .lat(lat)
                .lon(lon)
                .build();
    }

    public static List<RiderResponseEntity> getRiderResponseEntityList(List<RiderLocationEntity> riderLocationEntityList, Double longitude, Double latitude) {
        List<RiderResponseEntity> riders = new ArrayList<>();
        riderLocationEntityList.forEach(list -> {
            RiderResponseEntity rider = new RiderResponseEntity();
            rider.setLon(list.getGeom().getX());
            rider.setLat(list.getGeom().getY());
            rider.setRiderId(list.getRiderId());
            rider.setDistance(Math.round(list.getDistance()*100.0)/100.0);
            rider.setEvBikeUser(list.getEvBikeUser() != null && list.getEvBikeUser());
            rider.setRentingToday(list.getRentingToday() != null && list.getRentingToday());
            rider.setPreferredZone(list.getPreferredZone());
            riders.add(rider);
        });
        return riders;
    }

    @Override
    public void updateInsertRiderLocationEntity(String riderId, Double lon, Double lat,
                                                String dateTime) {
        log.info("Updating rider location for rider:{} ; lat:{} ; lon:{}", riderId, lat, lon);
        ZonedDateTime zone = ZonedDateTime.now();
        try {
            zone = ZonedDateTime.parse(dateTime);
        } catch (DateTimeParseException ex) {
            throw new InvalidInputException("Invalid DateTime Format");
        }
        riderLocationRepository.updateInsertRider(riderId, lon, lat, zone);
    }

    @Override
    public List<RiderResponseEntity> findAvailablePointxRiders(Double longitude, Double latitude, Integer limit) {
        if(longitude == null || latitude == null || limit == null)
            throw new InvalidInputException("Invalid Input for finding nearby pointx riders");

        List<RiderLocationEntity> riderLocationEntityList = riderLocationRepository.getNearbyAvailablePointxRiders( longitude, latitude,Boolean.TRUE, limit);
        return getRiderResponseEntityList(riderLocationEntityList,longitude, latitude);
    }

}

