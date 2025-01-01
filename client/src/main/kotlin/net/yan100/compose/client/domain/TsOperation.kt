package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.core.typing.HTTPMethod

data class TsOperation(
  val name: TsName.Name,
  val key: String,
  val path: String,
  val methods: List<HTTPMethod>,
  val requestType: String,
  val responseType: String,
  val parameters: List<TsUseVal.Parameter> = emptyList(),
  val returns: TsUseVal.Return,
)
