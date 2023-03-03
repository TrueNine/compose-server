package com.truenine.component.core.encrypt.consts;

import com.truenine.component.core.encrypt.base64.Base64Helper;
import com.truenine.component.core.encrypt.base64.SimpleUtf8Base64;
import lombok.Data;
import lombok.experimental.Accessors;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * rsa密钥对
 *
 * @author TrueNine
 * @since 2022-12-09
 */
@Data
@Accessors(chain = true)
public class RsaKeyPair {
  private static final Base64Helper H = new SimpleUtf8Base64();
  RSAPublicKey rsaPublicKey;
  RSAPrivateKey rsaPrivateKey;

  public String getRsaPublicKeyBase64() {
    return H.encode(rsaPublicKey.getEncoded());
  }

  public String getRsaPrivateKeyBase64() {
    return H.encode(rsaPrivateKey.getEncoded());
  }

  public byte[] getRsaPublicKeyBase64Byte() {
    return H.encodeToByte(rsaPublicKey.getEncoded());
  }

  public byte[] getRsaPrivateKeyBase64Byte() {
    return H.encodeToByte(rsaPrivateKey.getEncoded());
  }
}
