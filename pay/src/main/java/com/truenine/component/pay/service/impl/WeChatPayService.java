package com.truenine.component.pay.service.impl;

import com.truenine.component.pay.models.request.CreateOrderRequestParam;
import com.truenine.component.pay.models.response.CreateOrderResponseResult;
import com.truenine.component.pay.properties.WeChatProperties;
import com.truenine.component.pay.service.PayService;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WeChatPayService implements PayService {

  private final JsapiService jsapiService;

  private final WeChatProperties weChatProperties;

  private static final BigDecimal HUNDRED = new BigDecimal("100");

  public WeChatPayService(JsapiService jsapiService, WeChatProperties weChatProperties) {
    this.jsapiService = jsapiService;
    this.weChatProperties = weChatProperties;
  }

  @Override
  public CreateOrderResponseResult createOrder(CreateOrderRequestParam createOrderRequestParam) {
    PrepayRequest request = new PrepayRequest();
    Amount amount = new Amount();
    amount.setTotal(createOrderRequestParam.getMoney().multiply(HUNDRED).intValue());

    request.setAmount(amount);
    request.setAppid(weChatProperties.getAppId());
    request.setMchid(weChatProperties.getMerchantId());
    request.setDescription(createOrderRequestParam.getTitle());
    request.setNotifyUrl(weChatProperties.getNotifyUrl());
    request.setOutTradeNo(createOrderRequestParam.getOrderId());

    Payer payer = new Payer();
    payer.setOpenid(createOrderRequestParam.getOpenId());
    request.setPayer(payer);

    PrepayResponse response = jsapiService.prepay(request);
    CreateOrderResponseResult createOrderResponseResult = new CreateOrderResponseResult();
    createOrderResponseResult.setPrepayId(response.getPrepayId());
    return createOrderResponseResult;
  }
}
