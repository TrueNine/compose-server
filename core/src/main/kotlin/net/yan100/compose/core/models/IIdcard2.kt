package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate

/**
 * # 二代身份证抽象工具类
 */
interface IIdcard2 {
  private class DefaultIdcard2(private val idcard2Code: String) : IIdcard2 {
    override fun idcardCode(): String = idcard2Code
  }

  companion object {
    @JvmStatic
    fun of(idcard2Code: String): IIdcard2 {
      return DefaultIdcard2(idcard2Code)
    }
  }

  @JsonIgnore
  fun idcardCode(): String

  @get:JsonIgnore
  val idcardBirthday: LocalDate
    get() =
      LocalDate.of(
        idcardCode().substring(6, 10).toInt(),
        idcardCode().substring(10, 12).toInt(),
        idcardCode().substring(12, 14).toInt()
      )


  @get:JsonIgnore
  val idcardSexCode: String get() = idcardCode().substring(16, 17)

  @get:JsonIgnore
  val idcardSex: Boolean get() = idcardSexCode.toByte() % 2 != 0

  @get:JsonIgnore
  val idcardDistrictCode: String get() = idcardCode().substring(0, 6)

  @get:JsonIgnore
  val idcardUpperCase: String get() = idcardCode().uppercase()

}
