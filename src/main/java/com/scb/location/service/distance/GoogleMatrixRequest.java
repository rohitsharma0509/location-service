package com.scb.location.service.distance;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scb.location.config.GoogleMapsConfig;
import com.scb.location.constant.Constants;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.exception.RetryableException;
import com.scb.location.model.DistanceResponseEntity;
import com.scb.location.model.SubDistricts.AddressComponent;
import com.scb.location.model.SubDistricts.AddressResponse;
import com.scb.location.model.SubDistricts.GeocodeResult;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Service
public class GoogleMatrixRequest {

    private final MeterRegistry meterRegistry;
    private final RestTemplate restTemplate;
    private final String url;
    private final String reverseGeocodeUrl;
    private final String apiKey;


    @SneakyThrows
    @Autowired
    public GoogleMatrixRequest(RestTemplate restTemplate, GoogleMapsConfig googleMapsConfig, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.url = googleMapsConfig.getUrl();
        this.reverseGeocodeUrl = googleMapsConfig.getReverseGeocodeUrl();
        this.apiKey = googleMapsConfig.getApiKey();
        this.meterRegistry = meterRegistry;
    }



    @Timed(value = Constants.GET_DISTANCE_GOOGLE, description = Constants.GET_DISTANCE_DESCRIPTION)
    @Retryable( value = NoResultFoundException.class,
            maxAttempts = 3, backoff = @Backoff(delay = 50))
    public DistanceResponseEntity getDistance(Double longitudeFrom, Double latitudeFrom, Double longitudeTo, Double latitudeTo) throws IOException {

        log.info("Invoking api:");
        String origins = getLocation(latitudeFrom, longitudeFrom);
        String destinations = getLocation(latitudeTo, longitudeTo);

        try {
            String res =  getResponse(origins, destinations);
            if(res != null) {
                JsonObject o = JsonParser.parseString(res).getAsJsonObject();
                JsonArray rows = o.getAsJsonArray("rows");

                JsonObject elements = (JsonObject) rows.get(0);
                JsonObject element = (JsonObject) elements.getAsJsonArray("elements").get(0);
                String statusJson =  element.get("status").getAsString();
                if(statusJson.equals("ZERO_RESULTS"))
                    throw new NoResultFoundException("No Routes Exists");
                JsonObject distanceJson = (JsonObject) element.get("distance");
                Double distance = distanceJson.get("value").getAsDouble();

                JsonObject durationJson = (JsonObject) element.get("duration");
                Double duration = durationJson.get("value").getAsDouble();


                return DistanceResponseEntity.
                                         builder().
                                         longitudeFrom(longitudeFrom).
                                         latitudeFrom(latitudeFrom).
                                         longitudeTo(longitudeTo).
                                         latitudeTo(latitudeTo).
                                         distance(distance).
                                         duration(duration).build();

            }
            else {
                throw new NoResultFoundException("No Routes Exists");
            }

        }
        catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Api request error; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            throw new NoResultFoundException("No Routes Exists");
        }
        catch (ResourceAccessException ex) {
            log.error("Error accessing google service",ex);
            throw new NoResultFoundException("Error accessing google service");
        }

    }

    public String getResponse(String origins, String destinations) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("origins", origins)
                .queryParam("destinations",destinations)
                .queryParam("key", apiKey);
        return restTemplate.getForObject(builder.toUriString(), String.class);
    }

    private String getLocation(Double latitude, Double longitude) {
        return latitude + "," +
                longitude;
    }


    @Timed(value = Constants.GET_ADDRESS_GOOGLE, description = Constants.GET_ADDRESS_DESCRIPTION)
    @Retryable( value = RetryableException.class,
            maxAttempts = 3, backoff = @Backoff(delay = 50))
    public GeocodeResult getAddress(double latitude, double longitude) throws JsonProcessingException {
        try {

            String latlng = getLatLng(latitude, longitude);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(reverseGeocodeUrl)
                    .queryParam("latlng", latlng)
                    .queryParam("language", "th")
                    .queryParam("key", apiKey);
            log.info("Fetching address for {}", latlng);
            GeocodeResult response = restTemplate.getForObject(builder.toUriString(), GeocodeResult.class);
            if (response == null || response.getStatus() == null
                    || response.getStatus().equalsIgnoreCase("ZERO_RESULTS")) {
                throw new NoResultFoundException("No Address Found");
            }
            if (response.getStatus().equalsIgnoreCase("UNKNOWN_ERROR") ||
                    response.getStatus().equalsIgnoreCase("ERROR")) {
                log.error("Address api failed with:{}", response.getStatus());
                log.info("Retrying");
                throw new RetryableException("Address api failed with:" + response.getStatus());
            }

            return response;
        }catch (ResourceAccessException ex) {
            log.error("Error accessing google service",ex);
            throw new RetryableException("Error accessing google service");
        }
    }

    @Recover
    public GeocodeResult getBackendResponseFallback(RetryableException e){
        log.info("Address api failed even after retry.{}",e.getMessage());
        return GeocodeResult.builder().results(Collections.emptyList()).build();


    }


    private String getLatLng(double latitude, double longitude) {
        return latitude + "," + longitude;

    }


}