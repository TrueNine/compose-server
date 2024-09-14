package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.RefId
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

/**
 * # 用户 部门
 *
 * @author TureNine
 * @since 2023-07-16
 */
@MetaDef
@MappedSuperclass
abstract class SuperUserDept : IEntity() {
  @get:Schema(title = "用户id")
  abstract var userId: RefId

  @get:Schema(title = "部门id")
  abstract var deptId: RefId
}
