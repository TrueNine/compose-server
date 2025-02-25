package net.yan100.compose.oss.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class S3StatementArgs(
  @JsonProperty("Effect") @SerializedName("Effect") var effect: String? = null,
  @JsonProperty("Action")
  @SerializedName("Action")
  var action: MutableList<String> = ArrayList(),
  @JsonProperty("Principal")
  @SerializedName("Principal")
  var principal: S3PrincipalArgs? = null,
  @JsonProperty("Resource")
  @SerializedName("Resource")
  var resource: MutableList<String> = ArrayList(),
)
