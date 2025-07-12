package io.github.truenine.composeserver.domain

import io.github.truenine.composeserver.meta.annotations.MetaSkipGeneration

/**
 * # 可 有状态 脱敏 数据类
 *
 * @author TrueNine
 * @since 2024-07-09
 */
interface ISensitivity {
  fun changeWithSensitiveData() {}

  /**
   * ## 改变当前的脱敏状态为 sensed
   *
   * 该方法由更抽象的类等的实现，可重复被调用，但返回状态需保持一致
   */
  fun recordChangedSensitiveData() {}

  @MetaSkipGeneration
  val isChangedToSensitiveData: Boolean
    get() = false
}
