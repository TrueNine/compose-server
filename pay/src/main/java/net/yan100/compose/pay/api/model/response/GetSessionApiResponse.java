package net.yan100.compose.pay.api.model.response;

import lombok.Data;

@Data
public class GetSessionApiResponse {

  private String openid;
  private String session_key;
  private String unionid;
  private String errcode;
  private String errmsg;

}
