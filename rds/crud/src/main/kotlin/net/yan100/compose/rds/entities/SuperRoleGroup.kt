package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
abstract class SuperRoleGroup : IEntity() {
  /** 名称 */
  @get:Schema(title = "名称")
  abstract var name: String

  /** 描述 */
  @get:Schema(title = "描述")
  abstract var doc: String?
}
