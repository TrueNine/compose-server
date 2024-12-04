package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

/**
 * 动态表单字段
 */
@MetaDef
interface SuperDynamicForm : IJpaEntity {
  /**
   * 动态表单组 id
   */
  var dynamicFormGroupId: RefId

  /**
   * 可重复
   */
  var repeatable: Boolean?

  /**
   * 表单版本
   */
  var version: String

  /**
   * 值类型
   */
  var valueType: String

  /**
   * 字段描述
   */
  var description: String

  /**
   * 抽象的组件类型
   */
  var componentType: String

  /**
   * 字段位置
   */
  var index: Int

  /**
   * 与其他字段进行联动
   */
  var interactive: Boolean?

  /**
   * 字段标签
   */
  var label: String?

  /**
   * 占位符提示
   */
  var placeholder: String?

  /**
   * 字段进行分步骤的步骤索引
   */
  var groupIndex: Int?

  /**
   * 字段为只读
   */
  var readonly: Boolean?

  /**
   * 字段为必填项
   */
  var required: Boolean?

  /**
   * 字段的默认值
   */
  var defaultValue: String?

  /**
   * 字段的选项
   */
  var options: String?

  /**
   * 字段校验规则
   */
  var rules: String?
}
