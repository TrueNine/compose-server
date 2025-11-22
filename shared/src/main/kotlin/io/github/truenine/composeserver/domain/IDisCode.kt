package io.github.truenine.composeserver.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.truenine.composeserver.consts.IRegexes
import io.github.truenine.composeserver.string
import java.util.*

/** Second-generation disability certificate code. */
interface IDisCode : IIdcard2Code {
  private class DefaultDis2Code(override val disCode: string) : IDisCode, IIdcard2Code by IIdcard2Code[disCode.substring(0, 18)] {
    init {
      check(disCode.matches(idCardRegex)) { "$disCode is not a valid disability code" }
    }

    companion object {
      @Transient private val idCardRegex = IRegexes.CHINA_DIS_CARD.toRegex()
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
    @JvmStatic operator fun get(code: String): IDisCode = DefaultDis2Code(code.uppercase())
  }

  @get:JsonIgnore
  override val idcard2Code: string
    get() = disCode.substring(0, 18)

  val disType: Int
    get() = disCode.substring(18, 19).toInt()

  val disLevel: Int
    get() = disCode.substring(19, 20).toInt()

  /** ## Disability certificate number */
  @get:JsonIgnore val disCode: string

  /**
   * ## Whether the certificate has been reissued.
   * Determined by whether the 20th character is 'b' or 'B'.
   */
  @get:JsonIgnore
  val disCodeIsReIssued: Boolean
    get() = disCode.substring(19, 20).uppercase() == "B"

  /**
   * ## Reissue count.
   * Parsed from the 21st character.
   *
   * @see disCodeIsReIssued
   */
  @get:JsonIgnore
  val disCodeReIssuedCount: Byte?
    get() = disCode.substring(20, 21).toByteOrNull()
}
