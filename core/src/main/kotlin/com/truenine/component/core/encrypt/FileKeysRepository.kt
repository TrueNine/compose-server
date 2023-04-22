package com.truenine.component.core.encrypt

import java.nio.file.Files
import java.nio.file.Path
import javax.crypto.spec.SecretKeySpec

class FileKeysRepository(
  private val keyDest: String = "security"
) : KeysRepository {
  private var rsaKeyPair: RsaKeyPair? = null
  private var eccKeyPair: EccKeyPair? = null
  private var aesKey: SecretKeySpec? = null

  init {
    aesKey = Keys.readAesKeyByBase64(read("aes.key"))
    rsaKeyPair = Keys.readRsaKeyPair(read("rsa_public.key"), read("rsa_private.key"))!!
    eccKeyPair = Keys.readEccKeyPair(read("ecc_public.key"), read("ecc_private.key"))!!
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

  private fun read(name: String) =
    Files.readString(Path.of(keyDest, name))
}
