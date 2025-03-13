package net.yan100.compose.oss.common

import com.fasterxml.jackson.annotation.JsonProperty

data class S3Args(
  @JsonProperty("Version")
  var version: String? = null,
  @JsonProperty("Statement")
  var statement: MutableList<S3StatementArgs> = ArrayList(),
)
