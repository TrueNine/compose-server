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

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.httpexchange.jsonWebClientRegister
import net.yan100.compose.security.oauth2.api.IWxMpApi
import net.yan100.compose.security.oauth2.api.IWxpaWebClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = slf4j(ApiExchangeAutoConfiguration::class)

@Configuration
class ApiExchangeAutoConfiguration {

  @Bean
  fun wxMpApi(objectMapper: ObjectMapper): IWxMpApi {
    return jsonWebClientRegister<IWxMpApi>(objectMapper) { a, b -> a to b }
  }

  @Bean
  fun wxpaApi(objectMapper: ObjectMapper): IWxpaWebClient {
    return jsonWebClientRegister<IWxpaWebClient>(objectMapper) { a, b -> a to b }
  }
}
