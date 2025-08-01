package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.security.crypto.domain.IEccExtKeyPair
import io.github.truenine.composeserver.security.crypto.domain.IKeysRepo
import io.github.truenine.composeserver.security.crypto.domain.IRsaExtKeyPair
import io.github.truenine.composeserver.slf4j
import javax.crypto.spec.SecretKeySpec

class FileKeysRepoImpl(
  private val keyDest: String = "security",
  eccKeyPairPaths: Pair<String, String> = "ecc_public.key" to "ecc_private.key",
  rsaKeyPairPaths: Pair<String, String> = "rsa_public.key" to "rsa_private.key",
  aesPaths: String = "aes.key",
) : IKeysRepo {

  private var rsaKeyPair: IRsaExtKeyPair? = null
  private var eccKeyPair: IEccExtKeyPair? = null
  private var aesKey: SecretKeySpec? = null

  init {
    aesKey = CryptographicKeyManager.readAesKeyByBase64(read(aesPaths))
    rsaKeyPair = CryptographicKeyManager.readRsaKeyPair(read(rsaKeyPairPaths.first), read(rsaKeyPairPaths.second))
    eccKeyPair = CryptographicKeyManager.readEccKeyPair(read(eccKeyPairPaths.first), read(eccKeyPairPaths.second))
  }

  override fun basicEccKeyPair(): IEccExtKeyPair? {
    return eccKeyPair
  }

  override fun basicRsaKeyPair(): IRsaExtKeyPair? {
    return rsaKeyPair
  }

  override fun basicAesKey(): SecretKeySpec? {
    return this.aesKey
  }

  private fun read(name: String): String {
    val text = javaClass.classLoader.getResource("${this.keyDest}/$name")!!.readText()
    log.trace("text = {}", text)
    return text
  }

  companion object {
    @JvmStatic private val log = slf4j<FileKeysRepoImpl>()
  }
}
