package com.truenine.component.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.annotations.VisibleForTesting
import com.google.gson.Gson
import com.truenine.component.core.encrypt.Encryptors
import com.truenine.component.core.lang.DTimer
import com.truenine.component.core.lang.LogKt
import org.slf4j.Logger
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration

class JwtIssuer private constructor() : JwtVerifier() {
  var expireMillis: Long = Duration.ofMinutes(30).toMillis()
  var signatureIssuerKey: RSAPrivateKey? = null

  fun <S : Any, E : Any> issued(params: IssuerParams<S, E>): String =
    basicCreator(
      subject = createContent(params.subjectObj!!),
      signatureKey = params.signatureKey,
      encryptData = if (isEncrypt(params)) encryptData(
        createContent(params.encryptedDataObj!!),
        params.contentEncryptEccKey!!
      ) else null,
      issuer = params.issuer ?: this.issuer,
      id = params.id ?: this.id,
      encKeyName = this.encryptDataKeyName
    )

  private fun <S : Any, E : Any> isEncrypt(p: IssuerParams<S, E>): Boolean {
    return null != p.encryptedDataObj
      && null != p.contentEncryptEccKey
  }

  @VisibleForTesting
  internal fun basicCreator(
    subject: String,
    signatureKey: RSAPrivateKey,
    encryptData: String? = null,
    issuer: String? = this.issuer,
    id: String? = this.id,
    encKeyName: String? = this.encryptDataKeyName
  ): String {
    return JWT.create()
      .withIssuer(issuer)
      .withJWTId(id)
      .withSubject(subject ?: "none")
      .withClaim(encKeyName, encryptData ?: "none")
      .withExpiresAt(DTimer.plusMillisFromCurrent(expireMillis))
      .sign(Algorithm.RSA256(signatureKey))
  }

  @VisibleForTesting
  internal fun createContent(
    content: Any
  ): String = kotlin.runCatching {
    objectMapper?.writeValueAsString(content)
      ?: gson?.toJson(content)!!
  }.onFailure { log.warn("jwt json 解析异常，或许没有配置序列化器", it) }
    .getOrElse {
      "{}"
    }

  @VisibleForTesting
  internal fun encryptData(
    encData: String,
    eccPublicKey: PublicKey
  ): String? = Encryptors.encryptByEccPublicKey(eccPublicKey, encData)

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


    fun serializer(
      mapper: ObjectMapper? = null,
      gson: Gson? = null
    ): Builder {
      mapper?.let { objectMapper = it }
        ?: gson?.let { this@JwtIssuer.gson = it }
        ?: run {
          this@JwtIssuer.gson = Gson()
          log.warn(
            "没有内容序列化器，将使用一个默认序列化器 = {}",
            this@JwtIssuer.gson
          )
        }
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
    private val log: Logger = LogKt.getLog(JwtIssuer::class)
  }
}
