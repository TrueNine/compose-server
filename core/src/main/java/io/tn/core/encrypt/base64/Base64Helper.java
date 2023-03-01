package io.tn.core.encrypt.base64;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * base64 工具类
 *
 * @author TrueNine
 * @since 2023-02-20
 */
public interface Base64Helper {
  Base64.Encoder JAVA_INTERNAL_ENCODER = Base64.getEncoder();
  Base64.Decoder JAVA_INTERNAL_DECODER = Base64.getDecoder();

  /**
   * 默认 base64 工具类实现
   *
   * @return {@link Base64Helper}
   */
  static Base64Helper defaultHelper() {
    return new SimpleUtf8Base64();
  }

  /**
   * 编码
   *
   * @param content 内容
   * @return {@link String}
   */
  default String encode(byte[] content) {
    return JAVA_INTERNAL_ENCODER.encodeToString(content);
  }

  /**
   * 以字节编码
   *
   * @param content 内容
   * @return {@link byte[]}
   */
  default byte[] encodeToByte(byte[] content) {
    return JAVA_INTERNAL_ENCODER.encode(content);
  }

  /**
   * 编码
   *
   * @param content 内容
   * @param charset 字符集
   * @return {@link String}
   */
  default String encode(String content, Charset charset) {
    return encode(content.getBytes(charset));
  }

  /**
   * 编码
   *
   * @param content 内容
   * @return {@link String}
   */
  String encode(String content);

  /**
   * 解码字节
   *
   * @param base64 base64
   * @return {@link byte[]}
   */
  default byte[] decodeToByte(String base64) {
    return JAVA_INTERNAL_DECODER.decode(base64);
  }

  /**
   * 解码字节
   *
   * @param base64 base64
   * @return {@link byte[]}
   */
  default byte[] decodeToByte(byte[] base64) {
    return JAVA_INTERNAL_DECODER.decode(base64);
  }

  /**
   * 解码
   *
   * @param base64  base64
   * @param charset 字符集
   * @return {@link String}
   */
  default String decode(byte[] base64, Charset charset) {
    return new String(JAVA_INTERNAL_DECODER.decode(base64), charset);
  }

  /**
   * 解码
   *
   * @param base64  base64
   * @param charset 字符集
   * @return {@link String}
   */
  default String decode(String base64, Charset charset) {
    return decode(base64.getBytes(charset), charset);
  }

  /**
   * 解码
   *
   * @param base64 base64
   * @return {@link String}
   */
  String decode(String base64);
}
