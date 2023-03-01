package io.tn.rds.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.tn.rds.dao.PermissionsDao;
import io.tn.rds.dao.RoleDao;
import io.tn.rds.dao.UserDao;
import io.tn.rds.dao.UserInfoDao;
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
public class UsrVo {
  @Schema(title = "角色")
  private Set<RoleDao> roles = new HashSet<>();
  @Schema(title = "权限")
  private Set<PermissionsDao> permissions = new HashSet<>();
  @Schema(title = "用户")
  private UserDao user;
  @Schema(title = "用户信息")
  private UserInfoDao info;
  @Schema(title = "租户id")
  private String tenant;

}
