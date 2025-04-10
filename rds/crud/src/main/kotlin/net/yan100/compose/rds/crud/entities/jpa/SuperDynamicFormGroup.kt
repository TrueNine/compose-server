package net.yan100.compose.rds.crud.entities.jpa

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

/** 动态表单组 */
@MetaDef
interface SuperDynamicFormGroup : IJpaEntity {
  /**
   * ## 名称
   * 名称（不可重复）
   */
  @get:Schema(title = "", description = "") var name: String

  @get:Schema(title = "描述") var doc: String?
}
