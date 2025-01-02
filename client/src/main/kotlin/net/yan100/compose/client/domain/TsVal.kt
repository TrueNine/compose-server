package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName

sealed class TsVal(
  open val modifiers: List<TsModifier> = emptyList(),
  open val quota: TsScopeQuota = TsScopeQuota.OBJECT,
) {
  data class Constructor(
    override val modifiers: List<TsModifier> = emptyList(),
    override val quota: TsScopeQuota = TsScopeQuota.OBJECT,
    val params: List<TsUseVal.Parameter> = emptyList(),
  ) : TsVal(modifiers = modifiers, quota = quota)

  data class Function(
    override val modifiers: List<TsModifier> = emptyList(),
    val name: TsName.Name,
    val params: List<TsUseVal.Parameter> = emptyList(),
    val returnType: TsUseVal.Return = TsUseVal.Return(TsTypeVal.Void),
    override val quota: TsScopeQuota = TsScopeQuota.OBJECT,
  ) : TsVal(modifiers = modifiers, quota = quota) {
    val isAsync: Boolean = returnType.typeVal is TsTypeVal.Promise
  }

  val isReadonly: Boolean get() = modifiers.any { it is TsModifier.Readonly }
  val isStatic: Boolean get() = modifiers.any { it is TsModifier.Static }
}
