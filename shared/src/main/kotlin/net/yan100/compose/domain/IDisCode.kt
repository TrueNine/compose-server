package net.yan100.compose.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import net.yan100.compose.consts.IRegexes
import net.yan100.compose.string
import java.util.*

/** 二代残疾证代码 */
interface IDisCode : IIdcard2Code {
  private class DefaultDis2Code(override val disCode: string) :
    IDisCode, IIdcard2Code by IIdcard2Code[disCode.substring(0, 18)] {
    init {
      check(disCode.matches(idCardRegex)) {
        "$disCode is not a valid disability code"
      }
    }

    companion object {
      @Transient
      private val idCardRegex = IRegexes.CHINA_DIS_CARD.toRegex()
    }

    override fun hashCode(): Int = Objects.hashCode(disCode)

    override fun equals(other: Any?): Boolean {
      if (null == other) return false
      return if (other is IIdcard2Code) {
        other.idcardDistrictCode == idcardDistrictCode
      } else false
    }

    override fun toString(): String = disCode
  }

  companion object {
    @JvmStatic
    operator fun get(code: String): IDisCode = DefaultDis2Code(code.uppercase())
  }

  @get:JsonIgnore
  override val idcard2Code: string
    get() = disCode.substring(0, 18)

  val disType: Int
    get() = disCode.substring(18, 19).toInt()

  val disLevel: Int
    get() = disCode.substring(19, 20).toInt()

  /** ## 残疾证号 */
  @get:JsonIgnore
  val disCode: string

  /**
   * ## 是否补办过
   * 根据第 20 位是否有 b 判断是否有补办过
   */
  @get:JsonIgnore
  val disCodeIsReIssued: Boolean
    get() = disCode.substring(19, 20).uppercase() == "B"

  /**
   * ## 补办次数 根据第 21 位 来解析补办次数
   *
   * @see disCodeIsReIssued
   */
  @get:JsonIgnore
  val disCodeReIssuedCount: Byte?
    get() = disCode.substring(20, 21).toByteOrNull()
}
