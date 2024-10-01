package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.consts.IRegexes
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
abstract class SuperRole : IEntity() {
  /** 角色名称 */
  @get:Schema(title = "角色名称")
  @get:NotBlank(message = "角色名称不能为空")
  @get:Pattern(regexp = IRegexes.RBAC_NAME, message = "角色名称不合法")
  abstract var name: String

  /** 角色描述 */
  @get:Schema(title = "角色描述")
  abstract var doc: String?
}
