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
package net.yan100.compose.security.oauth2.autoconfig

import net.yan100.compose.core.slf4j
import net.yan100.compose.security.oauth2.api.IWxpaWebClient
import net.yan100.compose.security.oauth2.properties.WechatProperties
import net.yan100.compose.security.oauth2.property.WxpaProperty
import net.yan100.compose.security.oauth2.service.WxpaService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WxpaAutoConfiguration {
  companion object {
    private val log = slf4j(WxpaAutoConfiguration::class)
  }

  @Bean
  fun wxpaService(
    client: IWxpaWebClient,
    property: WxpaProperty
  ): WxpaService {
    return WxpaService(
      client = client,
      property = property
    )
  }

  @Bean
  fun wxpaProperty(properties: WechatProperties): WxpaProperty {
    log.trace("注册 wechat 相关属性配置 = {}", properties)
    val p = WxpaProperty()

    val pa = properties.wxpa
    p.appId = pa.appId!!
    p.fixedExpiredSecond = pa.fixedExpiredSecond
    p.preValidToken = pa.verifyToken!!
    p.appSecret = pa.appSecret!!

    return p
  }
}
