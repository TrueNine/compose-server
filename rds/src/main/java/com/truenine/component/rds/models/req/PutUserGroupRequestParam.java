package com.truenine.component.rds.models.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册 dto
 *
 * @author TrueNine
 * @since 2023-01-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户组注册参数")
public class PutUserGroupRequestParam {
  @NotBlank
  @NotNull
  @Schema(description = "用户组名称")
  private String name;

  @Nullable
  @Schema(description = "用户组描述")
  private String desc;

  @NotNull
  @NotBlank
  @Schema(description = "创建该用户组的账号")
  private String leaderUserAccount;
}
