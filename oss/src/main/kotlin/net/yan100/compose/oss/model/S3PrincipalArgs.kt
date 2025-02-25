package net.yan100.compose.oss.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class S3PrincipalArgs(
  @JsonProperty("AWS")
  @SerializedName("AWS")
  var aws: MutableList<String> = ArrayList()
)
