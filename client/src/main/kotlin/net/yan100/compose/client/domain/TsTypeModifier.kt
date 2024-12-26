package net.yan100.compose.client.domain

sealed class TsTypeModifier(
  open val marker: String
) {
  data object None : TsTypeModifier(marker = "")
  data object Type : TsTypeModifier(marker = "type")
  data object Const : TsTypeModifier(marker = "const")
  data object Function : TsTypeModifier(marker = "function")
  data object Interface : TsTypeModifier(marker = "interface")
  data object Class : TsTypeModifier(marker = "class")
  data object Enum : TsTypeModifier(marker = "enum")
}
