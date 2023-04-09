package com.truenine.component.rds.models;

import com.truenine.component.rds.entity.PermissionsEntity;
import com.truenine.component.rds.entity.RoleEntity;
import com.truenine.component.rds.entity.UserEntity;
import com.truenine.component.rds.entity.UserInfoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息
 *
 * @author TrueNine
 * @since 2022-12-19
 */
@Getter
@Setter
@ToString
@Schema(title = "用户认证授权信息")
public class UserAuthorizationModel {
  @Schema(title = "角色")
  private Set<RoleEntity> roles = new HashSet<>();
  @Schema(title = "权限")
  private Set<PermissionsEntity> permissions = new HashSet<>();
  @Schema(title = "用户")
  private UserEntity user;
  @Schema(title = "用户信息")
  private UserInfoEntity info;
}
