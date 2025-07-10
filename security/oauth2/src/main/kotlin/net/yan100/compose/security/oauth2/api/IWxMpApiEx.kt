package net.yan100.compose.security.oauth2.api

import net.yan100.compose.security.oauth2.typing.WechatMpGrantTyping

fun IWxMpApi.jsCodeToSessionStandard(param: IWxMpApi.JsCodeToSessionApiReq): IWxMpApi.JsCodeToSessionResp =
  jsCodeToSession(appId = param.mpAppId, secret = param.mpSecret, jsCode = param.jsCode, grantType = WechatMpGrantTyping.AUTH_CODE).toStandard()

fun IWxMpApi.WxMpJsCodeToSessionResp.toStandard(): IWxMpApi.JsCodeToSessionResp {
  return IWxMpApi.JsCodeToSessionResp().also {
    it.sessionKey = sessionKey
    it.unionId = unionId
    it.openId = openId
    it.errorMessage = errorMessage
    it.errorCode = errorCode
  }
}
