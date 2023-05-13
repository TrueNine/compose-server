package net.yan100.compose.pay.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "创建订单返回")
public class CreateOrderResponse {
  @Schema(title = "32位随机字符串")
  private String nonceStr;
  @Schema(name = "package")
  @JsonProperty(value = "package")
  private String packageStr;
  @Schema(title = "时间戳秒")
  private String timeStamp;
  @Schema(title = "加密方法，SHA256-RSA")
  private String signType;
  @Schema(title = "加密字符串")
  private String paySign;
}
