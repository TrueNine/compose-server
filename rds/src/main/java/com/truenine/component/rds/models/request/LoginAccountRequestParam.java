package com.truenine.component.rds.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "登录账号")
public class LoginAccountRequestParam {

  @NotBlank(message = "账号不能为空")
  @Schema(title = "账号")
  private String account;

  @NotBlank(message = "密码不能为空")
  @Schema(title = "密码")
  private String password;
}
