package net.yan100.compose.oss.common

import com.fasterxml.jackson.annotation.JsonProperty

data class S3PrincipalArgs(
  @JsonProperty("AWS")
  var aws: MutableList<String> = ArrayList()
)
