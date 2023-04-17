package com.truenine.component.pay.controller;

import com.truenine.component.pay.api.WeChatApi;
import com.truenine.component.pay.api.model.request.GetSessionRequest;
import com.truenine.component.pay.properties.WeChatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/wechat")
public class LoginController {

  private WeChatApi weChatApi;

  private WeChatProperties weChatProperties;

  public LoginController(WeChatApi weChatApi, WeChatProperties weChatProperties) {
    this.weChatApi = weChatApi;
    this.weChatProperties = weChatProperties;
  }

  @PostMapping(value = "/getUserInfo")
  public void getUserInfo(String code) {
    GetSessionRequest getSessionRequest = new GetSessionRequest();
//    getSessionRequest.setSecret();

//    weChatApi.token();
  }

}
