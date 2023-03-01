package com.truenine.component.rds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 普通用户注册入参
 *
 * @author TrueNine
 * @since 2022-12-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户注册信息")
public class UserRegisterDto {
  @NotBlank
  @Schema(name = "account", description = "账号")
  private String account;

  @NotBlank
  @NotEmpty
  @Schema(name = "nickName", description = "昵称")
  @NotNull
  private String nickName;

  @NotBlank
  @NotEmpty
  @Schema(name = "pwd", description = "密码")
  @NotNull
  private String pwd;

  @NotBlank
  @NotEmpty
  @Schema(name = "againPwd", description = "再次输入密码")
  @NotNull
  private String againPwd;

  @Schema(name = "doc", description = "描述")
  private String doc;
}
