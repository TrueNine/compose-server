package net.yan100.compose.security.autoconfig

import net.yan100.compose.core.encrypt.*
import net.yan100.compose.core.lang.base64Decode
import org.springframework.core.io.ClassPathResource
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey


class FileKeyRepo(
  private val baseDir: String = "keys"
) : IKeysRepo {
  private fun isPem(content: String): Boolean {
    return content.startsWith(PemFormat.BEGIN_START)
  }

  private fun load(path: String): String {
    return BufferedReader(InputStreamReader(BufferedInputStream(ClassPathResource("$baseDir/$path").inputStream))).use { it.readText() }
  }

  private fun readBase64(name: String): String {
    val a = load(name)
    return if (isPem(a)) PemFormat(a).content
    else a.base64Decode()
  }

  override fun findRsaPrivetKeyByName(name: String): RSAPrivateKey? {
    return Keys.readRsaPrivateKeyByBase64(readBase64(name))
  }

  override fun findRsaPublicKeyByName(name: String): RSAPublicKey? {
    return Keys.readRsaPublicKeyByBase64(readBase64(name))
  }

  override fun findEccPrivateKeyByName(name: String): PrivateKey? {
    return Keys.readEccPrivateKeyByBase64(readBase64(name))
  }

  override fun findEccPublicKeyByName(name: String): PublicKey? {
    return Keys.readEccPublicKeyByBase64(readBase64(name))
  }

  override fun jwtEncryptDataIssuerEccKeyPair(): EccKeyPair? {
    return findEccKeyPairByName("jwt_issuer_enc.key", "jwt_verifier_enc.pem")
  }

  override fun jwtEncryptDataVerifierKey(): PrivateKey? {
    return findEccPrivateKeyByName("jwt_verifier_enc.pem")
  }

  override fun jwtSignatureIssuerRsaKeyPair(): RsaKeyPair? {
    return findRsaKeyPairByName("jwt_verifier.key","jwt_issuer.pem" )
  }

  override fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? {
    return findRsaPublicKeyByName("jwt_verifier.key")
  }
}
