package com.truenine.component.core.encrypt;

import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * ecc 密钥对
 *
 * @author TrueNine
 * @since 2022-12-15
 */
@Data
public class EccKeyPair {
  private static final Base64Helper h = Base64Helper.defaultHelper();
  PublicKey eccPublicKey;
  PrivateKey eccPrivateKey;

  public String getEccPublicKeyBase64() {
    return h.encode(eccPublicKey.getEncoded());
  }

  public String getEccPrivateKeyBase64() {
    return h.encode(eccPrivateKey.getEncoded());
  }

  public byte[] getEccPublicKeyBase64Byte() {
    return h.encodeToByte(eccPublicKey.getEncoded());
  }

  public byte[] getEccPrivateKeyBase64Byte() {
    return h.encodeToByte(eccPrivateKey.getEncoded());
  }
}
