package io.github.truenine.composeserver.pay.wechat.api

import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.service.annotation.HttpExchange

/**
 * # WeChat Pay JS API
 *
 * @author shanghua
 * @since 2023-05-31
 */
@ResponseBody @HttpExchange(url = "https://api.weixin.qq.com/") interface WechatPayV3JsApi
