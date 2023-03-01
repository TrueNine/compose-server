package io.tn.core.encrypt.consts;

import io.tn.core.encrypt.base64.Base64Helper;
import io.tn.core.encrypt.base64.SimpleUtf8Base64;
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
  RSAPublicKey pubKey;
  RSAPrivateKey priKey;

  public RSAPrivateKey getPri() {
    return this.priKey;
  }

  public RSAPublicKey getPub() {
    return this.pubKey;
  }

  public String getPubKey() {
    return H.encode(pubKey.getEncoded());
  }

  public String getPriKey() {
    return H.encode(priKey.getEncoded());
  }

  public byte[] getPubKeyByte() {
    return H.encodeToByte(pubKey.getEncoded());
  }

  public byte[] getPriKeyByte() {
    return H.encodeToByte(priKey.getEncoded());
  }
}
