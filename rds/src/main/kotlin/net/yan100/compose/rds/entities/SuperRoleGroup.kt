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
abstract class SuperRoleGroup : IEntity() {
  /** 名称 */
  @get:Schema(title = "名称")
  @get:NotBlank(message = "角色组名称不能为空")
  @get:Pattern(regexp = IRegexes.RBAC_NAME, message = "角色组名称不合法")
  abstract var name: String

  /** 描述 */
  @get:Schema(title = "描述")
  abstract var doc: String?
}
