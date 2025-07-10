package io.github.truenine.composeserver.domain

import io.github.truenine.composeserver.bool
import io.github.truenine.composeserver.i32

/** ## 分页参数 Like */
interface IPageParamLike {
  /** ## 分页 页面 大小 */
  val s: i32?
    get() = null

  /** ## 分页 页面 偏移量 */
  val o: i32?
    get() = null

  /**
   * ## UnPaged（禁用分页）
   * 默认 false
   */
  @Deprecated(message = "禁用分页是不明智的选择", level = DeprecationLevel.ERROR)
  val u: bool?
    get() = null
}
