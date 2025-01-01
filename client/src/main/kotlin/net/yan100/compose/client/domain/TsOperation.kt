package net.yan100.compose.client.domain

import net.yan100.compose.core.typing.HTTPMethod

data class TsOperation(
  val name: String,
  val key: String,
  val path: String,
  val methods: List<HTTPMethod>,
  val requestType: String,
  val responseType: String,
  val parameterMap: Map<String, TsTypeVal<*>> = emptyMap(),
  val returnType: TsTypeVal<*>,
)
