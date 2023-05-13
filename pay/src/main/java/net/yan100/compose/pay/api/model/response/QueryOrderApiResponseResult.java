package net.yan100.compose.pay.api.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryOrderApiResponseResult {

  private String orderId;
  private String orderNo;

  private BigDecimal money;
  private String tradeStatus;

}
