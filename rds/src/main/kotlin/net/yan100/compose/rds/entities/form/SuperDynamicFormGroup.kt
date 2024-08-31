package net.yan100.compose.rds.entities.form

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaName
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
@Schema(title = "动态表单组")
@MetaName("dynamic_form_group")
abstract class SuperDynamicFormGroup : IEntity() {
  @get:Schema(title = "名称", description = "名称（不可重复）")
  abstract var name: String

  @get:Schema(title = "描述")
  abstract var doc: String?
}
