package com.scb.location.service.distance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.exception.InvalidInputException;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.ZoneEntity;
import com.scb.location.model.SubDistricts.AddressResponse;
import com.scb.location.model.SubDistricts.GeocodeResult;
import com.scb.location.repository.zone.ZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.Optional;
import java.util.function.ObjLongConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DistanceServiceImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private DistanceServiceImpl distanceService ;

    @Mock
    private GoogleMatrixRequest googleMatrixRequest;
    
    @Mock
    private ZoneRepository zoneRepository;
    
    @Test
    public void testGetDistanceValidInput() throws IOException {
        when(googleMatrixRequest.getDistance(100.53,13.75,
                100.23,13.54)).thenReturn(getDistanceResponseEntity());

        DistanceResponseEntity distanceResponseEntity =  distanceService.getDistance(100.53,13.75,
                100.23,13.54);

        assertEquals(62551.0,distanceResponseEntity.getDistance());
        assertEquals(3372.0, distanceResponseEntity.getDuration());

    }

    @Test
    void testGetDistanceInvalidInputNullValues(){

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> distanceService.getDistance(100.53,13.75,
                        100.23,null));
        assertEquals("Mandatory fields missing", exception.getMessage());
    }

    @Test
    void testGetDistanceInvalidInput() throws IOException{

        when(googleMatrixRequest.getDistance(-1000.23,130.75,
                2000.23,200.89)).thenThrow(new IOException("No Routes Exists"));
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> distanceService.getDistance(-1000.23,130.75,
                        2000.23,200.89));
        assertEquals("No Routes Exists", exception.getMessage());
    }
    @Test
    void testGetAddressInvalidLocation() throws IOException{

        when(googleMatrixRequest.getAddress(anyDouble(), anyDouble())).thenReturn(getInvalidGeocodeResult());
        AddressResponse addressResponse = distanceService.getAddress(100.23, 12.23);

        assertEquals("", addressResponse.getSubDistrict());
    }

    private GeocodeResult getInvalidGeocodeResult() throws JsonProcessingException {
        String jsonResponse = "{\n" +
                "  \"plus_code\": {\n" +
                "    \"global_code\": \"7M63C3XF+X7\"\n" +
                "  },\n" +
                "  \"results\": [\n" +
                "    \n" +
                "  ],\n" +
                "  \"status\": \"ZERO_RESULTS\"\n" +
                "}";
        return objectMapper.readValue(jsonResponse, GeocodeResult.class);

    }

    @Test
    public void testGetAddressValidInput() throws IOException {
        when(googleMatrixRequest.getAddress(13.803611,100.6075))
                .thenReturn(getLevel2GeocodeResult());
        when(zoneRepository.getMerchantZone(any(), any())).thenReturn(
            Optional.of(ZoneEntity.builder().zoneId(76).build())
            );
        AddressResponse response = distanceService.getAddress(100.6075, 13.803611);

        assertNotNull(response);

    }
    
    @Test
    public void testGetLocalityAddressValidInput() throws IOException {
        when(googleMatrixRequest.getAddress(13.803611,100.6075))
                .thenReturn(getLocalityGeocodeResult());
        when(zoneRepository.getMerchantZone(any(), any())).thenReturn(
            Optional.of(ZoneEntity.builder().zoneId(76).build())
            );
        AddressResponse response = distanceService.getAddress(100.6075, 13.803611);

        assertNotNull(response);

    }

    @Test
    public void testGetAddressValidLevel1Input() throws IOException {
        when(googleMatrixRequest.getAddress(13.803611,100.6075))
                .thenReturn(getLevel1GeocodeResult());
        
        when(zoneRepository.getMerchantZone(any(), any())).thenReturn(
            Optional.of(ZoneEntity.builder().zoneId(1).build())
            );
        
        AddressResponse response = distanceService.getAddress(100.6075, 13.803611);

        assertNotNull(response);

    }
    
    private GeocodeResult getLocalityGeocodeResult() throws JsonProcessingException {

      String jsonResponse = "{\n" +
              "  \"plus_code\": {\n" +
              "    \"compound_code\": \"RJ35+C2 Bangkok, Thailand\",\n" +
              "    \"global_code\": \"7P52RJ35+C2\"\n" +
              "  },\n" +
              "  \"results\": [\n" +
              "    {\n" +
              "      \"address_components\": [\n" +
              "        {\n" +
              "          \"long_name\": \"77\",\n" +
              "          \"short_name\": \"77\",\n" +
              "          \"types\": [\n" +
              "            \"locality\"\n" +
              "          ]\n" +
              "        },\n" +
              "        {\n" +
              "          \"long_name\": \"Nak Niwat Road\",\n" +
              "          \"short_name\": \"Nak Niwat Rd\",\n" +
              "          \"types\": [\n" +
              "            \"route\"\n" +
              "          ]\n" +
              "        },\n" +
              "        {\n" +
              "          \"long_name\": \"Khwaeng Lat Phrao\",\n" +
              "          \"short_name\": \"Khwaeng Lat Phrao\",\n" +
              "          \"types\": [\n" +
              "            \"political\",\n" +
              "            \"locality\",\n" +
              "            \"sublocality_level_2\"\n" +
              "          ]\n" +
              "        },\n" +
              "        {\n" +
              "          \"long_name\": \"Khet Lat Phrao\",\n" +
              "          \"short_name\": \"Khet Lat Phrao\",\n" +
              "          \"types\": [\n" +
              "            \"political\",\n" +
              "            \"sublocality\",\n" +
              "            \"sublocality_level_1\"\n" +
              "          ]\n" +
              "        },\n" +
              "        {\n" +
              "          \"long_name\": \"Krung Thep Maha Nakhon\",\n" +
              "          \"short_name\": \"Krung Thep Maha Nakhon\",\n" +
              "          \"types\": [\n" +
              "            \"administrative_area_level_1\",\n" +
              "            \"political\"\n" +
              "          ]\n" +
              "        },\n" +
              "        {\n" +
              "          \"long_name\": \"Thailand\",\n" +
              "          \"short_name\": \"TH\",\n" +
              "          \"types\": [\n" +
              "            \"country\",\n" +
              "            \"political\"\n" +
              "          ]\n" +
              "        },\n" +
              "        {\n" +
              "          \"long_name\": \"10230\",\n" +
              "          \"short_name\": \"10230\",\n" +
              "          \"types\": [\n" +
              "            \"postal_code\"\n" +
              "          ]\n" +
              "        }\n" +
              "      ],\n" +
              "      \"formatted_address\": \"77 Nak Niwat Rd, Khwaeng Lat Phrao, Khet Lat Phrao, Krung Thep Maha Nakhon 10230, Thailand\",\n" +
              "      \"geometry\": {\n" +
              "        \"location\": {\n" +
              "          \"lat\": 13.8035942,\n" +
              "          \"lng\": 100.6075378\n" +
              "        },\n" +
              "        \"location_type\": \"ROOFTOP\",\n" +
              "        \"viewport\": {\n" +
              "          \"northeast\": {\n" +
              "            \"lat\": 13.8049431802915,\n" +
              "            \"lng\": 100.6088867802915\n" +
              "          },\n" +
              "          \"southwest\": {\n" +
              "            \"lat\": 13.8022452197085,\n" +
              "            \"lng\": 100.6061888197085\n" +
              "          }\n" +
              "        }\n" +
              "      },\n" +
              "      \"place_id\": \"ChIJWQKib5Kd4jARw4vRXvFMcms\",\n" +
              "      \"plus_code\": {\n" +
              "        \"compound_code\": \"RJ35+C2 Bangkok, Thailand\",\n" +
              "        \"global_code\": \"7P52RJ35+C2\"\n" +
              "      },\n" +
              "      \"types\": [\n" +
              "        \"street_address\"\n" +
              "      ]\n" +
              "    }\n" +
              "  ],\n" +
              "  \"status\": \"OK\"\n" +
              "}";

      return objectMapper.readValue(jsonResponse, GeocodeResult.class);
  }

    private GeocodeResult getLevel2GeocodeResult() throws JsonProcessingException {

        String jsonResponse = "{\n" +
                "  \"plus_code\": {\n" +
                "    \"compound_code\": \"RJ35+C2 Bangkok, Thailand\",\n" +
                "    \"global_code\": \"7P52RJ35+C2\"\n" +
                "  },\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"address_components\": [\n" +
                "        {\n" +
                "          \"long_name\": \"77\",\n" +
                "          \"short_name\": \"77\",\n" +
                "          \"types\": [\n" +
                "            \"street_number\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Nak Niwat Road\",\n" +
                "          \"short_name\": \"Nak Niwat Rd\",\n" +
                "          \"types\": [\n" +
                "            \"route\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Khwaeng Lat Phrao\",\n" +
                "          \"short_name\": \"Khwaeng Lat Phrao\",\n" +
                "          \"types\": [\n" +
                "            \"political\",\n" +
                "            \"sublocality\",\n" +
                "            \"sublocality_level_2\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Khet Lat Phrao\",\n" +
                "          \"short_name\": \"Khet Lat Phrao\",\n" +
                "          \"types\": [\n" +
                "            \"political\",\n" +
                "            \"sublocality\",\n" +
                "            \"sublocality_level_1\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Krung Thep Maha Nakhon\",\n" +
                "          \"short_name\": \"Krung Thep Maha Nakhon\",\n" +
                "          \"types\": [\n" +
                "            \"administrative_area_level_1\",\n" +
                "            \"political\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Thailand\",\n" +
                "          \"short_name\": \"TH\",\n" +
                "          \"types\": [\n" +
                "            \"country\",\n" +
                "            \"political\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"10230\",\n" +
                "          \"short_name\": \"10230\",\n" +
                "          \"types\": [\n" +
                "            \"postal_code\"\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"formatted_address\": \"77 Nak Niwat Rd, Khwaeng Lat Phrao, Khet Lat Phrao, Krung Thep Maha Nakhon 10230, Thailand\",\n" +
                "      \"geometry\": {\n" +
                "        \"location\": {\n" +
                "          \"lat\": 13.8035942,\n" +
                "          \"lng\": 100.6075378\n" +
                "        },\n" +
                "        \"location_type\": \"ROOFTOP\",\n" +
                "        \"viewport\": {\n" +
                "          \"northeast\": {\n" +
                "            \"lat\": 13.8049431802915,\n" +
                "            \"lng\": 100.6088867802915\n" +
                "          },\n" +
                "          \"southwest\": {\n" +
                "            \"lat\": 13.8022452197085,\n" +
                "            \"lng\": 100.6061888197085\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"place_id\": \"ChIJWQKib5Kd4jARw4vRXvFMcms\",\n" +
                "      \"plus_code\": {\n" +
                "        \"compound_code\": \"RJ35+C2 Bangkok, Thailand\",\n" +
                "        \"global_code\": \"7P52RJ35+C2\"\n" +
                "      },\n" +
                "      \"types\": [\n" +
                "        \"street_address\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"OK\"\n" +
                "}";

        return objectMapper.readValue(jsonResponse, GeocodeResult.class);
    }

    private GeocodeResult getLevel1GeocodeResult() throws JsonProcessingException {

        String jsonResponse = "{\n" +
                "  \"plus_code\": {\n" +
                "    \"compound_code\": \"QFXV+GJ Bang Kruai, Bang Kruai District, Nonthaburi, Thailand\",\n" +
                "    \"global_code\": \"7P52QFXV+GJ\"\n" +
                "  },\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"address_components\": [\n" +
                "        {\n" +
                "          \"long_name\": \"Si Rat-Outer Ring Road Express Way\",\n" +
                "          \"short_name\": \"Si Rat-Outer Ring Rd Express Way\",\n" +
                "          \"types\": [\n" +
                "            \"route\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Khet Bang Phlat\",\n" +
                "          \"short_name\": \"Khet Bang Phlat\",\n" +
                "          \"types\": [\n" +
                "            \"political\",\n" +
                "            \"sublocality\",\n" +
                "            \"sublocality_level_1\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Krung Thep Maha Nakhon\",\n" +
                "          \"short_name\": \"Krung Thep Maha Nakhon\",\n" +
                "          \"types\": [\n" +
                "            \"administrative_area_level_1\",\n" +
                "            \"political\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"Thailand\",\n" +
                "          \"short_name\": \"TH\",\n" +
                "          \"types\": [\n" +
                "            \"country\",\n" +
                "            \"political\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"long_name\": \"10700\",\n" +
                "          \"short_name\": \"10700\",\n" +
                "          \"types\": [\n" +
                "            \"postal_code\"\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"formatted_address\": \"Si Rat-Outer Ring Rd Express Way, Khet Bang Phlat, Krung Thep Maha Nakhon 10700, Thailand\",\n" +
                "      \"geometry\": {\n" +
                "        \"bounds\": {\n" +
                "          \"northeast\": {\n" +
                "            \"lat\": 13.7983875,\n" +
                "            \"lng\": 100.4951943\n" +
                "          },\n" +
                "          \"southwest\": {\n" +
                "            \"lat\": 13.7968887,\n" +
                "            \"lng\": 100.491184\n" +
                "          }\n" +
                "        },\n" +
                "        \"location\": {\n" +
                "          \"lat\": 13.7976381,\n" +
                "          \"lng\": 100.4931892\n" +
                "        },\n" +
                "        \"location_type\": \"GEOMETRIC_CENTER\",\n" +
                "        \"viewport\": {\n" +
                "          \"northeast\": {\n" +
                "            \"lat\": 13.7989870802915,\n" +
                "            \"lng\": 100.4951943\n" +
                "          },\n" +
                "          \"southwest\": {\n" +
                "            \"lat\": 13.7962891197085,\n" +
                "            \"lng\": 100.491184\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"place_id\": \"ChIJZ-KJ5s6b4jARRsgun6YxW1U\",\n" +
                "      \"types\": [\n" +
                "        \"route\"\n" +
                "      ]\n" +
                "    }\n" +
                "      ],\n" +
                "  \"status\": \"OK\"\n" +
                "}";

        return objectMapper.readValue(jsonResponse, GeocodeResult.class);
    }

    @Test
    void testGetAddressInvalidInputNullValues(){

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> distanceService.getAddress(100.53,null));
        assertEquals("Mandatory fields missing", exception.getMessage());
    }

    private DistanceResponseEntity getDistanceResponseEntity() {
        return DistanceResponseEntity.
                builder().
                longitudeFrom(100.53).
                latitudeFrom(13.75).
                longitudeTo(100.23).
                latitudeTo(13.54).
                distance(62551.0).
                duration(3372.0).
                build();
    }


}
