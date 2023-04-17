package com.truenine.component.pay.api;

import com.truenine.component.pay.api.model.request.GetSessionRequest;
import com.truenine.component.pay.api.model.response.GetSessionResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange
public interface WeChatApi {

  @GetExchange(value = "sns/jscode2session")
  GetSessionResponse token(GetSessionRequest request);

}
