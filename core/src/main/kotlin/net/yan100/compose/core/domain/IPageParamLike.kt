package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.core.bool
import net.yan100.compose.core.i32
import net.yan100.compose.core.i64

/**
 * ## 分页参数 Like
 *
 */
interface IPageParamLike {
  /** ## 分页 页面 大小 */
  val s: i32?
    @Transient
    get() = null

  /**
   * ## 分页 页面 偏移量
   */
  val o: i64?
    @Transient
    get() = null

  /**
   * ## UnPaged（禁用分页）
   */
  val u: bool?
    @Transient
    @JsonIgnore
    get() = null
}
