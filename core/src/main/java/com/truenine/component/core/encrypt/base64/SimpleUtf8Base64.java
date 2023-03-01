package com.truenine.component.core.encrypt.base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 简单use utf8 base64
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class SimpleUtf8Base64 implements Base64Helper {
  Charset utf8 = StandardCharsets.UTF_8;

  @Override
  public String encode(String content) {
    return encode(content, utf8);
  }

  @Override
  public String decode(String base64) {
    return decode(base64, utf8);
  }
}
