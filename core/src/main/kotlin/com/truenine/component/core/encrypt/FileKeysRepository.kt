package com.truenine.component.core.encrypt

import com.truenine.component.core.lang.slf4j
import javax.crypto.spec.SecretKeySpec

class FileKeysRepository(
  private val keyDest: String = "security",
  eccKeyPairPaths: Pair<String, String> = "ecc_public.key" to "ecc_private.key",
  rsaKeyPairPaths: Pair<String, String> = "rsa_public.key" to "rsa_private.key",
  aesPaths: String = "aes.key"
) : KeysRepository {
  private val log = slf4j(this::class)
  private var rsaKeyPair: RsaKeyPair? = null
  private var eccKeyPair: EccKeyPair? = null
  private var aesKey: SecretKeySpec? = null

  init {
    aesKey = Keys.readAesKeyByBase64(read(aesPaths))
    rsaKeyPair = Keys.readRsaKeyPair(read(rsaKeyPairPaths.first), read(rsaKeyPairPaths.second))!!
    eccKeyPair = Keys.readEccKeyPair(read(eccKeyPairPaths.first), read(eccKeyPairPaths.second))!!
  }

  override fun basicEccKeyPair(): EccKeyPair? {
    return eccKeyPair
  }

  override fun basicRsaKeyPair(): RsaKeyPair? {
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

}
