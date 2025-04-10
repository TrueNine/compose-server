package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

/**
 * # 用户 部门
 *
 * @author TureNine
 * @since 2023-07-16
 */
@MetaDef
interface SuperUserDept : IJpaEntity {
  /** 用户id */
  var userId: RefId

  /** 部门id */
  var deptId: RefId
}
