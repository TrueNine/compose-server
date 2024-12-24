package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TypescriptName

/**
 * 使用的泛型
 * @param index 插入索引
 */
sealed class TypescriptGeneric(
  open val index: Int,
) {

  /**
   * 定义之上的泛型定义
   */
  data class Defined(
    val typeName: TypescriptName,
    override val index: Int,
  ) : TypescriptGeneric(index)

  /**
   * 使用的泛型
   * @param used 插入的值
   * @param useGenerics 其内嵌套使用的泛型
   */
  data class Used(
    val used: TypescriptTypeValue,
    override val index: Int,
    val useGenerics: List<Used> = emptyList(),
  ) : TypescriptGeneric(index)
}
