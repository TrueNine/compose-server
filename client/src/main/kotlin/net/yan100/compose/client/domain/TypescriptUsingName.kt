package net.yan100.compose.client.domain

sealed class TypescriptUsingName {
  data class AsName(
    val name: String,
    val asName: String
  ) : TypescriptUsingName()

  data class Name(
    val name: String
  ) : TypescriptUsingName()
}
