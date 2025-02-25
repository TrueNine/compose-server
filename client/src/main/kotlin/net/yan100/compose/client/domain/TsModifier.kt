package net.yan100.compose.client.domain

import net.yan100.compose.client.TsSymbol

sealed class TsModifier(open val modifier: String) : TsSymbol {
  override fun toString(): String = modifier

  data object Default : TsModifier(modifier = "default") {
    override fun toString(): String = modifier
  }

  data object None : TsModifier(modifier = "") {
    override fun toString(): String = modifier
  }

  data object Export : TsModifier(modifier = "export") {
    override fun toString(): String = modifier
  }

  data object Const : TsModifier(modifier = "const") {
    override fun toString(): String = modifier
  }

  data object Var : TsModifier(modifier = "var") {
    override fun toString(): String = modifier
  }

  data object Let : TsModifier(modifier = "let") {
    override fun toString(): String = modifier
  }

  data object Readonly : TsModifier(modifier = "readonly") {
    override fun toString(): String = modifier
  }

  data object Abstract : TsModifier(modifier = "abstract") {
    override fun toString(): String = modifier
  }

  data object Async : TsModifier(modifier = "async") {
    override fun toString(): String = modifier
  }

  data object Await : TsModifier(modifier = "await") {
    override fun toString(): String = modifier
  }

  data object From : TsModifier(modifier = "from") {
    override fun toString(): String = modifier
  }

  data object Import : TsModifier(modifier = "import") {
    override fun toString(): String = modifier
  }

  data object Of : TsModifier(modifier = "of") {
    override fun toString(): String = modifier
  }

  data object Extends : TsModifier(modifier = "extends") {
    override fun toString(): String = modifier
  }

  data object Implements : TsModifier(modifier = "implements") {
    override fun toString(): String = modifier
  }

  data object In : TsModifier(modifier = "in") {
    override fun toString(): String = modifier
  }

  data object Instanceof : TsModifier(modifier = "instanceof") {
    override fun toString(): String = modifier
  }

  data object As : TsModifier(modifier = "as") {
    override fun toString(): String = modifier
  }

  data object Static : TsModifier(modifier = "static") {
    override fun toString(): String = modifier
  }

  data object Private : TsModifier(modifier = "private") {
    override fun toString(): String = modifier
  }

  data object Public : TsModifier(modifier = "public") {
    override fun toString(): String = modifier
  }
}
