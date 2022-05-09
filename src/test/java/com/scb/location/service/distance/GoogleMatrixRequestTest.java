package com.scb.location.service.distance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.config.GoogleMapsConfig;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.exception.RetryableException;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.SubDistricts.GeocodeResult;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GoogleMatrixRequestTest {


    private GoogleMatrixRequest googleMatrixRequest;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GoogleMapsConfig googleMapsConfig;

    @Mock
    MeterRegistry meterRegistry;

    @BeforeEach
    public void setup() {
        when(googleMapsConfig.getUrl()).thenReturn("https://www.mock-url.com/");
        when(googleMapsConfig.getReverseGeocodeUrl()).thenReturn("https://www.mock-url.com/");
        when(googleMapsConfig.getApiKey()).thenReturn("mock-api-key");
        googleMatrixRequest = new GoogleMatrixRequest(restTemplate, googleMapsConfig, meterRegistry);
    }



    @Test
    public void testGetLocation() throws IOException {
        when(googleMatrixRequest.getResponse(any(), any())).thenReturn(getValidResponseJsonString());
        DistanceResponseEntity distanceResponseEntity = googleMatrixRequest.getDistance(100.53,13.75, 100.23, 13.54);
        assertEquals(62551.0,distanceResponseEntity.getDistance());
        assertEquals(3372.0, distanceResponseEntity.getDuration());
    }

    @Test
    public void testGetLocationWhenNoRoutesExist() {
        when(googleMatrixRequest.getResponse(any(), any())).thenReturn(getNoRoutesExistResponseJsonString());
        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
                () -> googleMatrixRequest.getDistance(100.53,13.75, 100.423442,13.430346));
        assertEquals("No Routes Exists", exception.getMessage());
    }
    @Test
    public void testGetLocationWhenNullResponse() {
        when(googleMatrixRequest.getResponse(any(), any())).thenReturn(null);
        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
                () -> googleMatrixRequest.getDistance(100.53,13.75, 100.423442,13.430346));
        assertEquals("No Routes Exists", exception.getMessage());
    }

    @Test
    public void testNoRoutesExistException() {
        String errorResponse = "{\"errorCode\":\"404\",\"errorMessage\":\"Failure\"}";
        when(googleMatrixRequest.getResponse(any() , any()))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponse.getBytes(), StandardCharsets.UTF_8));

        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
                () -> googleMatrixRequest.getDistance(100.53, 13.75, 100.423442, 13.430346));
        assertEquals("No Routes Exists", exception.getMessage());
    }

    @Test
    public void testGetAddress() throws JsonProcessingException {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(getGeocodeResult());
        GeocodeResult response = googleMatrixRequest.getAddress(13.23,100.9);
        assertNotNull(response);

    }

    @Test
    public void testGetAddressWithNullResponse() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);
        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
                () -> googleMatrixRequest.getAddress(100.53,13.75));
        assertEquals("No Address Found", exception.getMessage());
    }

    @Test
    public void testGetAddressWithSocketError() {
        when(restTemplate.getForObject(anyString(), any())).thenThrow(new ResourceAccessException("Failed to connect"));
        RetryableException exception = assertThrows(RetryableException.class,
                () -> googleMatrixRequest.getAddress(100.53,13.75));
        assertEquals("Error accessing google service", exception.getMessage());
    }

    @Test
    public void testGetAddressUnknownError() {
        GeocodeResult geocodeResult = GeocodeResult.builder().results(null).status("UNKNOWN_ERROR").build();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(geocodeResult);
        RetryableException exception = assertThrows(RetryableException.class,
                () -> googleMatrixRequest.getAddress(100.53,13.75));
        assertEquals("Address api failed with:UNKNOWN_ERROR", exception.getMessage());

    }

    @Test
    public void testGetAddressOverQueryLimit() {
        GeocodeResult geocodeResult = GeocodeResult.builder().results(null).status("OVER_QUERY_LIMIT").build();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(geocodeResult);
        assertDoesNotThrow(() -> googleMatrixRequest.getAddress(100.53,13.75));

    }

    @Test
    public void testGetAddressERROR() {
        GeocodeResult geocodeResult = GeocodeResult.builder().results(null).status("ERROR").build();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(geocodeResult);
        RetryableException exception = assertThrows(RetryableException.class,
                () -> googleMatrixRequest.getAddress(100.53,13.75));
        assertEquals("Address api failed with:ERROR", exception.getMessage());

    }

    @Test
    public void testGetAddressZERO_RESULTS_ERROR() {
        GeocodeResult geocodeResult = GeocodeResult.builder().results(null).status("ZERO_RESULTS").build();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(geocodeResult);
        NoResultFoundException exception = assertThrows(NoResultFoundException.class,
                () -> googleMatrixRequest.getAddress(100.53,13.75));
        assertEquals("No Address Found", exception.getMessage());

    }

    private GeocodeResult getGeocodeResult() throws JsonProcessingException {

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

    private String getNoRoutesExistResponseJsonString() {
        return "{\n" +
                "    \"destination_addresses\": [\n" +
                "        \"13.430346,100.423442\"\n" +
                "    ],\n" +
                "    \"origin_addresses\": [\n" +
                "        \"13.75,100.53\"\n" +
                "    ],\n" +
                "    \"rows\": [\n" +
                "        {\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"status\": \"ZERO_RESULTS\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"OK\"\n" +
                "}";
    }


    private String getValidResponseJsonString() {
        return "{\n" +
                "    \"destination_addresses\": [\n" +
                "        \"เลขที่ 30/17 17 Mu 2 Ban Bang Phai Tia Rd, Tambon Bang Kachao, Amphoe Mueang Samut Sakhon, Chang Wat Samut Sakhon 74000, Thailand\"\n" +
                "    ],\n" +
                "    \"origin_addresses\": [\n" +
                "        \"334/2 Phayathai Rd, Khwaeng Thanon Phetchaburi, Khet Ratchathewi, Krung Thep Maha Nakhon 10400, Thailand\"\n" +
                "    ],\n" +
                "    \"rows\": [\n" +
                "        {\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"distance\": {\n" +
                "                        \"text\": \"62.6 km\",\n" +
                "                        \"value\": 62551\n" +
                "                    },\n" +
                "                    \"duration\": {\n" +
                "                        \"text\": \"56 mins\",\n" +
                "                        \"value\": 3372\n" +
                "                    },\n" +
                "                    \"status\": \"OK\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"OK\"\n" +
                "}";
    }
}
