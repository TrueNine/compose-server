package com.truenine.component.rds.models;

import com.truenine.component.rds.dao.PermissionsDao;
import com.truenine.component.rds.dao.RoleDao;
import com.truenine.component.rds.dao.UserDao;
import com.truenine.component.rds.dao.UserInfoDao;
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
  private Set<RoleDao> roles = new HashSet<>();
  @Schema(title = "权限")
  private Set<PermissionsDao> permissions = new HashSet<>();
  @Schema(title = "用户")
  private UserDao user;
  @Schema(title = "用户信息")
  private UserInfoDao info;
}
