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
package net.yan100.compose.security.oauth2.typing.wechat

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.typing.StringTyping

/**
 * # 微信支付验证类型
 *
 * @author TrueNine
 * @since 2023-05-31
 */
enum class WechatMpGrantTyping(private val typingCode: String) : StringTyping {
  CLIENT_CREDENTIAL("client_credential"),
  AUTH_CODE("authorization_code");

  @JsonValue override val value: String = typingCode

  companion object {
    @JvmStatic fun findVal(v: String?) = entries.find { it.typingCode == v }
  }
}
