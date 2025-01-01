package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName

sealed class TsUseVal<T : TsUseVal<T>>(
  open val typeValue: TsTypeVal<*>,
  open val partial: Boolean = false
) {
  data class ReturnType(
    override val typeValue: TsTypeVal<*>,
    override val partial: Boolean = false
  ) : TsUseVal<ReturnType>(
    typeValue = typeValue,
    partial = partial
  )

  data class ParameterType(
    val name: TsName.Name,
    override val typeValue: TsTypeVal<*>,
    override val partial: Boolean = false
  ) : TsUseVal<ParameterType>(
    typeValue,
    partial
  ) {
    override fun toTsTypeProperty(): TsTypeProperty = super.toTsTypeProperty().copy(name = name)
  }

  open fun toTsTypeProperty(): TsTypeProperty {
    return TsTypeProperty(
      name = TsName.Anonymous,
      partial = partial,
      defined = typeValue
    )
  }
}
