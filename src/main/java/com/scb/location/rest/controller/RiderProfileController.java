package com.scb.location.rest.controller;


import com.scb.location.model.RiderFoodBoxSize;
import com.scb.location.service.rider.RiderProfileUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/profile/rider")
public class RiderProfileController {

    @Autowired
    private RiderProfileUpdateService riderProfileUpdateService;

    @PutMapping(value = "/food-box", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RiderFoodBoxSize> updateFoodBoxSize(@RequestBody RiderFoodBoxSize riderFoodBoxSize) {
        log.info("Updating food box size for the rider {}",riderFoodBoxSize);
        return ResponseEntity.ok(riderProfileUpdateService.updateRiderFoodCartSize(riderFoodBoxSize));
    }

    @PutMapping(value = "/express-rider", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateRiderToExpressRider(
            @RequestParam("riderId") String riderId,
            @RequestParam("isRiderExpress") Boolean isRiderExpress
    ) {
        log.info("Updating rider express status for {} as  {}",riderId,isRiderExpress);
        riderProfileUpdateService.updateRiderToExpress(riderId,isRiderExpress);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @PutMapping(value = "/pointx-rider", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateRiderToPointXRider(
            @RequestParam("riderId") String riderId,
            @RequestParam("isRiderPointX") Boolean isRiderPointX
    ) {
        log.info("Updating rider pointX status for {} as  {}",riderId,isRiderPointX);
        riderProfileUpdateService.updateRiderToPointX(riderId,isRiderPointX);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @PutMapping(value = "/mart-rider", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateRiderToMartRider(
            @RequestParam("riderId") String riderId,
            @RequestParam("isMartRider") Boolean isMartRider
    ) {
        log.info("Updating rider mart status for {} as  {}",riderId, isMartRider);
        riderProfileUpdateService.updateRiderToMart(riderId, isMartRider);
        return ResponseEntity.ok(Boolean.TRUE);
    }
}
