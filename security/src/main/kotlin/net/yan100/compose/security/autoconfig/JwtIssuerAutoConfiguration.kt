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
import net.yan100.compose.security.crypto.domain.IKeysRepo
import net.yan100.compose.security.jwt.JwtIssuer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
@EnableConfigurationProperties(net.yan100.compose.security.properties.JwtProperties::class)
class JwtIssuerAutoConfiguration(private val jp: net.yan100.compose.security.properties.JwtProperties, private val keysRepository: IKeysRepo) {
  @Bean
  @Primary
  fun jwtIssuer(mapper: ObjectMapper): JwtIssuer {
    // TODO 完成此类
    val sig = keysRepository.jwtSignatureIssuerRsaKeyPair()!!
    val enc = keysRepository.jwtEncryptDataIssuerEccKeyPair()!!
    return JwtIssuer.createIssuer()
      .signatureIssuerKey(sig.privateKey)
      .signatureVerifyKey(sig.publicKey)
      .contentEncryptKey(enc.publicKey)
      .contentDecryptKey(enc.privateKey)
      .expireFromDuration(Duration.of(7, ChronoUnit.DAYS))
      .serializer(mapper)
      .encryptDataKeyName(jp.encryptDataKeyName)
      .issuer(jp.issuer)
      .expire(jp.expiredDuration)
      .build()
  }
}
