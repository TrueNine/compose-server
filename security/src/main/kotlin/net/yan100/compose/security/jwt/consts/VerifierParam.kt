package net.yan100.compose.security.jwt.consts

import net.yan100.compose.core.encrypt.Keys
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey

data class VerifierParam<S : Any, E : Any>(
    val token: String,
    val subjectTargetType: Class<S>? = null,
    var encryptDataTargetType: Class<E>? = null,
    var signatureKey: RSAPublicKey? = null,
    var contentEncryptEccKey: PrivateKey? = null,
    var id: String? = null,
    var issuer: String? = null
) {
    fun isRequireDecrypted(): Boolean =
        this.encryptDataTargetType != null


    fun contentEncryptEccKeyFromBase64(base64Key: String) {
        this.contentEncryptEccKey = Keys.readEccPrivateKeyByBase64(base64Key)
    }

    fun signatureKeyFromBase64(base64Key: String) {
        this.signatureKey = Keys.readRsaPublicKeyByBase64(base64Key)!!
    }
}
