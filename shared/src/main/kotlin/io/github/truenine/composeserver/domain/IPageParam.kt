package io.github.truenine.composeserver.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * # Pagination parameters
 *
 * Common pagination parameters used for all paging scenarios.
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

    /** ## Minimum offset */
    const val MIN_OFFSET: Int = 0

    /** ## Maximum page size */
    const val MAX_PAGE_SIZE: Int = 42

    /** ## Default maximum pagination implementation constant */
    @Suppress("DEPRECATION_ERROR") val DEFAULT_MAX: IPageParam = DefaultPageParam(MIN_OFFSET, MAX_PAGE_SIZE, false)

    /**
     * ## Build pagination parameters
     *
     * @param offset Offset (minimum 0)
     * @param pageSize Page size
     * @param unPage Whether to disable pagination
     */
    @JvmStatic
    @JsonCreator
    @Suppress("DEPRECATION_ERROR")
    operator fun get(
      @JsonProperty("o") offset: Int? = MIN_OFFSET,
      @JsonProperty("s") pageSize: Int? = MAX_PAGE_SIZE,
      @JsonProperty("u") unPage: Boolean? = false,
    ): IPageParam {
      return if (pageSize == 0) {
        DefaultPageParam(0, 0, unPage)
      } else if (unPage == true) {
        DefaultPageParam(0, Int.MAX_VALUE, unPage)
      } else {
        val ps = (pageSize ?: MAX_PAGE_SIZE)
        val o = (offset ?: MIN_OFFSET)
        DefaultPageParam(if (o <= 0) MIN_OFFSET else o, if (ps <= 0) 1 else ps, unPage)
      }
    }

    /** ## Disable pagination */
    @JvmStatic
    fun unPage(): IPageParam {
      return get(0, Int.MAX_VALUE, true)
    }

    @JvmStatic
    operator fun get(param: IPageParamLike?): IPageParam {
      return get(param?.o, param?.s)
    }

    /** ## A default pagination implementation */
    class DefaultPageParam
    @Deprecated("Direct usage is not recommended", level = DeprecationLevel.ERROR)
    constructor(
      @Transient @param:JsonProperty("o") override var o: Int? = null,
      @Transient @param:JsonProperty("s") override var s: Int? = null,
      @Deprecated("Disabling pagination is not a wise choice", level = DeprecationLevel.ERROR) @Transient @param:JsonProperty("u") override var u: Boolean? = null,
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
  }

  @JsonIgnore
  operator fun plus(total: Int): IPageParam {
    if (total <= 0) return empty()
    val ss = if (safePageSize >= total) total else safePageSize
    val c = (safeOffset + 1) * safePageSize
    val oo = if (c > total) (total / safePageSize) else safeOffset
    return get(oo, ss)
  }

  @get:JsonIgnore
  val safeOffset: Int
    get() = o ?: 0

  /** ## Safe page size with null handling */
  @get:JsonIgnore
  val safePageSize: Int
    get() = s ?: 0

  @get:JsonIgnore
  private val safeRangeOffset: Long
    get() = (safePageSize.toLong() * safeOffset)

  @get:JsonIgnore
  private val safeRandEnd: Long
    get() {
      val end = (safeRangeOffset + (safePageSize) - 1)
      return end
    }

  @JsonIgnore fun toLongRange(): LongRange = LongRange(safeRangeOffset, safeRandEnd)
}
