package net.yan100.compose.core.domain

import net.yan100.compose.core.bool
import net.yan100.compose.core.i32
import net.yan100.compose.core.i64

/** ## 分页参数 Like */
interface IPageParamLike {
  /** ## 分页 页面 大小 */
  val s: i32?

  /** ## 分页 页面 偏移量 */
  val o: i64?

  /**
   * ## UnPaged（禁用分页）
   * 默认 false
   */
  val u: bool?
}
