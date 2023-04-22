package com.truenine.component.pay.api.model.response;

import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryOrderApiResponseResult {

  private String orderId;
  private String orderNo;

  private BigDecimal money;
  private String tradeStatus;

}
