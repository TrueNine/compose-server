package net.yan100.compose.client

import net.yan100.compose.client.domain.TsGeneric

interface TsTypeDefine<T : TsTypeDefine<T>> {
  /**
   * 判定其内包括自身属性是否具有 [net.yan100.compose.client.domain.TsGeneric.UnUsed] 标记
   *
   * 进而决定是否应该向其内部填充泛型参数
   */
  val isRequireUseGeneric: Boolean

  /**
   * 判定自身是否为基本 无引用类型，无需填充泛型
   */
  val isBasic: Boolean

  /**
   * 浅层填充泛型
   */
  fun fillGenerics(vararg generic: TsGeneric): T

  /**
   * 浅层填充泛型
   */
  fun fillGenerics(usedGenerics: List<TsGeneric>): T
}
