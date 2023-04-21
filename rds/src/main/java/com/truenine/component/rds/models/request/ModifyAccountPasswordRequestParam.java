package com.truenine.component.rds.models.request;

import com.truenine.component.core.consts.Regexes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(title = "修改账号密码")
public class ModifyAccountPasswordRequestParam {
  @NotBlank(message = "账号不能为空")
  private String account;

  @NotBlank(message = "密码不能为空")
  @Pattern(regexp = Regexes.PASSWORD, message = "密码必须匹配规则为：" + Regexes.PASSWORD)
  private String oldPassword;

  @NotBlank(message = "新密码不能为空")
  @Pattern(regexp = Regexes.PASSWORD, message = "密码必须匹配规则为：" + Regexes.PASSWORD)
  private String newPassword;
}
