package io.github.truenine.composeserver.domain

interface IPageLike<T : Any?> {
  /**
   * ## Data List
   * Data collection for the current page.
   */
  var d: Collection<T>

  /**
   * ## Page Current Offset
   * Current page offset, starting from 0, default is 0.
   */
  @Deprecated("This property is not required")
  var o: Long?
    get() = null
    set(value) {}

  /**
   * ## Total Page Size
   *
   * Total number of pages.
   */
  var p: Int

  /**
   * ## Total Elements Size
   * Total number of elements.
   */
  var t: Long

  fun component1(): Collection<T> = d

  fun component2(): Long = t
}
