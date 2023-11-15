package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient

/**
 * 二代残疾证代码
 */
interface IDisabilityCode2 : IIdcard2Code {
  private class DefaultDisability2Code(dCode: String) : IDisabilityCode2 {
    override val disabilityCode: String = dCode
  }

  companion object {
    @JvmStatic
    fun of(code: String): IDisabilityCode2 {
      return DefaultDisability2Code(code)
    }
  }

  @get:Transient
  @get:JsonIgnore
  override val idcard2Code: String get() = disabilityCode.substring(0, -2)

  @get:Transient
  @get:JsonIgnore
  val disabilityCode: String
}
