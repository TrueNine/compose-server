package net.yan100.compose.client.domain

data class TypescriptExportNameScope(
  val name: String,
  val isDefault: Boolean = false,
  val fromPath: String? = null
)
