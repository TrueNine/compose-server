package net.yan100.compose.client.domain

data class TypescriptImport(
  val fromPath: String,
  val useType: Boolean,
  val usingNames: List<TypescriptUsingName>
)
