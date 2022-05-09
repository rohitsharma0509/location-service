package com.scb.location.service.rider;

import com.scb.location.model.RiderFoodBoxSize;
import com.scb.location.model.RiderPreferredZones;
import com.scb.location.model.RiderProfileEventModel;
import com.scb.location.repository.rider.RiderLocationRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scb.location.exception.RiderProfileServiceException;

@Slf4j
@Service
public class RiderProfileUpdateService {

    private final RiderLocationRespository riderLocationRepository;

    @Autowired
    public RiderProfileUpdateService(RiderLocationRespository riderLocationRepository) {
        this.riderLocationRepository = riderLocationRepository;
    }

    public void updateRiderProfileData(RiderProfileEventModel riderProfileEventModel){
        log.info("Updating rider profile data for riderId:{}", riderProfileEventModel.getId());

        riderLocationRepository
                .updateRiderProfileData(riderProfileEventModel.getId(),
                        riderProfileEventModel.getAvailabilityStatus().name(),
                        riderProfileEventModel.getStatus().name(), riderProfileEventModel.isEvBikeUser(),
                        riderProfileEventModel.isRentingToday(),
                        getPreferredZoneId(riderProfileEventModel.getRiderPreferredZones()));
        log.info("Rider profile details updated for riderId:{}", riderProfileEventModel.getId());

    }

    public RiderFoodBoxSize updateRiderFoodCartSize(RiderFoodBoxSize riderFoodBoxSize){
        log.info("Updating rider profile data for riderId:{}", riderFoodBoxSize.getRiderId());

        try {
            riderLocationRepository
                    .updateFoodBoxSize(riderFoodBoxSize.getRiderId(), riderFoodBoxSize.getFoodBoxSize());
            log.info("Rider profile details updated for riderId:{}", riderFoodBoxSize.getRiderId());
            return riderFoodBoxSize;
        }
        catch (Exception e){
            log.error("Db update of foodBoxSize for rider {} failed with exception: {}",riderFoodBoxSize.getRiderId(),e.getMessage());
            throw new RiderProfileServiceException("Update failed");
        }

    }

    public void updateRiderToExpress(String riderId,Boolean isRiderExpress){
        log.info("Changing the rider express status to {}",isRiderExpress);

        try {
            riderLocationRepository.updateRiderToExpressRider(riderId,isRiderExpress);
        }
        catch (Exception e){
            log.error("DB update for rider failed {} {}",riderId,isRiderExpress);
            throw new RiderProfileServiceException("Failed to update isExpressRider flag");
        }

    }

    private String getPreferredZoneId(RiderPreferredZones riderPreferredZones){
        return riderPreferredZones != null && riderPreferredZones.getPreferredZoneId() !=null
                ? riderPreferredZones.getPreferredZoneId() : null;

    }

    public void updateRiderToPointX(String riderId, Boolean isRiderPointX) {
        log.info("Changing the rider pointx status to {}",isRiderPointX);
        try {
            riderLocationRepository.updateRiderToPointXRider(riderId,isRiderPointX);
        }
        catch (Exception e){
            log.error("DB update for rider failed {} {}",riderId,isRiderPointX);
            throw new RiderProfileServiceException("Failed to update isPointXRider flag");
        }
    }

    public void updateRiderToMart(String riderId, Boolean isMartRider) {
        log.info("Changing the rider mart status to {}", isMartRider);
        try {
            riderLocationRepository.updateRiderToMartRider(riderId, isMartRider);
        } catch (Exception e){
            log.error("DB update for rider failed for mart {} {}",riderId, isMartRider);
            throw new RiderProfileServiceException("Failed to update isMartRider flag");
        }
    }
}
