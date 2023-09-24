package net.yan100.compose.core.http;

import com.fasterxml.jackson.annotation.JsonValue;
import net.yan100.compose.core.lang.Str;
import net.yan100.compose.core.lang.StringTyping;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * mime类型
 *
 * @author TrueNine
 * @since 2022-11-03
 */
public enum MediaTypes implements StringTyping {
  /**
   * 二进制
   */
  BINARY("application/octet-stream"),
  /**
   * 二进制 exe
   */
  EXE("application/octet-stream"),
  /**
   * text
   */
  TEXT("text/plain"),
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
   *  server sent event
   */
  SSE("text/event-stream"),
  /**
   * gzip
   */
  GZIP("application/x-gzip");
  private final String media;

  MediaTypes(String value) {
    this.media = value;
  }

  /**
   * 查询一个字符串，如果为没有则为二进制
   *
   * @param type 类型
   * @return {@link MediaTypes}
   */
  @Deprecated
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

  @Nullable
  public static MediaTypes findVal(String media) {
    return Arrays.stream(MediaTypes.values())
      .filter(v -> Objects.equals(v.getValue(), media))
      .findFirst().orElse(null);
  }

  @Deprecated
  public String media() {
    return this.media;
  }

  @Override
  @JsonValue
  public String getValue() {
    return this.media();
  }
}
