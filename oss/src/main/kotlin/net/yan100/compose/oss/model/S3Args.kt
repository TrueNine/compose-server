package net.yan100.compose.oss.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class S3Args(
  @JsonProperty("Version")
  @SerializedName("Version")
  var version: String? = null,
  @JsonProperty("Statement")
  @SerializedName("Statement")
  var statement: MutableList<S3StatementArgs> = ArrayList(),
)
