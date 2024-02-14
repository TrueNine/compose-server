package net.yan100.compose.security.oauth2.models.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * ## 微信公众号开发者接入指南
 *
 * 开发者通过检验signature对请求进行校验（下面有校验方式）。若确认此次GET请求来自微信服务器，请原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败。加密/校验流程如下：
 *
 * 1）将token、timestamp、nonce三个参数进行字典序排序
 *
 * 2）将三个参数字符串拼接成一个字符串进行sha1加密
 *
 * 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
 */
open class WxpaVerifyModel {
    /**
     * 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     */
    @Schema(title = "微信加密签名", description = "signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数")
    var signature: String? = null

    /**
     * 时间戳
     */
    @Schema(title = "时间戳")
    var timestamp: Long? = null

    /**
     * 随机数
     */
    @Schema(title = "随机数")
    var nonce: String? = null

    /**
     * 随机字符串
     */
    @Schema(title = "随机字符串")
    var echostr: String? = null
}

@Schema(
    title = "公众号获取 access_token 返回结果"
)
open class WxpaGetAccessTokenResp : BaseWxpaResp() {
    @Schema(title = "公众号 access_token")
    @JsonProperty("access_token")
    open var accessToken: String? = null
}

@Schema(title = "公众号获取 ticket 返回结果")
open class WxpaGetTicketResp : BaseWxpaResp() {
    @Schema(title = "票证")
    open var ticket: String? = null
}
