package net.yan100.compose.core.encrypt

import com.fasterxml.jackson.annotation.JsonIgnore
import java.security.PrivateKey
import java.security.PublicKey

/**
 * ecc 密钥对
 *
 * @author TrueNine
 * @since 2022-12-15
 */
class EccKeyPair {
    @JsonIgnore
    var eccPublicKey: PublicKey? = null

    @JsonIgnore
    var eccPrivateKey: PrivateKey? = null
    val eccPublicKeyBase64: String
        get() = Base64Helper.encode(eccPublicKey!!.encoded)
    val eccPrivateKeyBase64: String
        get() = Base64Helper.encode(eccPrivateKey!!.encoded)

    @get:JsonIgnore
    val eccPublicKeyBase64Byte: ByteArray
        get() = Base64Helper.encodeToByte(eccPublicKey!!.encoded)

    @get:JsonIgnore
    val eccPrivateKeyBase64Byte: ByteArray
        get() = Base64Helper.encodeToByte(eccPrivateKey!!.encoded)
}
