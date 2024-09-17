/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 * 
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
*/
package net.yan100.compose.pay.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.pay"

/**
 * # 微信单支付 js API 配置 <br></br>
 * [微信支付文档](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_1.shtml)
 *
 * @author shanghua
 * @since 2023-05-05
 */
@ConfigurationProperties(prefix = "$PREFIX.wechat", ignoreUnknownFields = true)
data class WeChatPayProperties (
  /** 开启 单配置支付  */
  var enableSingle: Boolean = false,

  /** 商户号  */
  var merchantId: String? = null,

  /** 商户序列号  */
  var merchantSerialNumber: String? = null,

  /** cret文件存放路径  */
  var certPath: String = WECHAT_KEY_DIR + "apiclient_cert.pem",

  /** 私钥文件存放路径  */
  var privateKeyPath: String = WECHAT_KEY_DIR + "apiclient_key.pem",

  /** appId 小程序 Id  */
  var mpAppId: String? = null,

  /**
   * 支付成功异步通知 url <br></br>
   * [微信支付通知文档](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_5.shtml)
   */
  var asyncSuccessNotifyUrl: String? = null,

  /**
   * 异步成功退款通知 url <br></br>
   * [微信退款文档](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_11.shtml)
   */
  var asyncSuccessRefundNotifyUrl: String? = null,

  /** api 密钥  */
  var apiSecret: String? = null,

  /** 微信支付 jsAPI v3 私钥  */
  var apiV3Key: String? = null
) {
  companion object {
    const val WECHAT_KEY_DIR = "security/wechat/pay/"
  }
}

