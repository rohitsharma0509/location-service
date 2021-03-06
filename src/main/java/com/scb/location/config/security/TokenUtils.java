package com.scb.location.config.security;

import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.location.exception.AuthenticationException;
import lombok.SneakyThrows;

@Service
public class TokenUtils {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


  @Autowired
  private AwsCognitoRSAKeyProvider awsCognitoRSAKeyProvider;

  @Autowired
  private AwsCognitoRSAOpsKeyProvider awsCognitoRSAOpsKeyProvider;

  public boolean checkIfOpsToken(String idToken) throws AuthenticationException {
    try {
      String[] parts = idToken.split("\\.");
      String body = parts[1];
      String decodedString = new String(Base64.getDecoder().decode(body));
      JsonNode jsonNode = OBJECT_MAPPER.readTree(decodedString);
      return jsonNode.get("cognito:groups") != null;
    }catch(Exception e){
      throw new AuthenticationException("Unauthorized");
    }

  }

  public DecodedJWT validateIdToken(String idToken){
    Algorithm algorithm = Algorithm.RSA256(awsCognitoRSAKeyProvider);
    JWTVerifier jwtVerifier = JWT.require(algorithm)
        .build();
   return jwtVerifier.verify(idToken);

  }

  public DecodedJWT validateOpsIdToken(String idToken){
    Algorithm algorithm = Algorithm.RSA256(awsCognitoRSAOpsKeyProvider);
    JWTVerifier jwtVerifier = JWT.require(algorithm)
        .build();
    return jwtVerifier.verify(idToken);

  }

  public String getPhoneNumberFromIdToken(String idToken) throws AuthenticationException {
    try {
      DecodedJWT decodedJWT = validateIdToken(idToken);
      return decodedJWT.getClaims().get("phone_number").asString();
    }catch (Exception ex){
      throw new AuthenticationException("UnAuthorised");
    }

  }

  public boolean validateAccessIdToken(String idToken, String accessToken){
    String idTokenSub = getClaimFromToken(idToken, "sub");
    String accessTokenSub = getClaimFromToken(accessToken, "sub");
    return StringUtils.isNotBlank(idTokenSub) && StringUtils.isNotBlank(accessTokenSub)
        && idTokenSub.equals(accessTokenSub);

  }

  @SneakyThrows
  private String getClaimFromToken(String token, String claim){
    String[] parts = token.split("\\.");
    String body = parts[1];
    String decodedString = new String(Base64.getDecoder().decode(body));
    JsonNode jsonNode = OBJECT_MAPPER.readTree(decodedString);
    return jsonNode.get(claim).asText();

  }

}
