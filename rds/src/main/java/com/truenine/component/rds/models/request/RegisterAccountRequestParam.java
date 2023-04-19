package com.truenine.component.rds.models.request;

import com.truenine.component.core.consts.Regexes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(title = "账号注册")
public class RegisterAccountRequestParam {
  @NotBlank(message = "账号不可为空")
  @Nullable
  @Schema(title = "账号")
  private String account;

  @NotBlank(message = "昵称不能为空")
  @Size(max = 128, min = 4, message = "昵称最长 128，最短 4")
  private String nickName;

  @NotBlank(message = "密码不能为空")
  @Size(max = 100, min = 8, message = "密码最短8位，最长100")
  @Pattern(regexp = Regexes.PASSWORD, message = "密码必须匹配规则为：" + Regexes.PASSWORD)
  @Schema(title = "密码")
  private String password;

  @Nullable
  @Schema(title = "描述", nullable = true)
  private String description;
}
