package net.yan100.compose.core.encrypt

import com.fasterxml.jackson.annotation.JsonIgnore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * rsa密钥对
 *
 * @author TrueNine
 * @since 2022-12-09
 */
data class RsaKeyPair(
  @JsonIgnore
  var rsaPublicKey: RSAPublicKey? = null,

  @JsonIgnore
  var rsaPrivateKey: RSAPrivateKey? = null
) {
  @get:JsonIgnore
  val rsaPublicKeyBase64: String
    get() = Base64Helper.encode(rsaPublicKey!!.encoded)

  @get:JsonIgnore
  val rsaPrivateKeyBase64: String
    get() = Base64Helper.encode(rsaPrivateKey!!.encoded)

  @get:JsonIgnore
  val rsaPublicKeyBase64Byte: ByteArray
    get() = Base64Helper.encodeToByte(rsaPublicKey!!.encoded)

  @get:JsonIgnore
  val rsaPrivateKeyBase64Byte: ByteArray
    get() = Base64Helper.encodeToByte(rsaPrivateKey!!.encoded)
}
