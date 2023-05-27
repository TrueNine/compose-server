package net.yan100.compose.pay.models.response

import com.fasterxml.jackson.annotation.JsonProperty


open class GetSessionApiResponse {
  @JsonProperty("openid")
  open var openId: String? = null

  @JsonProperty("session_key")
  open var sessionKey: String? = null

  @JsonProperty("unionid")
  open var uniOnId: String? = null

  @JsonProperty("errcode")
  open var errorCode: String? = null

  @JsonProperty("errmsg")
  open var errorMessage: String? = null
}
