package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName

data class TspService(
  val name: TsName,
  val operations: List<TsOperation>
)
