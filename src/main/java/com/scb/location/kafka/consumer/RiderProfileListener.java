package com.scb.location.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.model.RiderProfileEventModel;
import com.scb.location.service.rider.RiderProfileUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.io.IOException;
import java.util.Set;

@Component
@Slf4j
public class RiderProfileListener {

  private final ObjectMapper objectMapper;

  private final RiderProfileUpdateService riderProfileUpdateService;

  @Autowired
  public RiderProfileListener(ObjectMapper objectMapper, RiderProfileUpdateService riderProfileUpdateService) {
    this.objectMapper = objectMapper;
    this.riderProfileUpdateService = riderProfileUpdateService;
  }

  @KafkaListener(topics = "${kafka.profile-update-topic}")
  public void receive(@Payload String data) throws IOException {
    log.info("message received: " + data);
    RiderProfileEventModel riderProfileEventModel = objectMapper.readValue(data, RiderProfileEventModel.class);
    validateMessage(riderProfileEventModel);
    if(riderProfileEventModel != null){
      riderProfileUpdateService.updateRiderProfileData(riderProfileEventModel);
    }

  }

  public void validateMessage(Object obj) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<Object>> violations = validator.validate(obj);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
