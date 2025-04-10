package net.yan100.compose.oss

import com.fasterxml.jackson.annotation.JsonProperty

data class S3PrincipalArgs(
  @JsonProperty("AWS") var aws: MutableList<String> = ArrayList()
)
