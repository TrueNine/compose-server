package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName

/**
 * 类型定义的属性
 * @param name 属性的名称
 * @param partial 是否可选
 * @param defined 属性的类型
 */
data class TsTypeProperty(
  val name: TsName,
  val partial: Boolean = false,
  val defined: TsTypeVal
) {
  fun isRequireUseGeneric(): Boolean {
    return defined.isRequireUseGeneric()
  }

  fun isBasic(): Boolean {
    return defined.isBasic()
  }

  override fun toString(): String {
    if (name is TsName.Anonymous) return defined.toString()
    return "$name${if (partial) "?" else ""}: $defined"
  }
}
