package io.github.truenine.composeserver.domain

/**
 * ## 分页参数 Like
 *
 * @author TrueNine
 * @since 2025-07-03
 */
interface IPageParamLike {
  /** ## 分页 页面 大小 */
  val s: Int?
    get() = null

  /** ## 分页 页面 偏移量 */
  val o: Int?
    get() = null

  /**
   * ## UnPaged（禁用分页）
   * 默认 false
   */
  @Deprecated(message = "禁用分页是不明智的选择", level = DeprecationLevel.ERROR)
  val u: Boolean?
    get() = null
}
