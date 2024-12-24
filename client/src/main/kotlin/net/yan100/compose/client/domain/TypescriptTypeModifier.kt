package net.yan100.compose.client.domain

sealed class TypescriptTypeModifier(
  open val marker: String
) {
  data class Type(
    override val marker: String = "type"
  ) : TypescriptTypeModifier(marker = marker)

  data class Const(
    override val marker: String = "const"
  ) : TypescriptTypeModifier(marker = marker)


  data class Function(
    override val marker: String = "function"
  ) : TypescriptTypeModifier(marker = marker)

  data class Interface(
    override val marker: String = "interface"
  ) : TypescriptTypeModifier(marker = marker)

  data class Class(
    override val marker: String = "class"
  ) : TypescriptTypeModifier(marker = marker)


  data class Enum(
    override val marker: String = "enum"
  ) : TypescriptTypeModifier(marker = marker)

  data class None(
    override val marker: String = ""
  ) : TypescriptTypeModifier(marker = marker)
}
