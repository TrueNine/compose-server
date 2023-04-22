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
  @JsonIgnore
  PublicKey eccPublicKey;
  @JsonIgnore
  PrivateKey eccPrivateKey;

  public String getEccPublicKeyBase64() {
    return Base64Helper.encode(eccPublicKey.getEncoded());
  }

  public String getEccPrivateKeyBase64() {
    return Base64Helper.encode(eccPrivateKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getEccPublicKeyBase64Byte() {
    return Base64Helper.encodeToByte(eccPublicKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getEccPrivateKeyBase64Byte() {
    return Base64Helper.encodeToByte(eccPrivateKey.getEncoded());
  }
}
