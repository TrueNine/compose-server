package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import java.time.LocalDate

/**
 * # 二代身份证代码
 */
interface IIdcard2Code {
  private class DefaultIdcard2Code(code: String) : IIdcard2Code {
    override val idcard2Code: String = code
  }

  companion object {
    @JvmStatic
    fun of(idcard2Code: String): IIdcard2Code {
      return DefaultIdcard2Code(idcard2Code)
    }
  }

  @get:Transient
  @get:JsonIgnore
  val idcard2Code: String

  @get:Transient
  @get:JsonIgnore
  val idcardBirthday: LocalDate
    get() =
      LocalDate.of(
        idcard2Code.substring(6, 10).toInt(),
        idcard2Code.substring(10, 12).toInt(),
        idcard2Code.substring(12, 14).toInt()
      )

  @get:Transient
  @get:JsonIgnore
  val idcardSexCode: String get() = idcard2Code.substring(16, 17)

  @get:Transient
  @get:JsonIgnore
  val idcardSex: Boolean get() = idcardSexCode.toByte() % 2 != 0

  @get:Transient
  @get:JsonIgnore
  val idcardDistrictCode: String get() = idcard2Code.substring(0, 6)

  @get:Transient
  @get:JsonIgnore
  val idcardUpperCase: String get() = idcardDistrictCode.uppercase()
}
