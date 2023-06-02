package net.yan100.compose.security.oauth2.models.api


import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.wechat.WechatMpGrantTyping


class JsCodeToSessionApiReq {
  @Schema(title = "小程序 appId")
  @JsonProperty("appid")
  lateinit var mpAppId: String

  @Schema(title = "小程序 appSecret")
  @JsonProperty("secret")
  lateinit var mpSecret: String

  @Schema(title = "登录时获取的 code", description = "可通过wx.login获取")
  @JsonProperty("js_code")
  lateinit var jsCode: String

  @Schema(title = "授权类型", description = "此处只需填写 authorization_code")
  @JsonProperty("grant_type")
  var grantTyping: WechatMpGrantTyping? = WechatMpGrantTyping.AUTH_CODE
}


@Schema(
  title = "小程序登录返回", description = """
  登录凭证校验。通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程。更多使用方法详见小程序登录。
"""
)
open class JsCodeToSessionApiResp {
  @Schema(title = "会话密钥")
  @JsonProperty("session_key")
  var sessionKey: String? = null

  @Schema(title = "开放平台唯一标识符", description = """用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回，详见 UnionID 机制说明。""")
  @JsonProperty("unionid")
  var unionId: String? = null

  @Schema(title = "用户唯一标识")
  @JsonProperty("openid")
  var openid: String? = null

  @Schema(title = "错误信息")
  @JsonProperty("errmeg")
  var errorMessage: String? = null

  @Schema(title = "错误码")
  @JsonProperty("errcode")
  var errorCode: Int? = null
}

fun JsCodeToSessionApiResp.toStandard(): JsCodeToSessionResp {
  return JsCodeToSessionResp().also {
    it.sessionKey = sessionKey
    it.unionId = unionId
    it.openid = openid
    it.errorMessage = errorMessage
    it.errorCode = errorCode
  }
}


@Schema(
  title = "小程序登录返回（标准接口）", description = """
  登录凭证校验。通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程。更多使用方法详见小程序登录。
"""
)
open class JsCodeToSessionResp {
  @Schema(title = "会话密钥")
  var sessionKey: String? = null

  @Schema(title = "开放平台唯一标识符", description = """用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回，详见 UnionID 机制说明。""")
  var unionId: String? = null

  @Schema(title = "用户唯一标识")
  var openid: String? = null

  @Schema(title = "错误信息")
  var errorMessage: String? = null

  @Schema(title = "错误码")
  var errorCode: Int? = null
}
