package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

/**
 * 用户 角色组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperUserRoleGroup : IJpaEntity {
  /** 用户 */
  var userId: RefId

  /** 权限组 */
  var roleGroupId: RefId
}
