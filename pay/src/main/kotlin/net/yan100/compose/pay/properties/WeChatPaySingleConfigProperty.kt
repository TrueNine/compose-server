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

/**
 * # 微信支付 单配置属性
 *
 * @author TrueNine
 * @since 2023-05-28
 */
open class WeChatPaySingleConfigProperty {
  /**
   * ## 此属性代表是否开启了 单配置支付
   * 如果未开启，则其他属性都是未初始化的属性
   */
  open var enable: Boolean = false
  open lateinit var privateKey: String
  open lateinit var apiSecret: String
  open lateinit var merchantId: String
  open lateinit var mpAppId: String
  open lateinit var asyncSuccessNotifyUrl: String
  open lateinit var asyncSuccessRefundNotifyUrl: String
}
