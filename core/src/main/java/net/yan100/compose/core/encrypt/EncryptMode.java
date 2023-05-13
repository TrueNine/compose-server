package net.yan100.compose.core.encrypt;

import javax.crypto.Cipher;

/**
 * 加密模式
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public enum EncryptMode {
  /**
   * 加密
   */
  ENCRYPT(Cipher.ENCRYPT_MODE),
  /**
   * 未定义
   */
  UNDEFINED(-1024),
  /**
   * 解密
   */
  DECRYPT(Cipher.DECRYPT_MODE);

  private final int i;

  EncryptMode(int i) {
    this.i = i;
  }

  public int mode() {
    return this.i;
  }
}
