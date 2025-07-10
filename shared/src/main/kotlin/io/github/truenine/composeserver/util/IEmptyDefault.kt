package io.github.truenine.composeserver.util

/**
 * 空体默认接口，查看当前类或者实现是否为空体
 *
 * @author TrueNine
 * @since 2023-04-11
 */
@Deprecated(" 多此一举")
interface IEmptyDefault {
  companion object {
    fun isEmptyDefault(anyObject: Any?): Boolean {
      return anyObject is IEmptyDefault
    }
  }
}
