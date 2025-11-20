package io.github.truenine.composeserver.domain

/**
 * ## Pagination parameters (lightweight interface)
 *
 * @author TrueNine
 * @since 2025-07-03
 */
interface IPageParamLike {
  /** ## Page size */
  val s: Int?
    get() = null

  /** ## Page offset */
  val o: Int?
    get() = null

  /**
   * ## UnPaged (disable pagination)
   * Default false
   */
  @Deprecated(message = "Disabling pagination is not a wise choice", level = DeprecationLevel.ERROR)
  val u: Boolean?
    get() = null
}
