package com.truenine.component.core.encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * rsa密钥对
 *
 * @author TrueNine
 * @since 2022-12-09
 */
@Data
public class RsaKeyPair {
  private static final Base64Helper H = new SimpleUtf8Base64();
  @JsonIgnore
  RSAPublicKey rsaPublicKey;

  @JsonIgnore
  RSAPrivateKey rsaPrivateKey;

  public String getRsaPublicKeyBase64() {
    return H.encode(rsaPublicKey.getEncoded());
  }

  public String getRsaPrivateKeyBase64() {
    return H.encode(rsaPrivateKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getRsaPublicKeyBase64Byte() {
    return H.encodeToByte(rsaPublicKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getRsaPrivateKeyBase64Byte() {
    return H.encodeToByte(rsaPrivateKey.getEncoded());
  }
}
