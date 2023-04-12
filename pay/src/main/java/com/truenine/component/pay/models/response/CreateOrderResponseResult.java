package com.truenine.component.pay.models.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrderResponseResult {


  @NotNull(message = "预支付交易会话标识不能为空")
  @Size(max = 64, min = 1, message = "预支付交易会话标识长度不符合规范")
  private String prepayId;

}
