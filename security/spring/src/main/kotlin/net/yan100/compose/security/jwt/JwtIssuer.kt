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
package net.yan100.compose.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.DTimer
import net.yan100.compose.core.slf4j
import net.yan100.compose.security.crypto.Encryptors
import net.yan100.compose.security.jwt.consts.IssuerParam
import org.slf4j.Logger
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration

class JwtIssuer private constructor() : JwtVerifier() {
  var expireMillis: Long = Duration.ofMinutes(30).toMillis()
  var signatureIssuerKey: RSAPrivateKey? = null
  var contentEccPublicKey: PublicKey? = null

  fun <S : Any, E : Any> issued(params: IssuerParam<S, E>): String {
    return JWT.create()
      .withIssuer(issuer)
      .withJWTId(id)
      .apply {
        // 是否包含明文主题，有则进行转换
        if (params.containSubject()) {
          withSubject(createContent(params.subjectObj!!))
        }
        // 包含加密块则加密
        if (params.containEncryptContent()) {
          withClaim(
            this@JwtIssuer.encryptDataKeyName,
            encryptData(createContent(params.encryptedDataObj!!), params.contentEncryptEccKey ?: this@JwtIssuer.contentEccPublicKey!!),
          )
        }
        withExpiresAt(DTimer.plusMillisFromCurrent(params.duration?.toMillis() ?: this@JwtIssuer.expireMillis))
      }
      .sign(Algorithm.RSA256(params.signatureKey ?: this.signatureIssuerKey))
  }

  internal fun createContent(content: Any): String =
    runCatching { objectMapper.writeValueAsString(content) }.onFailure { log.warn("jwt json 签发异常，或许没有配置序列化器", it) }.getOrElse { "{}" }

  internal fun encryptData(encData: String, eccPublicKey: PublicKey): String? = Encryptors.encryptByEccPublicKey(eccPublicKey, encData)

  inner class Builder {
    fun build(): JwtIssuer = this@JwtIssuer

    fun expireFromDuration(duration: Duration): Builder {
      this@JwtIssuer.expireMillis = duration.toMillis()
      return this
    }

    fun expire(expireMillis: Long): Builder {
      this@JwtIssuer.expireMillis = expireMillis
      return this
    }

    fun serializer(mapper: ObjectMapper? = null): Builder {
      mapper?.let { objectMapper = it }
      return this
    }

    fun encryptDataKeyName(name: String): Builder {
      this@JwtIssuer.encryptDataKeyName = name
      return this
    }

    fun contentDecryptKey(contentDecryptKey: PrivateKey): Builder {
      this@JwtIssuer.contentEccPrivateKey = contentDecryptKey
      return this
    }

    fun contentEncryptKey(contentEncryptKey: PublicKey): Builder {
      this@JwtIssuer.contentEccPublicKey = contentEncryptKey
      return this
    }

    fun signatureVerifyKey(signatureDecryptKey: RSAPublicKey): Builder {
      this@JwtIssuer.signatureVerifyKey = signatureDecryptKey
      return this
    }

    fun signatureIssuerKey(signatureEncryptKey: RSAPrivateKey): Builder {
      this@JwtIssuer.signatureIssuerKey = signatureEncryptKey
      return this
    }

    fun issuer(issuer: String): Builder {
      this@JwtIssuer.issuer = issuer
      return this
    }

    fun id(id: String): Builder {
      this@JwtIssuer.id = id
      return this
    }
  }

  companion object {
    @JvmStatic
    fun createIssuer() = JwtIssuer().Builder()

    @JvmStatic
    private val log: Logger = slf4j(JwtIssuer::class)
  }
}