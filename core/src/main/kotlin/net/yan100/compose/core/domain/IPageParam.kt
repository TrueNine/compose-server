package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import net.yan100.compose.core.domain.IPageParam.Companion.MAX_PAGE_SIZE
import net.yan100.compose.core.domain.IPageParam.Companion.MIN_OFFSET
import net.yan100.compose.core.toSafeInt

/** ## 一个默认分页实现 */
private class DefaultPageParam
@Deprecated("不退完直接使用", level = DeprecationLevel.ERROR)
constructor(
  @Transient override var o: Long? = MIN_OFFSET,
  @Transient override var s: Int? = MAX_PAGE_SIZE,
  @Transient override var u: Boolean? = false,
) : IPageParam, Serializable {
  override fun toString(): String {
    return "PageParam(offset=$o, pageSize=$s, unPage=$u)"
  }

  override fun hashCode(): Int {
    return listOf(o, s, u).hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return when (other) {
      is IPageParam -> {
        o == other.o && s == other.s && (u == true) == (other.u == true)
      }

      else -> false
    }
  }
}

/**
 * # 分页参数
 *
 * 为所有的分页场景准备的分页参数
 *
 * @author TrueNine
 * @since 2024-06-20
 */
interface IPageParam : IPageParamLike, Serializable {
  companion object {
    @JvmStatic
    fun from(param: IPageParamLike): IPageParam {
      return get(param.o, param.s)
    }

    @JvmStatic
    fun empty(): IPageParam {
      return get(0, 0, true)
    }

    /** ## 最小偏移量 */
    const val MIN_OFFSET: Long = 0

    /** ## 最大分页页面大小 */
    const val MAX_PAGE_SIZE: Int = 42

    /** ## 默认 最大分页实现常量 */
    @Suppress("DEPRECATION_ERROR")
    val DEFAULT_MAX: IPageParam =
      DefaultPageParam(MIN_OFFSET, MAX_PAGE_SIZE, false)

    /**
     * ## 构建分页参数
     *
     * @param offset 偏移量 （最小为 0）
     * @param pageSize 页面大小
     * @param unPage 是否禁用分页
     */
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    operator fun get(
      offset: Long? = MIN_OFFSET,
      pageSize: Int? = MAX_PAGE_SIZE,
      unPage: Boolean? = false,
    ): IPageParam {
      return if (pageSize == 0) {
        DefaultPageParam(0, 0, unPage)
      } else if (unPage == true) {
        DefaultPageParam(0, Int.MAX_VALUE, unPage)
      } else {
        val ps = (pageSize ?: MAX_PAGE_SIZE)
        val o = (offset ?: MIN_OFFSET)
        DefaultPageParam(
          if (o <= 0) MIN_OFFSET else o,
          if (ps <= 0) 1 else ps,
          unPage,
        )
      }
    }

    /** ## 不进行分页 */
    @JvmStatic
    fun unPage(): IPageParam {
      return get(0, Int.MAX_VALUE, true)
    }

    @JvmStatic
    operator fun get(param: IPageParamLike?): IPageParam {
      return get(param?.o, param?.s)
    }
  }

  @JsonIgnore
  operator fun plus(total: Long): IPageParam {
    if (total <= 0) return empty()
    val ss = if (safePageSize >= total) total.toSafeInt() else safePageSize
    val c = (safeOffset + 1) * safePageSize
    val oo = if (c > total) (total / safePageSize) else safeOffset
    return get(oo, ss, u)
  }

  @get:JsonIgnore
  val safeOffset: Long
    get() = o ?: 0

  /** ## 分页 页面 偏移量 null any */
  @get:JsonIgnore
  val safePageSize: Int
    get() = s ?: 0

  @get:JsonIgnore
  private val safeRangeOffset: Long
    get() = (safePageSize.toLong() * safeOffset)

  @get:JsonIgnore
  private val safeRandEnd: Long
    get() {
      val end = (safeRangeOffset + (safePageSize))
      return end
    }

  @JsonIgnore
  fun toLongRange(): LongRange = LongRange(safeRangeOffset, safeRandEnd)
}
