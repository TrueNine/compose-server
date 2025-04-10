package net.yan100.compose.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import net.yan100.compose.consts.IRegexes
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

/** # 二代身份证代码 */
interface IIdcard2Code {

  private class DefaultIdcard2Code(override val idcard2Code: String) :
    IIdcard2Code {
    init {
      check(idcardBirthday.isBefore(LocalDate.now())) {
        "$idcard2Code is not a valid idcard2Code"
      }
      check(idcard2Code.matches(idCardRegex)) {
        "$idcard2Code is not a valid idcard2Code"
      }
    }

    companion object {
      private val idCardRegex = IRegexes.CHINA_ID_CARD.toRegex()
    }

    override fun hashCode(): Int = Objects.hashCode(idcard2Code)

    override fun equals(other: Any?): Boolean {
      if (null == other) return false
      return if (other is IIdcard2Code) {
        other.idcard2Code == idcard2Code
      } else false
    }

    override fun toString(): String = idcard2Code
  }

  companion object {
    @JvmStatic
    operator fun get(idcard2Code: String): IIdcard2Code =
      DefaultIdcard2Code(idcard2Code.uppercase())
  }

  @get:JsonIgnore
  val idcard2Code: String

  @get:JsonIgnore
  val idcardBirthday: LocalDate
    get() =
      LocalDate.of(
        idcard2Code.substring(6, 10).toInt(),
        idcard2Code.substring(10, 12).toInt(),
        idcard2Code.substring(12, 14).toInt(),
      )

  @get:JsonIgnore
  val idcardSexCode: String
    get() = idcard2Code.substring(16, 17)

  @get:JsonIgnore
  val idcardSex: Boolean
    get() = idcardSexCode.toByte() % 2 != 0

  @get:JsonIgnore
  val idcardAge: Int
    get() = ChronoUnit.YEARS.between(idcardBirthday, LocalDate.now()).toInt()

  @get:JsonIgnore
  val idcardRecommendAvailabilityYear: Int
    get() {
      return when (idcardAge) {
        in 0 .. 16 -> 5
        in 17 .. 25 -> 10
        in 26 .. 45 -> 20
        else -> -1
      }
    }

  /**
   * ## 行政区划码
   *
   * 不一定有效，可能会夹杂 00
   */
  @get:JsonIgnore
  val idcardDistrictCode: String
    get() = idcard2Code.substring(0, 6)

  @get:JsonIgnore
  val idcardUpperCase: String
    get() = idcardDistrictCode.uppercase()
}
