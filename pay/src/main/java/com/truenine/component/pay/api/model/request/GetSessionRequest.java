package com.truenine.component.pay.api.model.request;

import lombok.Data;
import org.springframework.core.convert.converter.Converter;

@Data
public class GetSessionRequest {

  private String appid;
  private String secret;
  private String js_code;
  private String grant_type = "authorization_code";

}
