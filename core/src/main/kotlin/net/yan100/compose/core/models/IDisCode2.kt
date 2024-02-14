package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient

/**
 * 二代残疾证代码
 */
interface IDisCode2 : IIdcard2Code {
    private class DefaultDis2Code(dCode: String) : IDisCode2 {
        override val disabilityCode: String = dCode
    }

    companion object {
        @JvmStatic
        fun of(code: String): IDisCode2 {
            return DefaultDis2Code(code)
        }
    }

    @get:Transient
    @get:JsonIgnore
    override val idcard2Code: String get() = disabilityCode.substring(0, -2)

    @get:Transient
    @get:JsonIgnore
    val disabilityCode: String
}
