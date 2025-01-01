package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName

/**
 * 类型定义的属性
 * @param name 属性的名称
 * @param partial 是否可选
 * @param defined 属性的类型
 */
@Deprecated("改为 Use")
data class TsTypeProperty(
  val name: TsName,
  val partial: Boolean = false,
  val defined: TsTypeVal<*>
) {
  override fun toString(): String {
    if (name is TsName.Anonymous) return defined.toString()
    return "$name${if (partial) "?" else ""}: $defined"
  }
}
