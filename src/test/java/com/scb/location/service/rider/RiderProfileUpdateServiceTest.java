package com.scb.location.service.rider;

import com.scb.location.exception.RiderProfileServiceException;
import com.scb.location.model.AvailabilityStatus;
import com.scb.location.model.RiderFoodBoxSize;
import com.scb.location.model.RiderProfileEventModel;
import com.scb.location.model.RiderStatus;
import com.scb.location.repository.rider.RiderLocationRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RiderProfileUpdateServiceTest {

    private RiderProfileUpdateService riderProfileUpdateService;

    @Mock
    private RiderLocationRespository riderLocationRepository;

    @BeforeEach
    void setup() {
        riderProfileUpdateService = new RiderProfileUpdateService(riderLocationRepository);
    }

    @Test
    public void updateRiderProfileDataTest(){

        RiderProfileEventModel riderProfileEventModel = RiderProfileEventModel.builder()
                .id("riderId")
                .riderId("riderId")
                .availabilityStatus(AvailabilityStatus.Inactive)
                .status(RiderStatus.AUTHORIZED)
                .build();

        riderProfileUpdateService.updateRiderProfileData(riderProfileEventModel);

        verify(riderLocationRepository, times(1))
                .updateRiderProfileData(eq("riderId"), eq(AvailabilityStatus.Inactive.name()),
                        eq(RiderStatus.AUTHORIZED.name()), eq(false), eq(false), eq(null));

    }

    @Test
    public void updateRiderFoodBoxTest(){

        RiderFoodBoxSize riderFoodBoxSize = RiderFoodBoxSize.builder()
                .foodBoxSize("LARGE")
                .riderId("riderId")
                .build();

        riderProfileUpdateService.updateRiderFoodCartSize(riderFoodBoxSize);

        verify(riderLocationRepository, times(1))
                .updateFoodBoxSize(eq("riderId"),eq("LARGE"));

    }

    @Test
    public void updateRiderExpressTestException(){

        doThrow(new RiderProfileServiceException("Update failed")).when(riderLocationRepository).updateRiderToExpressRider(anyString(), anyBoolean());
        RiderProfileServiceException exception = assertThrows(RiderProfileServiceException.class,
                () -> riderProfileUpdateService.updateRiderToExpress("riderId",false));

    }

    @Test
    public void updateRiderExpressTest(){

        riderProfileUpdateService.updateRiderToExpress("riderId",false);

        verify(riderLocationRepository, times(1))
                .updateRiderToExpressRider(eq("riderId"),eq(false));

    }

    @Test
    public void updateRiderFoodBoxTestException() {

        RiderFoodBoxSize riderFoodBoxSize = RiderFoodBoxSize.builder()
                .foodBoxSize("LARGE")
                .riderId("riderId")
                .build();

        doThrow(new RiderProfileServiceException("Update failed")).when(riderLocationRepository).updateFoodBoxSize(anyString(), anyString());

        RiderProfileServiceException exception = assertThrows(RiderProfileServiceException.class,
                () -> riderProfileUpdateService.updateRiderFoodCartSize(riderFoodBoxSize));

    }

    @Test
    public void updateRiderToPointXTest(){

        riderProfileUpdateService.updateRiderToPointX("riderId",true);
        verify(riderLocationRepository, times(1))
                .updateRiderToPointXRider(eq("riderId"),eq(true));

    }
    @Test
    public void updateRiderToPointXTestException(){

        doThrow(new RiderProfileServiceException("Update failed")).when(riderLocationRepository).updateRiderToPointXRider(anyString(), anyBoolean());
        assertThrows(RiderProfileServiceException.class, () -> riderProfileUpdateService.updateRiderToPointX("riderId",false));

    }

    @Test
    public void updateRiderToMartTest(){
        riderProfileUpdateService.updateRiderToMart("riderId",true);
        verify(riderLocationRepository, times(1)).updateRiderToMartRider(eq("riderId"),eq(true));
    }
    @Test
    public void updateRiderToMartTestException(){
        doThrow(new RiderProfileServiceException("Update failed")).when(riderLocationRepository).updateRiderToMartRider(anyString(), anyBoolean());
        assertThrows(RiderProfileServiceException.class, () -> riderProfileUpdateService.updateRiderToMart("riderId",false));
    }


}
