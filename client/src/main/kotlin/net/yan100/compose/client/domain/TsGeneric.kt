package net.yan100.compose.client.domain

import net.yan100.compose.client.TsTypeDefine
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.toVariableName
import net.yan100.compose.client.unwrapGenericName

/**
 * 使用的泛型
 *
 * @param index 插入索引
 */
sealed class TsGeneric(open val index: Int = 0) : TsTypeDefine<TsGeneric> {
  override fun fillGenerics(usedGenerics: List<TsGeneric>): TsGeneric {
    if (usedGenerics.isEmpty()) return this
    return when (this) {
      is UnUsed -> this
      is Defined -> this
      is Used -> copy(used = used.fillGenerics(usedGenerics))
    }
  }

  override val isRequireUseGeneric: Boolean
    get() =
      when (this) {
        UnUsed -> true
        is Defined -> false
        is Used -> used.isRequireUseGeneric
      }

  override val isBasic: Boolean
    get() {
      return when (this) {
        is UnUsed -> false
        is Defined -> name.isBasic()
        is Used -> used.isBasic
      }
    }

  /** 定义之上的泛型定义 */
  data class Defined(val name: TsName, override val index: Int = 0) :
    TsGeneric(index) {
    override fun toString(): String = name.toVariableName().unwrapGenericName()
  }

  /**
   * 使用的泛型
   *
   * @param used 插入的值
   * @param index 其内嵌套使用的泛型
   */
  data class Used(val used: TsTypeVal<*>, override val index: Int = 0) :
    TsGeneric(index) {
    override fun toString(): String = used.toString()
  }

  /** 没有填写泛型 */
  data object UnUsed : TsGeneric(index = Int.MIN_VALUE) {
    override fun toString(): String = TsTypeVal.Unknown.toString()
  }
}
