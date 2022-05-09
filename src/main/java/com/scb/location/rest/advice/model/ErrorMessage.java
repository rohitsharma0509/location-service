package com.scb.location.rest.advice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Message POJO for custom exception message
 */
@AllArgsConstructor
@Data
public class ErrorMessage {
 
  private int errorCode;
  private String errorMessage;

  @Override
  public String toString() {
    return "ErrorMessage{" + "errorCode=" + errorCode + ", errorMessage='" + errorMessage + '\''
        + '}';
  }
}
