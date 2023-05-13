package net.yan100.compose.core.encrypt;

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
  @JsonIgnore
  RSAPublicKey rsaPublicKey;

  @JsonIgnore
  RSAPrivateKey rsaPrivateKey;

  public String getRsaPublicKeyBase64() {
    return Base64Helper.encode(rsaPublicKey.getEncoded());
  }

  public String getRsaPrivateKeyBase64() {
    return Base64Helper.encode(rsaPrivateKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getRsaPublicKeyBase64Byte() {
    return Base64Helper.encodeToByte(rsaPublicKey.getEncoded());
  }

  @JsonIgnore
  public byte[] getRsaPrivateKeyBase64Byte() {
    return Base64Helper.encodeToByte(rsaPrivateKey.getEncoded());
  }
}
