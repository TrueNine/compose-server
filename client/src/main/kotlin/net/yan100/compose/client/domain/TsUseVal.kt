package net.yan100.compose.client.domain

import net.yan100.compose.client.TsTypeDefine
import net.yan100.compose.client.domain.entries.TsName

sealed class TsUseVal<T : TsUseVal<T>>(
  open val typeValue: TsTypeVal<*>,
  open val partial: Boolean = false
) : TsTypeDefine<T> {
  override val isBasic: Boolean
    get() = when (this) {
      is ParameterType -> typeValue.isBasic
      is ReturnType -> typeValue.isBasic
    }
  override val isRequireUseGeneric: Boolean
    get() = when (this) {
      is ParameterType -> typeValue.isRequireUseGeneric
      is ReturnType -> typeValue.isRequireUseGeneric
    }

  @Suppress("UNCHECKED_CAST")
  override fun fillGenerics(vararg generic: TsGeneric): T {
    if (generic.isEmpty()) return this as T
    return fillGenerics(generic.toList())
  }

  @Suppress("UNCHECKED_CAST")
  override fun fillGenerics(usedGenerics: List<TsGeneric>): T {
    if (usedGenerics.isEmpty()) return this as T
    return when (this) {
      is ParameterType -> copy(typeValue = typeValue.fillGenerics(usedGenerics)) as T
      is ReturnType -> copy(typeValue = typeValue.fillGenerics(usedGenerics)) as T
    }
  }

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
