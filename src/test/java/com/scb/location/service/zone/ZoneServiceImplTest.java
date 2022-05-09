package com.scb.location.service.zone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.*;

import com.scb.location.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scb.location.exception.NoResultFoundException;
import com.scb.location.repository.zone.ZoneRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
public class ZoneServiceImplTest {

    private ZoneServiceImpl zoneServiceImpl;

    @Mock
    private ZoneRepository zoneRepository;

    @BeforeEach
    void setup() {
        zoneServiceImpl = new ZoneServiceImpl(zoneRepository);
    }

    @Test
    void testGetMerchantZone(){
        Point expectedPoint = new GeometryFactory().createPoint(new Coordinate(-36.829, 174.896));
        when(zoneRepository.getMerchantZone(-36.829, 174.896)).thenReturn(Optional.of(getZoneRecord()));
        ZoneEntity zoneEntity = zoneServiceImpl.getMerchantZone(-36.829, 174.896);
        assertEquals(1, zoneEntity.getZoneId());
        assertEquals(10001, zoneEntity.getPostalCode());
        assertEquals("province_0: zone_0", zoneEntity.getZoneName());
        assertEquals(expectedPoint, zoneEntity.getGeom());
        assertEquals(50, zoneEntity.getMaxDistanceToBroadcast());

    }

    @Test
    void testGetMerchantZoneNotFound(){
        when(zoneRepository.getMerchantZone(-36.829, 174.896)).thenReturn(Optional.empty());
        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
            () -> zoneServiceImpl.getMerchantZone(-36.829, 174.896));
        assertEquals("Zone of the merchant not found", exception.getMessage());

    }

    private ZoneEntity getZoneRecord() {
        return new ZoneEntity(1,10001, "zone_0",
            new GeometryFactory().createPoint(new Coordinate(-36.829, 174.896)), 10, 2, 50,
                new ProvinceEntity(1,  "province_0"),1);
    }
    
    @Test
     void testGetAllZones() {
    	when(zoneRepository.getAllZones()).thenReturn(generateProvinceZoneProjectionList());
    	List<ZoneName> allZones = zoneServiceImpl.getAllZones();
    	assertEquals(5, allZones.size());
    }

    @Test
     void testGetAllZonesWithEmptyList() {
        List<ProvinceZoneProjection> list = new ArrayList<>();
        when(zoneRepository.getAllZones()).thenReturn(list);
        List<ZoneName> allZones = zoneServiceImpl.getAllZones();
        assertTrue(allZones.isEmpty());
    }

    @Test
    void testGetAllZonesInSortedOrder() {
        String BANGKOK_PREFIX = "กรุงเทพฯ";
        when(zoneRepository.getAllZones()).thenReturn(generateProvinceZoneProjectionList());
        List<ZoneName> allZones = zoneServiceImpl.getAllZones();
        assertFalse(CollectionUtils.isEmpty(allZones));
        //Having province == BANGKOK_PREFIX
        assertEquals(BANGKOK_PREFIX, allZones.get(0).getProvince());
        assertEquals("กรุงเทพฯ: Khlong Toei", allZones.get(0).getName());
        assertEquals(BANGKOK_PREFIX, allZones.get(1).getProvince());
        assertEquals("กรุงเทพฯ: Khlong Toei 2", allZones.get(1).getName());

        //Having province != BANGKOK_PREFIX
        assertNotEquals(BANGKOK_PREFIX, allZones.get(2).getProvince());
        assertEquals("กรุงเท: Khlong Toei 3", allZones.get(2).getName());
        assertNotEquals(BANGKOK_PREFIX, allZones.get(3).getProvince());
        assertEquals("กรุงเท: Khlong Toei 4", allZones.get(3).getName());
        assertNotEquals(BANGKOK_PREFIX, allZones.get(4).getProvince());
        assertEquals("กรุงเท: Khlong Toei 5", allZones.get(4).getName());
    }

    @Test
    void getAllZoneIdByProvinceTest(){
        List<Integer> zoneIdList = Arrays.asList(1, 2, 3);
        Optional<List<Integer>> optionalIntegerList = Optional.of(zoneIdList);
        when(zoneRepository.getAllZoneIdByProvince(1)).thenReturn(optionalIntegerList);
        List<Integer> response = zoneServiceImpl.getAllZoneIdByProvince(1);
        assertEquals(zoneIdList.size(), response.size());
    }

    @Test
    void getAllZoneIdByProvinceWithEmptyListTest(){
        Optional<List<Integer>> optionalIntegerList = Optional.empty();
        when(zoneRepository.getAllZoneIdByProvince(1)).thenReturn(optionalIntegerList);
        assertThrows(NoResultFoundException.class, () -> zoneServiceImpl.getAllZoneIdByProvince(1));
    }

    @Test
    void getProvinceIdByZoneIdTest(){
        ProvinceDto provinceDto = new ProvinceDto(){
            public Integer getProvinceId(){
                return 1;
            }
            public String getName(){
                return "abc";
            }
        };
        when(zoneRepository.getProvinceByZoneId(1)).thenReturn(provinceDto);
        ProvinceDto response = zoneServiceImpl.getProvinceIdByZoneId(1);
        assertEquals(1, response.getProvinceId());
        assertEquals("abc", response.getName());
    }

    @Test
    void getProvinceIdByZoneIdWithEmptyOptionalTest(){
        when(zoneRepository.getProvinceByZoneId(1)).thenReturn(null);
        assertThrows(NoResultFoundException.class, () -> zoneServiceImpl.getProvinceIdByZoneId(1));
    }

    private List<ProvinceZoneProjection> generateProvinceZoneProjectionList(){
        ProvinceZoneProjection provinceZoneProjection1 = new ProvinceZoneProjection() {
            public Integer getZoneId() {
                return 1;
            }
            public String getZoneName(){
                return "Khlong Toei";
            }
            public Integer getProvinceId(){
                return 1;
            }
            public String getProvinceName(){
                return "กรุงเทพฯ";
            }
        };
        ProvinceZoneProjection provinceZoneProjection2 = new ProvinceZoneProjection() {
            public Integer getZoneId() {
                return 2;
            }
            public String getZoneName(){
                return "Khlong Toei 2";
            }
            public Integer getProvinceId(){
                return 1;
            }
            public String getProvinceName(){
                return "กรุงเทพฯ";
            }
        };
        ProvinceZoneProjection provinceZoneProjection3 = new ProvinceZoneProjection() {
            public Integer getZoneId() {
                return 3;
            }
            public String getZoneName(){
                return "Khlong Toei 3";
            }
            public Integer getProvinceId(){
                return 2;
            }
            public String getProvinceName(){
                return "กรุงเท";
            }
        };
        ProvinceZoneProjection provinceZoneProjection4 = new ProvinceZoneProjection() {
            public Integer getZoneId() {
                return 4;
            }
            public String getZoneName(){
                return "Khlong Toei 4";
            }
            public Integer getProvinceId(){
                return 2;
            }
            public String getProvinceName(){
                return "กรุงเท";
            }
        };
        ProvinceZoneProjection provinceZoneProjection5 = new ProvinceZoneProjection() {
            public Integer getZoneId() {
                return 5;
            }
            public String getZoneName(){
                return "Khlong Toei 5";
            }
            public Integer getProvinceId(){
                return 2;
            }
            public String getProvinceName(){
                return "กรุงเท";
            }
        };

        return Arrays.asList(
                //Don't change the order
                provinceZoneProjection1,
                provinceZoneProjection5,
                provinceZoneProjection2,
                provinceZoneProjection3,
                provinceZoneProjection4
        );
    }
}
