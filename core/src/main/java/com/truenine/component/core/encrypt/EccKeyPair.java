package com.truenine.component.core.encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  PublicKey eccPublicKey;
  @JsonIgnore
  PrivateKey eccPrivateKey;

  public String getEccPublicKeyBase64() {
    return h.encode(eccPublicKey.getEncoded());
  }

  public String getEccPrivateKeyBase64() {
    return h.encode(eccPrivateKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getEccPublicKeyBase64Byte() {
    return h.encodeToByte(eccPublicKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getEccPrivateKeyBase64Byte() {
    return h.encodeToByte(eccPrivateKey.getEncoded());
  }
}
