package com.scb.location.rest.advice;


import java.util.ArrayList;
import com.scb.location.exception.NoResultFoundException;
import com.scb.location.rest.advice.model.ErrorMessage;
import com.scb.location.exception.InvalidInputException;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * @Exception invalid input exception
   * @return HttpStatus 400
   */
  @ExceptionHandler(value = {InvalidInputException.class})
  public ResponseEntity<ErrorMessage> invalidInputException(InvalidInputException ex,
      WebRequest request) {
    ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    if (log.isInfoEnabled())
      log.error(message.toString());
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  /**
   * @Exception no valid result found exception
   * @return HttpStatus 400
   */
  @ExceptionHandler(value = {NoResultFoundException.class})
  public ResponseEntity<ErrorMessage> noResultFoundException(NoResultFoundException ex,
      WebRequest request) {
    ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    if (log.isInfoEnabled())
      log.error(message.toString());

    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
      WebRequest request) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
          + violation.getMessage());
    }

    ErrorMessage message =
        new ErrorMessage(HttpStatus.BAD_REQUEST.value(), String.join(",", errors));
    if (log.isInfoEnabled())
      log.error(message.toString());

    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

  }
}
