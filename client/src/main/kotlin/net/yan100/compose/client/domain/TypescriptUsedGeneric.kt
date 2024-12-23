package net.yan100.compose.client.domain

data class TypescriptUsedGeneric(
  val typeName: String,
  val index: Int,
  val usedGenerics: List<TypescriptUsedGeneric> = emptyList(),
)
