package net.yan100.compose.security.oauth2.api

import net.yan100.compose.core.typing.wechat.WechatMpGrantTyping
import net.yan100.compose.security.oauth2.models.api.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * # 微信小程序认证授权
 * @author TrueNine
 * @since 2023-05-31
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface WechatMpAuthApi {

    /**
     * # 小程序登录
     *
     * @param appId appId
     * @param secret secret
     * @param jsCode 验证令牌
     * @param grantType 验证类型
     */
    @ResponseBody
    @GetExchange(value = "sns/jscode2session", accept = ["application/json", "text/plain"])
    fun jsCodeToSession(
        @RequestParam(name = "appid") appId: String,
        @RequestParam(name = "secret") secret: String,
        @RequestParam(name = "js_code") jsCode: String,
        @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE
    ): JsCodeToSessionApiResp?


    /**
     * 公众号 获取 access_token
     * @param appId appId
     * @param secret secret
     * @param grantType 验证类型
     * @return access_token
     */
    @GetExchange("cgi-bin/token")
    fun getAccessToken(
        @RequestParam(name = "appid") appId: String,
        @RequestParam(name = "secret") secret: String,
        @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.CLIENT_CREDENTIAL
    ): WxpaGetAccessTokenResp

    /**
     * jsapi 获取票证
     */
    @GetExchange("cgi-bin/ticket/getticket")
    fun getTicket(
        @RequestParam(name = "access_token") accessToken: String,
        @RequestParam(name = "type") type: String = "jsapi"
    ): WxpaGetTicketResp
}


fun WechatMpAuthApi.jsCodeToSessionStandard(param: JsCodeToSessionApiReq): JsCodeToSessionResp? = this.jsCodeToSession(
    appId = param.mpAppId,
    secret = param.mpSecret,
    jsCode = param.jsCode,
    grantType = WechatMpGrantTyping.AUTH_CODE
)?.toStandard()
