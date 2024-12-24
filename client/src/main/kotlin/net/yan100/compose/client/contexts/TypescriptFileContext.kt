package net.yan100.compose.client.contexts

import net.yan100.compose.client.domain.entries.TypescriptFile

data class TypescriptFileContext(
  val files: List<TypescriptFile>
)
