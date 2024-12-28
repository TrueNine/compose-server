package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.toVariableName
import net.yan100.compose.client.unwrapGenericName

/**
 * 使用的泛型
 * @param index 插入索引
 */
sealed class TsGeneric(
  open val index: Int = 0,
) {

  /**
   * 定义之上的泛型定义
   */
  data class Defined(
    val name: TsName,
    override val index: Int = 0,
  ) : TsGeneric(index) {
    override fun toString(): String = name.toVariableName().unwrapGenericName()
  }

  /**
   * 使用的泛型
   * @param used 插入的值
   * @param useGenerics 其内嵌套使用的泛型
   */
  data class Used(
    val used: TsTypeVal,
    override val index: Int = 0
  ) : TsGeneric(index) {
    override fun toString(): String = used.toString()
  }

  /**
   * 没有填写泛型
   */
  data class UnUsed(
    val used: TsTypeVal = TsTypeVal.Never,
    override val index: Int = 0
  ) : TsGeneric(index)
}
