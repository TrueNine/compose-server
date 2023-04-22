package com.truenine.component.pay.service.impl;

import cn.hutool.core.util.IdUtil;
import com.truenine.component.core.lang.Str;
import com.truenine.component.pay.api.model.request.CreateOrderApiRequestParam;
import com.truenine.component.pay.api.model.request.QueryOrderApiRequestParam;
import com.truenine.component.pay.api.model.response.CreateOrderApiResponseResult;
import com.truenine.component.pay.api.model.response.QueryOrderApiResponseResult;
import com.truenine.component.pay.properties.WeChatProperties;
import com.truenine.component.pay.service.PayService;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class WeChatPayService implements PayService {

  private final JsapiService jsapiService;
  private final RefundService refundService;

  private final WeChatProperties weChatProperties;

  private static final BigDecimal HUNDRED = new BigDecimal("100");

  public WeChatPayService(JsapiService jsapiService, RefundService refundService, WeChatProperties weChatProperties) {
    this.jsapiService = jsapiService;
    this.refundService = refundService;
    this.weChatProperties = weChatProperties;
  }

  @Override
  public CreateOrderApiResponseResult createOrder(CreateOrderApiRequestParam createOrderApiRequestParam) {
    PrepayRequest request = new PrepayRequest();
    Amount amount = new Amount();
    amount.setTotal(createOrderApiRequestParam.getMoney().multiply(HUNDRED).intValue());

    request.setAmount(amount);
    request.setAppid(weChatProperties.getAppId());
    request.setMchid(weChatProperties.getMerchantId());
    request.setDescription(createOrderApiRequestParam.getTitle());
    request.setNotifyUrl(weChatProperties.getNotifyUrl());
    request.setOutTradeNo(createOrderApiRequestParam.getOrderId());

    Payer payer = new Payer();
    payer.setOpenid(createOrderApiRequestParam.getOpenId());
    request.setPayer(payer);

    PrepayResponse response = jsapiService.prepay(request);
    CreateOrderApiResponseResult createOrderApiResponseResult = new CreateOrderApiResponseResult();
    createOrderApiResponseResult.setPrepayId(response.getPrepayId());
    return createOrderApiResponseResult;
  }

  @Override
  public QueryOrderApiResponseResult queryOrder(QueryOrderApiRequestParam queryOrderApiRequestParam) {
    if (Str.nonText(queryOrderApiRequestParam.getOrderId()) && Str.nonText(queryOrderApiRequestParam.getOrderNo())) {
      throw new IllegalArgumentException("商户订单号和第三方订单号不能同时为空");
    }
    Transaction transaction = null;
    if (Str.hasText(queryOrderApiRequestParam.getOrderId())) {
      QueryOrderByOutTradeNoRequest queryOrderByOutTradeNoRequest = new QueryOrderByOutTradeNoRequest();
      queryOrderByOutTradeNoRequest.setOutTradeNo(queryOrderApiRequestParam.getOrderId());
      queryOrderByOutTradeNoRequest.setMchid(weChatProperties.getMerchantId());
      transaction = jsapiService.queryOrderByOutTradeNo(queryOrderByOutTradeNoRequest);
    } else if (Str.hasText(queryOrderApiRequestParam.getOrderNo())) {
      QueryOrderByIdRequest queryOrderByIdRequest = new QueryOrderByIdRequest();
      queryOrderByIdRequest.setTransactionId(queryOrderApiRequestParam.getOrderNo());
      queryOrderByIdRequest.setMchid(weChatProperties.getMerchantId());
      transaction = jsapiService.queryOrderById(queryOrderByIdRequest);
    }

    assert transaction != null;
    QueryOrderApiResponseResult queryOrderApiResponseResult = new QueryOrderApiResponseResult();
    queryOrderApiResponseResult.setOrderId(transaction.getOutTradeNo());
    queryOrderApiResponseResult.setOrderNo(transaction.getTransactionId());
    queryOrderApiResponseResult.setMoney(
      new BigDecimal(transaction.getAmount().getTotal()).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY));
    queryOrderApiResponseResult.setTradeStatus(transaction.getTradeState().toString());
    return queryOrderApiResponseResult;
  }

  public void refundOrder() {
    CreateRequest createRequest = new CreateRequest();
    AmountReq amountReq = new AmountReq();
    long amount = new BigDecimal("0.01").multiply(HUNDRED).longValue();
    amountReq.setRefund(amount);
    amountReq.setTotal(amount);
    amountReq.setCurrency("CNY");
    createRequest.setAmount(amountReq);
    createRequest.setOutTradeNo("1649768373464600576");
    createRequest.setOutRefundNo(IdUtil.getSnowflake().nextIdStr());
    Refund refund = refundService.create(createRequest);
//    createRequest.setNotifyUrl("https://weixin.qq.com");

  }
}
