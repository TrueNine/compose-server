package net.yan100.compose.client.domain

data class TypescriptInterface(
  val name: String,
  val generics: List<String> = emptyList(),
  val properties: List<TypescriptTypeProp> = emptyList()
)
