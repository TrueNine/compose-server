package net.yan100.compose.core.encrypt

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.spec.SecretKeySpec


interface IKeysRepo {
    fun basicRsaKeyPair(): RsaKeyPair? = null
    fun basicEccKeyPair(): EccKeyPair? = null
    fun basicAesKey(): SecretKeySpec? = null

    fun findRsaKeyPairByName(publicKeyName: String, privateKeyName: String): RsaKeyPair? {
        return RsaKeyPair().also {
            it.rsaPublicKey = findRsaPublicKeyByName(publicKeyName)!!
            it.rsaPrivateKey = findRsaPrivetKeyByName(privateKeyName)!!
        }
    }


    fun findEccKeyPairByName(publicKeyName: String, privateKeyName: String): EccKeyPair? {
        return EccKeyPair().also {
            it.eccPublicKey = findEccPublicKeyByName(publicKeyName)!!
            it.eccPrivateKey = findEccPrivateKeyByName(privateKeyName)!!
        }
    }

    fun findAesSecretByName(name: String): SecretKeySpec? = null

    fun findEccPublicKeyByName(name: String): PublicKey? = null
    fun findRsaPublicKeyByName(name: String): RSAPublicKey? = null

    fun findEccPrivateKeyByName(name: String): PrivateKey? = null
    fun findRsaPrivetKeyByName(name: String): RSAPrivateKey? = null

    fun jwtSignatureIssuerRsaKeyPair(): RsaKeyPair? {
        return basicRsaKeyPair()
    }

    fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? {
        return basicRsaKeyPair()?.rsaPublicKey
    }

    fun jwtEncryptDataIssuerEccKeyPair(): EccKeyPair? {
        return basicEccKeyPair()
    }

    fun jwtEncryptDataVerifierKey(): PrivateKey? {
        return basicEccKeyPair()?.eccPrivateKey
    }

    fun databaseEncryptAesSecret(): SecretKeySpec? {
        return basicAesKey()
    }
}
