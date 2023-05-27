package net.yan100.compose.pay.models.request

import com.fasterxml.jackson.annotation.JsonProperty


open class GetSessionApiRequest {
  @JsonProperty("appid")
  open var appId: String? = null

  open var secret: String? = null

  @JsonProperty("js_code")
  open var jsCode: String? = null

  @JsonProperty("grant_type")
  open var grantType = "authorization_code"
}
