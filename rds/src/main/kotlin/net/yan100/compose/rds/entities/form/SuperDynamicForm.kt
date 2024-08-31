package net.yan100.compose.rds.entities.form

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.util.Str
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaName
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
@MetaName("dynamic_form")
@Schema(title = "动态表单字段")
abstract class SuperDynamicForm : IEntity() {
  @get:Schema(title = "动态表单组 id")
  abstract var dynamicFormGroupId: RefId

  @get:Schema(title = "可重复")
  abstract var repeatable: Boolean?

  @get:Schema(title = "表单版本")
  abstract var version: String

  @get:Schema(title = "值类型")
  abstract var valueType: String

  @get:Schema(title = "字段描述")
  abstract var description: String

  @get:Schema(title = "抽象的组件类型")
  abstract var componentType: String

  @get:Schema(title = "字段位置")
  abstract var index: Int

  @get:Schema(title = "与其他字段进行联动")
  abstract var interactive: Boolean?

  @get:Schema(title = "字段标签")
  abstract var label: String?

  @get:Schema(title = "占位符提示")
  abstract var placeholder: String?

  @get:Schema(title = "字段进行分步骤的步骤索引")
  abstract var groupIndex: Int?

  @get:Schema(title = "字段为只读")
  abstract var readonly: Boolean?

  @get:Schema(title = "字段为必填项")
  abstract var required: Boolean?

  @get:Schema(title = "字段的默认值")
  abstract var defaultValue: String?

  @get:Schema(title = "字段的选项")
  abstract var options: String?

  @get:Schema(title = "字段校验规则")
  abstract var rules: String?
}
