package com.truenine.component.pay.api.model.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryOrderApiRequestParam {

  @Size(min = 6, max = 32, message = "参数长度不对")
  private String orderId;

  @Size(min = 1, max = 32, message = "参数长度不对")
  private String orderNo;

}
