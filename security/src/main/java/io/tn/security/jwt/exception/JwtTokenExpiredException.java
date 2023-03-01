package io.tn.security.jwt.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;

/**
 * json 过期异常
 *
 * @author TrueNine
 * @since 2022-12-24
 */
public class JwtTokenExpiredException extends JwtException {

  private final String subject;

  @JsonIgnore
  @Expose(deserialize = false)
  private final ObjectMapper mapper;

  public JwtTokenExpiredException(String subject, ObjectMapper mapper) {
    this.subject = subject;
    this.mapper = mapper;
  }

  public <T> T getSubject(Class<T> type) {
    try {
      return mapper.readValue(this.subject, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
