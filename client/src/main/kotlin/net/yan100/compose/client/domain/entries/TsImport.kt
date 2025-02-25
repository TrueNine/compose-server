package net.yan100.compose.client.domain.entries

data class TsImport(
  val fromPath: String,
  val useType: Boolean,
  val usingNames: List<TsName>,
)
