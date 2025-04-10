package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

/**
 * # 角色 权限
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperRolePermissions : IJpaEntity {
  /** 角色 */
  var roleId: RefId

  /** 权限 */
  var permissionsId: RefId
}
