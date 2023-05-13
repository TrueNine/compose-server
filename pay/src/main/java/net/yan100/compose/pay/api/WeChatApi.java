package net.yan100.compose.pay.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


@HttpExchange
public interface WeChatApi {

  @GetExchange(value = "https://api.weixin.qq.com/sns/jscode2session", accept = "application/json")
  ResponseEntity<String> token(@RequestParam String appid, @RequestParam String secret,
                               @RequestParam(name = "js_code") String jsCode,
                               @RequestParam(name = "grant_type") String grantType);

}
