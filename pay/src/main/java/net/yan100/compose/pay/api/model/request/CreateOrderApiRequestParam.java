package net.yan100.compose.pay.api.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderApiRequestParam {

  /**
   * 商户订单号
   */
  @NotBlank(message = "商户订单号不能为空")
  @Size(max = 32, min = 6, message = "商户订单号长度不符合规范")
  private String orderId;

  /**
   * 金额
   */
  @NotNull(message = "金额不能为空")
  private BigDecimal money;

  /**
   * 微信用户唯一 ID
   */
  @NotNull(message = "用户Id")
  @Size(max = 128, min = 1, message = "用户标识长度不符合规范")
  private String openId;

  /**
   * 标题
   */
  @NotNull(message = "订单标题不能为空")
  @Size(max = 127, min = 1, message = "订单标题长度不符合规范")
  private String title;
}
