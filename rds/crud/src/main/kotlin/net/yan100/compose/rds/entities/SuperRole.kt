package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
abstract class SuperRole : IEntity {
  /** 角色名称 */
  @get:Schema(title = "角色名称")
  abstract var name: String

  /** 角色描述 */
  @get:Schema(title = "角色描述")
  abstract var doc: String?
}
