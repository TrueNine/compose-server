/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.pay.api

import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.service.annotation.HttpExchange

/**
 * # 微信支付 js API
 *
 * @author shanghua
 * @since 2023-05-31
 */
@ResponseBody @HttpExchange(url = "https://api.weixin.qq.com/") interface WechatPayV3JsApi
