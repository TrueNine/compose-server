package net.yan100.compose.security.crypto

import javax.crypto.spec.SecretKeySpec
import net.yan100.compose.security.crypto.domain.IEccExtKeyPair
import net.yan100.compose.security.crypto.domain.IKeysRepo
import net.yan100.compose.security.crypto.domain.IRsaExtKeyPair
import net.yan100.compose.slf4j

private val log = slf4j<FileKeysRepoImpl>()

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
    aesKey = Keys.readAesKeyByBase64(read(aesPaths))
    rsaKeyPair =
      Keys.readRsaKeyPair(
        read(rsaKeyPairPaths.first),
        read(rsaKeyPairPaths.second),
      )
    eccKeyPair =
      Keys.readEccKeyPair(
        read(eccKeyPairPaths.first),
        read(eccKeyPairPaths.second),
      )
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
    val text =
      javaClass.classLoader.getResource("${this.keyDest}/$name")!!.readText()
    log.trace("text = {}", text)
    return text
  }
}
