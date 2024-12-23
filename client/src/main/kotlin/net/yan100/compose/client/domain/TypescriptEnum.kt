package net.yan100.compose.client.domain

data class TypescriptEnum(
  val name: String,
  val isExport: Boolean,
  val isString: Boolean,
  val constants: Map<String, Comparable<*>>
)
