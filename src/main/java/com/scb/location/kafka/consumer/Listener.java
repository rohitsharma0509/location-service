package com.scb.location.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.model.RiderLocation;
import com.scb.location.service.rider.RiderLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Component
@Slf4j
public class Listener {

  private final ObjectMapper objectMapper;

  private final RiderLocationService riderLocationService;

  @Autowired
  public Listener(ObjectMapper objectMapper, RiderLocationService riderLocationService) {
    this.objectMapper = objectMapper;
    this.riderLocationService = riderLocationService;
  }

  @KafkaListener(topics = "${kafka.topic}")
  public void receive(@Payload String data) throws IOException {
    log.info("message received: " + data);
    RiderLocation riderLocation = objectMapper.readValue(data, RiderLocation.class);
    validateMessage(riderLocation);
    if(riderLocation != null){
      riderLocationService.updateInsertRiderLocationEntity(riderLocation.getRiderId(),
              riderLocation.getLon(), riderLocation.getLat(), riderLocation.getDateTime());
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
