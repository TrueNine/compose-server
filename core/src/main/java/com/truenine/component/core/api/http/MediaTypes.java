package com.truenine.component.core.api.http;

import com.truenine.component.core.lang.Str;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * mime类型
 *
 * @author TrueNine
 * @since 2022-11-03
 */
@Slf4j
public enum MediaTypes {
  /**
   * 二进制
   */
  BINARY("application/octet-stream"),
  /**
   * 二进制 exe
   */
  EXE("application/octet-stream"),
  /**
   * json
   */
  JSON("application/json"),
  /**
   * html
   */
  HTML("text/html"),
  /**
   * xml
   */
  XML("text/xml"),
  /**
   * png图像
   */
  PNG("image/png"),
  /**
   * mp3
   */
  MP3("audio/mpeg"),
  /**
   * wav
   */
  WAV("audio/x-wav"),
  /**
   * m3u
   */
  M3U("audio/x-mpegurl"),
  /**
   * m4a
   */
  M4A("audio/x-m4a"),
  /**
   * ogg
   */
  OGG("audio/ogg"),
  /**
   * mp4
   */
  MP4("video/mp4"),
  /**
   * webm
   */
  WEBM("video/webm"),
  /**
   * js
   */
  JAVASCRIPT("text/javascript"),
  /**
   * webp图像
   */
  WEBP("image/webp"),
  /**
   * java jar
   */
  JAR("application/java-archive"),
  /**
   * pdf
   */
  PDF("application/pdf"),
  /**
   * gif
   */
  GIF("image/gif"),
  /**
   * zip
   */
  ZIP("application/zip"),
  /**
   * css
   */
  CSS("text/css"),
  /**
   * gzip
   */
  GZIP("application/x-gzip");

  private final String[] VALUE;

  MediaTypes(String... value) {
    this.VALUE = value;
  }

  /**
   * 查询一个字符串，如果为没有则为二进制
   *
   * @param type 类型
   * @return {@link MediaTypes}
   */
  public static MediaTypes of(String type) {
    if (Str.nonText(type)) {
      return MediaTypes.BINARY;
    }
    for (MediaTypes value : MediaTypes.values()) {
      if (value.media().equals(type)) {
        return value;
      }
    }

    return MediaTypes.BINARY;
  }

  public String media() {
    return this.VALUE[0];
  }

  public Set<String> vals() {
    return Arrays.stream(this.VALUE).collect(Collectors.toSet());
  }
}
