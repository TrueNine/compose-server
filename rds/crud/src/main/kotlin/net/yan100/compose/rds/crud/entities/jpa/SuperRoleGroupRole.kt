package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

/**
 * 角色组 角色
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperRoleGroupRole : IJpaEntity {
  /** 用户组 */
  var roleGroupId: RefId

  /** 角色 */
  var roleId: RefId
}
