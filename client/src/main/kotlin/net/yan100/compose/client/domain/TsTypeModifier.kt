package net.yan100.compose.client.domain

import net.yan100.compose.client.TsSymbol

sealed class TsTypeModifier(
  open val marker: String
) : TsSymbol {
  data object None : TsTypeModifier(marker = "")
  data object Type : TsTypeModifier(marker = "type")
  data object Const : TsTypeModifier(marker = "const")
  data object Function : TsTypeModifier(marker = "function")
  data object Interface : TsTypeModifier(marker = "interface")
  data object Class : TsTypeModifier(marker = "class")
  data object Enum : TsTypeModifier(marker = "enum")
}
