package com.scb.location.service.rider;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.kafka.producer.Sender;
import com.scb.location.model.*;
import com.scb.location.repository.rider.RiderLocationRespository;
import com.scb.location.repository.zone.ZoneRepository;
import com.scb.location.service.zone.ZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RiderLocationServiceImplTest {

    private RiderLocationServiceImpl riderLocationServiceImpl;

    @Mock
    private  RiderLocationRespository riderLocationRespository;
    @Mock
    private  ZoneRepository zoneRepository;
    @Mock
    private ZoneService zoneService;
    @Mock
    private RiderServiceProxy riderServiceProxy;
    @Mock
    private Sender sender;

    @BeforeEach
    void setup() {
        riderLocationServiceImpl = new RiderLocationServiceImpl(riderLocationRespository, zoneService, sender,riderServiceProxy);
    }

    @Test
    void testFindAll(){
        when(riderLocationRespository.findAll()).thenReturn(getListOfRiderLocationRecords(3));
        List<RiderLocationEntity> riderLocationList = riderLocationServiceImpl.findAll();
        assertEquals(3, riderLocationList.size());
        assertEquals("rider_0", riderLocationList.get(0).getRiderId());
        assertEquals("rider_1", riderLocationList.get(1).getRiderId());
        assertEquals("rider_2", riderLocationList.get(2).getRiderId());
    }

    @Test
    void testFindById(){
        when(riderLocationRespository.findById("rider_0"))
                .thenReturn(Optional.of(getListOfRiderLocationRecords(1).get(0)));
        RiderLocationEntity riderLocation = riderLocationServiceImpl.findById("rider_0");
        assertEquals("rider_0", riderLocation.getRiderId());
    }

    @Test
    void testFindByIdNotFound(){
        when(riderLocationRespository.findById("rider_0"))
                .thenReturn(Optional.empty());
        NoResultFoundException exception = assertThrows(
                NoResultFoundException.class, () -> riderLocationServiceImpl.findById("rider_0"));
        assertEquals("Rider rider_0 not found", exception.getMessage());
    }

    @Test
    void testDeleteById(){
        when(riderLocationRespository.findById("rider_0")).thenReturn(Optional.ofNullable(getListOfRiderLocationRecords(1).get(0)));
        doNothing().when(riderLocationRespository).deleteById("rider_0");
        assertDoesNotThrow(
                () -> riderLocationServiceImpl.deleteById("rider_0"));
    }

    @Test
    void testDeleteByIdNotFound(){
        when(riderLocationRespository.findById("rider_0"))
                .thenReturn(Optional.empty());
        NoResultFoundException exception = assertThrows(
                NoResultFoundException.class, () -> riderLocationServiceImpl.deleteById("rider_0"));
        assertEquals("Rider rider_0 not found", exception.getMessage());
    }
    @Test
    void testAddNewRiderLocationEntity(){
        doNothing().when(riderLocationRespository)
                .addNewRider("rider_0", -36.829, 174.896);
        assertDoesNotThrow(
                () -> riderLocationServiceImpl.addNewRiderLocationEntity(
                        "rider_0", -36.829, 174.896)
        );
    }

    @Test
    void testFindAvailableRidersInvalidInput(){
        String[] riders = {"rider_1", "rider_2"};
        Double longitude = null;
        Double latitude = 12.2;
        Integer limit = 5;

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> riderLocationServiceImpl.findAvailableRiders(longitude, latitude, limit));

        assertEquals("Invalid Input", exception.getMessage());
    }

    @Test
    void testFindAvailableRidersEmptyRidersList(){
        List<String> riderList = Arrays.asList(new String[]{});
        Double longitude = 102.2;
        Double latitude = 12.2;
        Integer limit = 5;

        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailableRiders(longitude, latitude, limit));

        assertEquals(0, riderResponseEntityList.size());
    }

    @Test
    void testUpdatedInsertRiderLocationEntity() {
        ZonedDateTime resultDate = ZonedDateTime.parse("2020-01-29T19:10:27+07:00");
        doNothing().when(riderLocationRespository).updateInsertRider("rider_0", -36.829, 174.896,
                resultDate);
        assertDoesNotThrow(() -> riderLocationServiceImpl.updateInsertRiderLocationEntity("rider_0",
                -36.829, 174.896, "2020-01-29T19:10:27+07:00"));
    }

    @ParameterizedTest
    @CsvSource({"2017-02-29", "2017-29-02 19:10:27", "2017-02-29 19:10:27:000"})
    void testUpdatedInsertRiderLocationEntityThrowException(String dateTime) {
        assertThrows(InvalidInputException.class, () -> riderLocationServiceImpl
                .updateInsertRiderLocationEntity("rider_0", -36.829, 174.896, dateTime));
        verifyNoInteractions(riderLocationRespository);
    }


    @Test
    void testFindAvailableRiders(){
        List<String> riderList = Arrays.asList("rider_0", "rider_1", "rider_2");
        when(riderLocationRespository.getNearbyAvailableRiders( anyDouble(),anyDouble(), anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));
        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailableRiders(
                        13.737896, 100.5774, 3)
        );

        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    @Test
    void testFindAvailableRidersWithExcessLimit(){
        List<String> riderList = Arrays.asList("rider_0", "rider_1", "rider_2");
        when(riderLocationRespository.getNearbyAvailableRiders(anyDouble(),anyDouble(), anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));

        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailableRiders(
                        13.737896, 100.5774, 5)
        );

        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    @Test
    void testFindAvailableRidersByBoxType(){
        List<String> riderList = Arrays.asList("rider_0", "rider_1", "rider_2");
        when(riderLocationRespository.getNearbyAvailableRidersByBoxType( anyDouble(),anyDouble(), anyString(),anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));
        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailableRidersByBoxType(
                        13.737896, 100.5774, "LARGE", Boolean.FALSE, 3)
        );

        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    @Test
    void findAvailableRidersByBoxTypeTestWhenIsTrainingCheckRequired() {
        when(riderLocationRespository.getNearbyAvailableRidersByBoxTypeAndIsMartRider( anyDouble(),anyDouble(), anyString(),eq(true),anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));
        List<RiderResponseEntity> riderResponseEntityList = riderLocationServiceImpl.findAvailableRidersByBoxType(
                        13.737896, 100.5774, "LARGE", Boolean.TRUE, 3);
        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    @Test
    void testFindAvailableExpressRiders(){
        List<String> riderList = Arrays.asList("rider_0", "rider_1", "rider_2");
        when(riderLocationRespository.getNearbyAvailableExpressRiders( anyDouble(),anyDouble(), anyBoolean(),anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));
        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailableExpressRiders(
                        13.737896, 100.5774,3)
        );

        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    @Test
    void testFindAvailableRiderException(){
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> riderLocationServiceImpl.findAvailableRiders(0.0,0.0,null));
        assertEquals(exception.getMessage(),"Invalid Input");
    }

    @Test
    void testFindAvailableExpressRiderException(){
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> riderLocationServiceImpl.findAvailableExpressRiders(0.0,0.0,null));
        assertEquals(exception.getMessage(),"Invalid Input for finding nearby express riders");
    }

    @Test
    void testFindAvailableMartRiderException(){
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> riderLocationServiceImpl.findAvailableRidersByBoxType(0.0,0.0,null, Boolean.FALSE,null));
        assertEquals(exception.getMessage(),"Invalid Input for finding nearby riders");
    }

    @Test
    void testPostRiderLocation(){
        RiderLocation riderLocation = RiderLocation.builder()
                .riderId("riderId")
                .lon(100.0)
                .lat(13.08)
                .availabilityStatus(AvailabilityStatus.Active)
                .build();
        RiderProfileDetails riderProfileDetails = RiderProfileDetails.builder().availabilityStatus(AvailabilityStatus.Active.name())
                .build();
        doNothing().when(sender).send(any());

        riderLocationServiceImpl.postRiderLocation("riderId",riderLocation);
        verify(sender, times(1))
                .send(eq(riderLocation));
    }
    @Test
    void testPostRiderLocationWhenAvailabilityStatusIsNull(){
        RiderLocation riderLocation = RiderLocation.builder()
                .riderId("riderId")
                .lon(100.0)
                .lat(13.08)
                .build();
        RiderProfileDetails riderProfileDetails = RiderProfileDetails.builder().availabilityStatus(AvailabilityStatus.Active.name())
                .build();
        when(riderServiceProxy.getRiderProfile("riderId")).thenReturn(riderProfileDetails);
        doNothing().when(sender).send(any());

        riderLocationServiceImpl.postRiderLocation("riderId",riderLocation);
        verify(sender, times(1))
                .send(eq(riderLocation));
    }

    @Test
    void testFindAvailableRidersByBoxTypeWithExcessLimit(){
        List<String> riderList = Arrays.asList("rider_0", "rider_1", "rider_2");
        when(riderLocationRespository.getNearbyAvailableRidersByBoxType(anyDouble(),anyDouble(), anyString(), anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));

        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailableRidersByBoxType(
                        13.737896, 100.5774, "LARGE",Boolean.FALSE,5)
        );

        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    private ZoneEntity getZoneEntity() {
        ZoneEntity zoneEntity = new ZoneEntity();
        zoneEntity.setZoneId(13);
        return zoneEntity;
    }

    private List<RiderLocationEntity> getListOfRiderLocationRecords(int n) {
        List<RiderLocationEntity> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            RiderLocationEntity otpRecord = new RiderLocationEntity("rider_"+i,
                    new GeometryFactory().createPoint(new Coordinate(-36.829-i, 174.896-i)), 10.0, AvailabilityStatus.Active.name(),RiderStatus.AUTHORIZED.name(),false, false, "1","LARGE",false,false,false);
            list.add(otpRecord);
        }
        return list;
    }
    @Test
    void testFindAvailablePointXRiders(){
        List<String> riderList = Arrays.asList("rider_0", "rider_1", "rider_2");
        when(riderLocationRespository.getNearbyAvailablePointxRiders( anyDouble(),anyDouble(), anyBoolean(),anyInt()))
                .thenReturn(getListOfRiderLocationRecords(3));
        List<RiderResponseEntity> riderResponseEntityList = assertDoesNotThrow(
                () -> riderLocationServiceImpl.findAvailablePointxRiders(
                        13.737896, 100.5774,3)
        );

        assertEquals(3, riderResponseEntityList.size());
        assertEquals("rider_0", riderResponseEntityList.get(0).getRiderId());
        assertEquals("rider_1", riderResponseEntityList.get(1).getRiderId());
        assertEquals("rider_2", riderResponseEntityList.get(2).getRiderId());
    }

    @Test
    void testFindAvailablePointXRiderException(){
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> riderLocationServiceImpl.findAvailablePointxRiders(0.0,0.0,null));
        assertEquals(exception.getMessage(),"Invalid Input for finding nearby pointx riders");
    }
}

