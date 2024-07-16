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
package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.util.encrypt.IKeysRepo
import net.yan100.compose.security.jwt.JwtIssuer
import net.yan100.compose.security.jwt.JwtVerifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(net.yan100.compose.security.properties.JwtProperties::class)
class JwtVerifierAutoConfiguration(private val jp: net.yan100.compose.security.properties.JwtProperties, private val kr: IKeysRepo) {
  @Bean
  @ConditionalOnMissingBean(value = [JwtVerifier::class, JwtIssuer::class])
  fun jwtVerifier(mapper: ObjectMapper): JwtVerifier {
    // TODO 完成此类

    return JwtVerifier.createVerifier()
      .issuer(jp.issuer)
      .contentDecryptKey(kr.jwtEncryptDataVerifierKey()!!)
      .signatureVerifyKey(kr.jwtSignatureVerifierRsaPublicKey()!!)
      .encryptDataKeyName(jp.encryptDataKeyName)
      .serializer(mapper)
      .build()
  }
}
