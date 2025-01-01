package net.yan100.compose.client.domain

import net.yan100.compose.client.TsTypeDefine
import net.yan100.compose.client.domain.entries.TsName

sealed class TsUseVal<T : TsUseVal<T>>(
  open val typeVal: TsTypeVal<*>,
  open val partial: Boolean = false
) : TsTypeDefine<T> {
  override val isBasic: Boolean
    get() = when (this) {
      is Parameter -> typeVal.isBasic
      is Return -> typeVal.isBasic
      is Prop -> typeVal.isBasic
    }
  override val isRequireUseGeneric: Boolean
    get() = when (this) {
      is Parameter -> typeVal.isRequireUseGeneric
      is Return -> typeVal.isRequireUseGeneric
      is Prop -> typeVal.isRequireUseGeneric
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
      is Parameter -> copy(typeVal = typeVal.fillGenerics(usedGenerics)) as T
      is Return -> copy(typeVal = typeVal.fillGenerics(usedGenerics)) as T
      is Prop -> copy(typeVal = typeVal.fillGenerics(usedGenerics)) as T
    }
  }

  data class Prop(
    val name: TsName,
    override val typeVal: TsTypeVal<*>,
    override val partial: Boolean = false
  ) : TsUseVal<Prop>(
    typeVal = typeVal,
    partial = partial
  )

  data class Return(
    override val typeVal: TsTypeVal<*>,
    override val partial: Boolean = false
  ) : TsUseVal<Return>(
    typeVal = typeVal,
    partial = partial
  )

  data class Parameter(
    val name: TsName.Name,
    override val typeVal: TsTypeVal<*>,
    override val partial: Boolean = false
  ) : TsUseVal<Parameter>(
    typeVal = typeVal,
    partial = partial
  )
}
