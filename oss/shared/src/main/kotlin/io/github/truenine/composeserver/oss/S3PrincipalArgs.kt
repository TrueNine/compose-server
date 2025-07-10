package io.github.truenine.composeserver.oss

import com.fasterxml.jackson.annotation.JsonProperty

data class S3PrincipalArgs(@JsonProperty("AWS") var aws: MutableList<String> = ArrayList())
