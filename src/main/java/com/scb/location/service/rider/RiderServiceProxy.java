package com.scb.location.service.rider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.exception.ErrorResponse;
import com.scb.location.exception.RiderProfileServiceException;
import com.scb.location.model.RiderProfileDetails;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RiderServiceProxy {


  private RestTemplate restTemplate;
  private String riderProfilePath;
  private ObjectMapper objectMapper;

  @Autowired
  public RiderServiceProxy(RestTemplate restTemplate,
      @Value("${riderService.path}") String riderProfilePath,ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.riderProfilePath = riderProfilePath;
    this.objectMapper = objectMapper;
  }

  public boolean validateRider(String riderProfileId, String phoneNumber) {
    String uri = riderProfilePath.concat("/profile/").concat(riderProfileId);
    log.info("Invoking get rider profile details api:{}", uri);
    try {
      ResponseEntity<RiderProfileDetails> responseEntity =
              restTemplate.getForEntity(uri, RiderProfileDetails.class);
      log.info("Api invocation successful");
      RiderProfileDetails dto = responseEntity.getBody();
      return !ObjectUtils.isEmpty(dto) && dto.getPhoneNumber().equals(phoneNumber);
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      log.error("Api request error; ErrorCode:{} ; Message:{}", ex.getStatusCode(),
          ex.getResponseBodyAsString());
    }
    return false;
  }

  public RiderProfileDetails getRiderProfile(String riderId) {
    String url = riderProfilePath + "/profile/" + riderId;
    log.info("Invoking get rider profile api:{}", url);
    try {
      RiderProfileDetails responseEntity =
              restTemplate.getForObject(url, RiderProfileDetails.class);
      log.info("Api invocation successful");
      return responseEntity;
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      log.error(
              "Api request error; ErrorCode:{} ; Message:{}",
              ex.getStatusCode(),
              ex.getResponseBodyAsString());
      ErrorResponse error = parseErrorResponse(ex.getResponseBodyAsString());
      throw new RiderProfileServiceException(error.getErrorMessage());
    }
  }

  @SneakyThrows
  private ErrorResponse parseErrorResponse(String errorResponse){
    return objectMapper.readValue(errorResponse, ErrorResponse.class);

  }



}
