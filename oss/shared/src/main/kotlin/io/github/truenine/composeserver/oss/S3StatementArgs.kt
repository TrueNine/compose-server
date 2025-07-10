package io.github.truenine.composeserver.oss

import com.fasterxml.jackson.annotation.JsonProperty

data class S3StatementArgs(
  @JsonProperty("Effect") var effect: String? = null,
  @JsonProperty("Action") var action: MutableList<String> = ArrayList(),
  @JsonProperty("Principal") var principal: S3PrincipalArgs? = null,
  @JsonProperty("Resource") var resource: MutableList<String> = ArrayList(),
)
