package io.github.truenine.composeserver.security.oauth2.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.truenine.composeserver.security.oauth2.typing.WechatMpGrantTyping
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * WeChat Mini Program authentication and authorization API.
 *
 * @author TrueNine
 * @since 2023-05-31
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface IWxMpApi {
  @Schema(
    title = "Mini Program login response (standard API)",
    description =
      """
      Login credential validation. After obtaining the temporary login credential code via wx.login,
      the client sends it to the server to call this API and complete the login flow.
      See WeChat Mini Program login documentation for details.
      """,
  )
  class JsCodeToSessionResp {
    @Schema(title = "Session key") var sessionKey: String? = null

    @Schema(
      title = "Open platform unique identifier",
      description =
        """User's unique identifier on the Open Platform. Returned when the current Mini Program is bound
        to a WeChat Open Platform account. See UnionID specification for details.""",
    )
    var unionId: String? = null

    @Schema(title = "User unique identifier") var openId: String? = null

    @Schema(title = "Error message") var errorMessage: String? = null

    @Schema(title = "Error code") var errorCode: Int? = null
  }

  @Schema(
    title = "Mini Program login response",
    description =
      """
      Login credential validation. After obtaining the temporary login credential code via wx.login,
      the client sends it to the server to call this API and complete the login flow.
      See WeChat Mini Program login documentation for details.
      """,
  )
  class WxMpJsCodeToSessionResp {
    @Schema(title = "Session key") @JsonProperty("session_key") var sessionKey: String? = null

    @Schema(
      title = "Open platform unique identifier",
      description =
        """User's unique identifier on the Open Platform. Returned when the current Mini Program is bound
        to a WeChat Open Platform account. See UnionID specification for details.""",
    )
    @JsonProperty("unionid") var unionId: String? = null

    @Schema(title = "User unique identifier") @JsonProperty("openid") var openId: String? = null

    @Schema(title = "Error message") @JsonProperty("errmeg") var errorMessage: String? = null

    @Schema(title = "Error code") @JsonProperty("errcode") var errorCode: Int? = null
  }

  class JsCodeToSessionApiReq {
    @Schema(title = "Mini Program appId") @JsonProperty("appid") lateinit var mpAppId: String

    @Schema(title = "Mini Program appSecret") @JsonProperty("secret") lateinit var mpSecret: String

    @Schema(title = "Login credential code", description = "Obtained via wx.login") @JsonProperty("js_code") lateinit var jsCode: String

    @Schema(title = "Grant type", description = "Must be 'authorization_code'")
    @JsonProperty("grant_type")
    var grantTyping: WechatMpGrantTyping? = WechatMpGrantTyping.AUTH_CODE
  }

  /**
   * WeChat Mini Program login.
   *
   * @param appId WeChat Mini Program appId
   * @param secret WeChat Mini Program secret
   * @param jsCode Login credential code obtained via wx.login
   * @param grantType OAuth2 grant type
   */
  @ResponseBody
  @GetExchange(value = "sns/jscode2session", accept = ["application/json", "text/plain"])
  fun jsCodeToSession(
    @RequestParam(name = "appid") appId: String,
    @RequestParam(name = "secret") secret: String,
    @RequestParam(name = "js_code") jsCode: String,
    @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE,
  ): WxMpJsCodeToSessionResp
}
