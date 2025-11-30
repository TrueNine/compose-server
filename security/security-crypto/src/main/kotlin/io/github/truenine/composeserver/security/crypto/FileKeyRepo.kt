package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.security.crypto.domain.*
import org.springframework.core.io.ClassPathResource
import java.io.*
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class FileKeyRepo(private val baseDir: String = "keys") : IKeysRepo {
  private fun isPem(content: String): Boolean {
    return content.startsWith(PemFormat.BEGIN_PREFIX)
  }

  private fun load(path: String): String {
    return BufferedReader(InputStreamReader(BufferedInputStream(ClassPathResource("$baseDir/$path").inputStream))).use { it.readText() }
  }

  private fun readBase64(name: String): String {
    val a = load(name)
    return if (isPem(a)) PemFormat.parse(a).content else a.base64Decode()
  }

  override fun findRsaPrivateKeyByName(name: String): RSAPrivateKey? {
    return CryptographicKeyManager.readRsaPrivateKeyByBase64(readBase64(name))
  }

  override fun findRsaPublicKeyByName(name: String): RSAPublicKey? {
    return CryptographicKeyManager.readRsaPublicKeyByBase64(readBase64(name))
  }

  override fun findEccPrivateKeyByName(name: String): PrivateKey? {
    return CryptographicKeyManager.readEccPrivateKeyByBase64(readBase64(name))
  }

  override fun findEccPublicKeyByName(name: String): PublicKey? {
    return CryptographicKeyManager.readEccPublicKeyByBase64(readBase64(name))
  }

  override fun jwtEncryptDataIssuerEccKeyPair(): IEccExtKeyPair? {
    return findEccKeyPairByName("jwt_issuer_enc.key", "jwt_verifier_enc.pem")
  }

  override fun jwtEncryptDataVerifierKey(): PrivateKey? {
    return findEccPrivateKeyByName("jwt_verifier_enc.pem")
  }

  override fun jwtSignatureIssuerRsaKeyPair(): IRsaExtKeyPair? {
    return findRsaKeyPairByName("jwt_verifier.key", "jwt_issuer.pem")
  }

  override fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? {
    return findRsaPublicKeyByName("jwt_verifier.key")
  }
}
