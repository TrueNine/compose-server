package com.truenine.component.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.annotations.VisibleForTesting
import com.google.gson.Gson
import com.truenine.component.core.encrypt.Encryptors
import com.truenine.component.core.lang.DTimer
import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.exceptions.JwtException
import com.truenine.component.security.jwt.consts.TokenResult
import com.truenine.component.security.jwt.consts.VerifierParams
import org.slf4j.Logger
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.reflect.KClass

open class JwtVerifier internal constructor() {
  protected var issuer: String = "issuer with component framework"
  protected var id: String = "Gregorian-19700101"
  protected var encryptDataKeyName: String = "edt"
  protected var signatureVerifyKey: RSAPublicKey? = null
  protected var contentEccPrivateKey: PrivateKey? = null
  protected var objectMapper: ObjectMapper? = null
  protected var gson: Gson? = null

  @Throws(JwtException::class)
  fun <S : Any, E : Any> verify(
    params: VerifierParams<S, E>
  ): TokenResult<S, E>? = runCatching {
    JWT.require(
      Algorithm.RSA256(params.signatureKey ?: this.signatureVerifyKey)
    )
      .withIssuer(params.issuer ?: this.issuer)
      .withJWTId(params.id ?: this.id)
      .acceptLeeway(0)
      .build().let { verifier ->
        try {
          verifier.verify(params.token) // TODO 处理验证异常
        } catch (e: Exception) {
          throw parseExceptionHandle(e)
        }
        JWT.decode(params.token).let { decodedJwt ->
          TokenResult<S, E>().apply {
            if (
              decodedJwt.claims
                .containsKey(this@JwtVerifier.encryptDataKeyName)
            ) {
              this.decryptedData = decryptData(
                encData = decodedJwt.claims[this@JwtVerifier.encryptDataKeyName]
                !!.asString(),
                targetType = params.encryptDataTargetType!!,
                eccPrivateKey = params.contentEncryptEccKey
                  ?: this@JwtVerifier.contentEccPrivateKey
              )
            }
            if (decodedJwt.claims.containsKey("sub")) {
              this.subject =
                parseContent(decodedJwt.subject, params.subjectTargetType!!)
            }
            expireDateTime = DTimer.dateToLocalDatetime(decodedJwt.expiresAt)
            id = decodedJwt.id
            signatureAlgName = decodedJwt.algorithm
          }
        }
      }
  }.onFailure { log.warn("jwt 解析异常", it) }.getOrThrow()


  @VisibleForTesting
  internal fun <T : Any> decryptData(
    encData: String,
    targetType: KClass<T>,
    eccPrivateKey: PrivateKey? = this.contentEccPrivateKey,
  ): T? {
    val content = eccPrivateKey.let { priKey ->
      Encryptors.decryptByEccPrivateKey(priKey!!, encData)
    } ?: encData
    return parseContent(content, targetType)
  }

  @VisibleForTesting
  internal fun <T : Any> parseContent(json: String, classType: KClass<T>) =
    runCatching {
      objectMapper?.readValue(json, classType.java)
        ?: gson?.fromJson(json, classType.java)
    }.onFailure { log.warn("jwt 解析异常，可能没有序列化器", it) }.getOrNull()

  @VisibleForTesting
  internal fun parseExceptionHandle(e: Exception): JwtException {
    TODO("jwt方法等待处理异常")
  }

  inner class Builder {
    fun build() = this@JwtVerifier

    fun serializer(mapper: ObjectMapper? = null, gson: Gson? = null): Builder {
      mapper?.let { objectMapper = it }
        ?: gson?.let { this@JwtVerifier.gson = it }
        ?: run {
          this@JwtVerifier.gson = Gson()
          log.warn(
            "没有内容序列化器，将使用一个默认序列化器 = {}",
            this@JwtVerifier.gson
          )
        }
      return this
    }

    fun encryptDataKeyName(name: String): Builder {
      this@JwtVerifier.encryptDataKeyName = name
      return this
    }

    fun contentDecryptKey(contentDecryptKey: PrivateKey): Builder {
      this@JwtVerifier.contentEccPrivateKey = contentDecryptKey
      return this
    }

    fun signatureVerifyKey(signatureDecryptKey: RSAPublicKey): Builder {
      this@JwtVerifier.signatureVerifyKey = signatureDecryptKey
      return this
    }

    fun issuer(issuer: String): Builder {
      this@JwtVerifier.issuer = issuer
      return this
    }

    fun id(id: String): Builder {
      this@JwtVerifier.id = id
      return this
    }
  }

  companion object {
    @JvmStatic
    fun createVerifier(): Builder = JwtVerifier().Builder()

    @JvmStatic
    private val log: Logger = LogKt.getLog(JwtVerifier::class)
  }
}
