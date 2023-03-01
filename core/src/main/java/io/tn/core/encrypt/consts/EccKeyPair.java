package io.tn.core.encrypt.consts;

import io.tn.core.encrypt.base64.Base64Helper;
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
  private static final Base64Helper H = Base64Helper.defaultHelper();
  PublicKey pubKey;
  PrivateKey priKey;

  public PublicKey getPub() {
    return pubKey;
  }

  public PrivateKey getPri() {
    return priKey;
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
