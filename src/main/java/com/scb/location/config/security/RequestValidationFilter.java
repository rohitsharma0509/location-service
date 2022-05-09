package com.scb.location.config.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.exception.AuthenticationException;
import com.scb.location.service.rider.RiderServiceProxy;
import com.scb.location.utils.BeanUtilService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestValidationFilter extends HttpFilter {

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    log.info("Inside request filter");

    CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
        new CachedBodyHttpServletRequest(request);
    String accessToken = cachedBodyHttpServletRequest.getHeader("Authorization");
    String idToken = cachedBodyHttpServletRequest.getHeader("idToken");

    try {

      if (StringUtils.isNotEmpty(cachedBodyHttpServletRequest.getHeader("x-amzn-apigateway-api-id"))
          && !request.getRequestURI().contains("/health") && !isOpsMember(accessToken)) {
        if (StringUtils.isEmpty(idToken) || StringUtils.isEmpty(accessToken)) {
          log.error("idToken or accessToken is missing");
          setForbiddenResponse(response);
          return;
        }
        if (!validateAccessIdToken(idToken, accessToken)) {
          log.info("Id token and auth token does not match");
          throw new AuthenticationException("Unauthorised");
        }

        String body = cachedBodyHttpServletRequest.getReader().lines()
            .collect(Collectors.joining(System.lineSeparator()));

        String phoneNumber = getPhoneNumberUsingToken(idToken).substring(3);

        if (!validatePathParamsRequest(request, response, cachedBodyHttpServletRequest,
            phoneNumber)) {
          return;
        }

        if ((request.getMethod().equals("POST") && !request.getRequestURI().contains("/location"))
            || request.getMethod().equals("PUT") || request.getMethod().equals("DELETE")) {
          setForbiddenResponse(response);
          return;
        }
      }
    } catch (AuthenticationException | JsonProcessingException ae) {
      log.error("Server Error ", ae.getMessage());
      setForbiddenResponse(response);
      return;
    }
    chain.doFilter(cachedBodyHttpServletRequest, response);
  }

  private boolean validatePathParamsRequest(HttpServletRequest request,
      HttpServletResponse response, CachedBodyHttpServletRequest cachedBodyHttpServletRequest,
      String phoneNumber) {
    // Extract Path Variables
    String requestPath[] = cachedBodyHttpServletRequest.getRequestURI().split("/");

    List<String> requestPathList = Arrays.stream(requestPath)
        .filter(pathVariable -> !StringUtils.isAlpha(pathVariable)
            && (StringUtils.isAlphanumeric(pathVariable) || StringUtils.isNumeric(pathVariable)))
        .collect(Collectors.toList());

    if (requestPathList.size() >= 1) {
      String pathVariable = requestPathList.get(requestPathList.size() - 1);
      log.info("PathVariable -> " + pathVariable + "- is Numeric-"
          + StringUtils.isNumeric(pathVariable));
      if (StringUtils.isAlphanumeric(pathVariable)
          && validateUserAuthorization(pathVariable, phoneNumber)) {
        return true;
      }
    }
    if (request.getRequestURI().contains("/api/zone/all")){
      return true;
    }
    setForbiddenResponse(response);
    return false;
  }

  private String getPhoneNumberUsingToken(String idToken) throws AuthenticationException {
    TokenUtils tokenUtils = BeanUtilService.getBean(TokenUtils.class);
    return tokenUtils.getPhoneNumberFromIdToken(idToken);
  }

  private boolean validateAccessIdToken(String idToken, String accessToken) {
    TokenUtils tokenUtils = BeanUtilService.getBean(TokenUtils.class);
    return tokenUtils.validateAccessIdToken(idToken, accessToken);
  }

  private boolean isOpsMember(String accessToken) throws AuthenticationException {
    TokenUtils tokenUtils = BeanUtilService.getBean(TokenUtils.class);
    boolean isOpsMember = tokenUtils.checkIfOpsToken(accessToken);
    log.info("The access belongs to Ops Member {}", isOpsMember);
    return isOpsMember;
  }

  boolean validateUserAuthorization(String riderProfileId, String phoneNumber) {
    if (StringUtils.isEmpty(riderProfileId)) {
      log.warn("The rider doesn't exist in request body. Skipping validation in request body");
      return true;
    }
    RiderServiceProxy riderService = BeanUtilService.getBean(RiderServiceProxy.class);
    return riderService.validateRider(riderProfileId, phoneNumber);
  }

  private void setForbiddenResponse(HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // HTTP 401.
    response.setHeader("message", "User is not authorized to perform this operation");
  }
}
