package com.truenine.component.core.encrypt

import com.truenine.component.core.encrypt.consts.EccKeyPair
import com.truenine.component.core.encrypt.consts.RsaKeyPair
import java.nio.file.Files
import java.nio.file.Path
import javax.crypto.spec.SecretKeySpec

class FileKeysRepository(
  private val keyDest: String
) : KeysRepository {
  private var rsaKeyPair: RsaKeyPair? = null
  private var eccKeyPair: EccKeyPair? = null
  private var aesKey: SecretKeySpec? = null

  override fun basicAesKey(): SecretKeySpec? {
    aesKey = Keys.readAesKeyByBase64(read("aes.key"))
    this.rsaKeyPair =
      Keys.readRsaKeyPair(read("rsa_pub.key"), read("rsa_pri.key"))!!
    val rsaPri = rsaKeyPair!!.rsaPrivateKey
    val rsaPub = rsaKeyPair!!.rsaPrivateKey
    this.eccKeyPair = Keys.readEccKeyPair(
      read("ecc_pub.key"),
      read("ecc_pri.key")
    )!!
    val eccPri = eccKeyPair!!.eccPrivateKey
    val eccPub = eccKeyPair!!.eccPrivateKey

    return super.basicAesKey()
  }

  private fun read(name: String) =
    Files.readString(Path.of(keyDest, name))
}
