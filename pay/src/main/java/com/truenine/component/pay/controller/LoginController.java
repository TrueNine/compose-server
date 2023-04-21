package com.truenine.component.pay.controller;

import com.truenine.component.pay.api.WeChatApi;
import com.truenine.component.pay.properties.WeChatProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/wechat")
public class LoginController {

  private WeChatApi weChatApi;

  private WeChatProperties weChatProperties;

  public LoginController(WeChatApi weChatApi, WeChatProperties weChatProperties) {
    this.weChatApi = weChatApi;
    this.weChatProperties = weChatProperties;
  }

  @GetMapping(value = "/getUserInfo")
  public String getUserInfo(String code) {
    return weChatApi.token(weChatProperties.getAppId(), weChatProperties.getAppSecret(), code, "authorization_code").getBody();
  }

}
