package net.yan100.compose.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.yan100.compose.bool
import net.yan100.compose.i32
import java.io.Serializable

/** ## 一个默认分页实现 */
class DefaultPageParam
@Deprecated("不退完直接使用", level = DeprecationLevel.ERROR)
@JsonCreator
constructor(
  @Transient @JsonProperty("o") override var o: i32? = null,
  @Transient @JsonProperty("s") override var s: i32? = null,
  @Deprecated("禁用分页是不明智的选择", level = DeprecationLevel.ERROR)
  @Transient
  @JsonProperty("u")
  override var u: Boolean? = null,
) : IPageParam, Serializable {

  override fun toString(): String {
    return "PageParam(offset=$o, pageSize=$s)"
  }

  override fun hashCode(): Int {
    return listOf(o, s).hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return when (other) {
      is IPageParam -> {
        o == other.o && s == other.s
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
    const val MIN_OFFSET: i32 = 0

    /** ## 最大分页页面大小 */
    const val MAX_PAGE_SIZE: i32 = 42

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
    @JsonCreator
    @Suppress("DEPRECATION_ERROR")
    operator fun get(
      @JsonProperty("o") offset: i32? = MIN_OFFSET,
      @JsonProperty("s") pageSize: i32? = MAX_PAGE_SIZE,
      @JsonProperty("u") unPage: bool? = false,
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
  operator fun plus(total: i32): IPageParam {
    if (total <= 0) return empty()
    val ss = if (safePageSize >= total) total else safePageSize
    val c = (safeOffset + 1) * safePageSize
    val oo = if (c > total) (total / safePageSize) else safeOffset
    return get(oo, ss)
  }

  @get:JsonIgnore
  val safeOffset: i32
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
