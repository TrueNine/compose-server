package net.yan100.compose.security.oauth2.models.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

open class BaseWxpaResp {
    @JsonProperty("errcode")
    open var errorCode: Int? = null

    @JsonProperty("errmsg")
    open var errorMessage: String? = null

    @Schema(title = "过期时间限制（在多久后过期），使用秒表示")
    @JsonProperty("expires_in")
    open var expireInSecond: Long? = null

    @get:JsonIgnore
    open val isError: Boolean get() = errorCode != null && errorCode != 0
}
