package io.github.truenine.composeserver.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.DateTimeConverter
import io.github.truenine.composeserver.security.crypto.CryptographicOperations
import io.github.truenine.composeserver.security.jwt.consts.JwtToken
import io.github.truenine.composeserver.security.jwt.consts.VerifierParam
import io.github.truenine.composeserver.slf4j
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.reflect.KClass

private val log = slf4j(JwtVerifier::class)

open class JwtVerifier internal constructor() {
  protected var issuer: String = "issuer with component framework"
  protected var id: String = "Gregorian-19700101"
  protected var encryptDataKeyName: String = "edt"
  protected var signatureVerifyKey: RSAPublicKey? = null
  protected var contentEccPrivateKey: PrivateKey? = null
  protected lateinit var objectMapper: ObjectMapper

  fun <S : Any, E : Any> decode(params: VerifierParam<S, E>): JwtToken<S, E> {
    return JWT.decode(params.token).let { decodedJwt ->
      JwtToken<S, E>().also { token ->
        // 解包加密段
        if (decodedJwt.claims.containsKey(this@JwtVerifier.encryptDataKeyName)) {
          log.trace("jwt 发现加密段 {}", this@JwtVerifier.encryptDataKeyName)
          token.decryptedData =
            decryptData(
              encData = decodedJwt.claims.getValue(this@JwtVerifier.encryptDataKeyName).asString(),
              eccPrivateKey = params.contentEncryptEccKey ?: this@JwtVerifier.contentEccPrivateKey,
              targetType = params.encryptDataTargetType!!.kotlin,
            )
        }
        // 解包 subject
        if (decodedJwt.claims.containsKey("sub")) {
          log.trace("发现sub加密段")
          token.subject = parseContent(decodedJwt.subject, params.subjectTargetType!!.kotlin)
        }
        token.expireDateTime = DateTimeConverter.instantToLocalDateTime(decodedJwt.expiresAt.toInstant())
        token.id = decodedJwt.id
        token.signatureAlgName = decodedJwt.algorithm
      }
    }
  }

  fun <S : Any, E : Any> verify(params: VerifierParam<S, E>): JwtToken<S, E>? {
    return JWT.require(Algorithm.RSA256(params.signatureKey ?: this.signatureVerifyKey))
      .withIssuer(params.issuer ?: this.issuer)
      .withJWTId(params.id ?: this.id)
      .acceptLeeway(0)
      .build()
      .let { verifier ->
        val decoded =
          try {
            decode(params)
          } catch (e: Exception) {
            log.warn("jwt 在 decode 时异常 ${e.message}")
            null
          }

        try {
          verifier.verify(params.token)
        } catch (e: Exception) {
          log.warn("jwt 在 verify 时发生异常 ${e.message}")
          parseExceptionHandle(e, decoded)
        }
        decoded
      }
  }

  private fun <T : Any> decryptData(encData: String, targetType: KClass<T>, eccPrivateKey: PrivateKey? = this.contentEccPrivateKey): T? {
    val content = eccPrivateKey.let { priKey -> CryptographicOperations.decryptByEccPrivateKey(priKey!!, encData) } ?: encData
    return parseContent(content, targetType)
  }

  private fun <T : Any> parseContent(json: String, classType: KClass<T>) =
    runCatching { objectMapper.readValue(json, classType.java) }.onFailure { log.warn("jwt 解析异常，可能没有序列化器", it) }.getOrNull()

  private fun <S : Any, E : Any> parseExceptionHandle(e: Exception, d: JwtToken<S, E>?): JwtToken<S, E>? {
    return when (e) {
      is com.auth0.jwt.exceptions.TokenExpiredException -> d.also { it?.isExpired = true }

      else -> null
    }
  }

  inner class Builder {
    fun build() = this@JwtVerifier

    fun serializer(mapper: ObjectMapper? = null): Builder {
      mapper?.let { objectMapper = it }
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
    @JvmStatic fun createVerifier(): Builder = JwtVerifier().Builder()
  }
}
