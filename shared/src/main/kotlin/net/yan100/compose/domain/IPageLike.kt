package net.yan100.compose.domain

interface IPageLike<T : Any?> {
  /**
   * ## Data List
   * 数据列表
   */
  var d: Collection<T>

  /**
   * ## Page Current Offset
   * 当前所在页面 起始位置为 0 默认为 0
   */
  @Deprecated("无需此属性")
  var o: Long?
    get() = null
    set(value) {}

  /**
   * ## Total Page Size
   *
   * 所有页面的总数
   */
  var p: Int

  /**
   * ## Total Elements Size
   * 所有内容总数
   */
  var t: Long

  fun component1(): Collection<T> = d

  fun component2(): Long = t
}
