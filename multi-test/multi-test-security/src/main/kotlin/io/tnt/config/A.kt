package io.tnt.config

import com.truenine.component.core.encrypt.Keys
import java.io.File

fun main() {
  val aes = Keys.generateAesKeyToBase64()!!
  writeToFile("aes.key", aes)
  val rsa = Keys.generateRsaKeyPair()!!
  writeToFile("rsa_public.key", rsa.rsaPublicKeyBase64)
  writeToFile("rsa_private.key", rsa.rsaPrivateKeyBase64)
  val ecc = Keys.generateEccKeyPair()!!
  writeToFile("ecc_public.key", ecc.eccPublicKeyBase64)
  writeToFile("ecc_private.key", ecc.eccPrivateKeyBase64)
}

fun writeToFile(name: String, key: String) {
  val root = File(System.getProperty("user.dir"), ".generated")
  root.mkdirs()
  val f = File(root, name)
  f.createNewFile()
  f.writeBytes(key.toByteArray())
}
