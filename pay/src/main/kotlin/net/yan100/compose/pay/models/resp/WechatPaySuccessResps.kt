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
package net.yan100.compose.pay.models.resp

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * ## 微信支付成功通知
 *
 * [支付通知api](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_5.shtml)
 *
 * @author TrueNine
 * @since 2023-05-06
 */
@Deprecated(message = "暂时不用封装")
@Schema(title = "微信支付成功通知回调")
class WechatPaySuccessResp {
  @Schema(title = "通知 id") var id: String? = null

  @Schema(title = "创建时间")
  @JsonProperty("create_time")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  var createDatetime: LocalDateTime? = null

  @Schema(title = "通知类型", description = "通知的类型，支付成功通知的类型为TRANSACTION.SUCCESS")
  @JsonProperty("event_type")
  var eventType: String? = null

  @Schema(title = "通知数据类型") @JsonProperty("resource_type") var resourceType: String? = null

  @Schema(title = "通知数据")
  @JsonProperty("resource")
  var resource: WechatPaySuccessRespResource? = null
}

@Schema(title = "通知数据")
class WechatPaySuccessRespResource {
  @Schema(title = "加密算法类型", description = "对开启结果数据进行加密的加密算法，目前只支持AEAD_AES_256_GCM")
  var algorithm: String? = null

  @Schema(title = "数据密文") @JsonProperty("ciphertext") var cipherText: String? = null

  @Schema(title = "附加数据") @JsonProperty("associated_data") var associatedDate: String? = null

  @Schema(title = "原始类型", description = "原始回调类型，为transaction")
  @JsonProperty("original_type")
  var originalType: String? = null

  @Schema(title = "随机串", description = "加密使用的随机串")
  @JsonProperty("nonce")
  var randomString: String? = null
}
