package com.scb.location.rest.advice;

import com.scb.location.exception.InvalidInputException;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.model.RiderLocation;
import com.scb.location.rest.advice.model.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import javax.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ControllerExceptionHandlerTest {

  @InjectMocks
  private ControllerExceptionHandler controllerExceptionHandler;

  private WebRequest webRequest;

  @BeforeEach
  public void setup() {
    controllerExceptionHandler = new ControllerExceptionHandler();
    webRequest = Mockito.mock(WebRequest.class);
  }

  @Test
  public void testInvalidInputException() {
    ResponseEntity<ErrorMessage> responseEntity = controllerExceptionHandler
        .invalidInputException(new InvalidInputException("Mocked Exception"), webRequest);
    assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
  }

  @Test
  public void testNoResultFoundException() {
    ResponseEntity<ErrorMessage> responseEntity = controllerExceptionHandler
            .noResultFoundException(new NoResultFoundException("Mocked Exception"), webRequest);
    assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());

  }

  @Test
  public void testHandleConstraintViolation() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    RiderLocation riderLocation = new RiderLocation(null,null,null,null,null,null,null);
    Set<ConstraintViolation<RiderLocation>> constraintViolations = validator.validate(riderLocation);

    new ConstraintViolationException(constraintViolations);

    ResponseEntity<Object> responseEntity = controllerExceptionHandler
        .handleConstraintViolation(new ConstraintViolationException(constraintViolations), webRequest);
    assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
  }


}
