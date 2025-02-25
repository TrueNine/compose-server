package net.yan100.compose.meta.types

import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.yan100.compose.meta.client.ClientDoc

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = ClientDoc::class)
interface Doc {
  val value: String?
  val title: String?
  val description: String?
  val parameters: Map<String, String>
  val deprecated: Boolean?
  val deprecatedMessage: String?
  val since: String?
  val author: String?
}
