package net.yan100.compose.rds.entities.form

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.Converter
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaName
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
@MetaName("dynamic_form_value")
@Schema(name = "")
abstract class SuperDynamicFormValue : IEntity() {

  abstract var apiFor: String?

  abstract var platformFor: String?

  abstract var inputUserId: RefId?

  @get:Deprecated("不建议使用，尽量使用 user_id")
  abstract var inputUserInfoId: RefId?

  abstract var inputDatetime: datetime?

  @get:Schema(title = "审核 id", description = "充分 描述了当前值的审核状态")
  abstract var auditId: RefId?

  abstract var formGroupId: RefId

  @get:Schema(title = "填入的 form 值类型", description = "json")
  abstract var values: String?
}
