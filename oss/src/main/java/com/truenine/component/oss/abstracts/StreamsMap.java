package com.truenine.component.oss.abstracts;

import java.io.Closeable;

/**
 * 流图
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface StreamsMap {
  /**
   * 使用流
   *
   * @return {@link Closeable}
   */
  Closeable usedStream();

  /**
   * mime类型
   *
   * @return {@link String}
   */
  String mimeType();

  /**
   * 文件名称
   *
   * @return {@link String}
   */
  String fileName();

  /**
   * 目录名称
   *
   * @return {@link String}
   */
  String directoryName();

  /**
   * 大小
   *
   * @return long
   */
  long size();

  /**
   * 字符串表示的 size
   *
   * @return {@link String}
   */
  default String sSize() {
    return Long.toString(size());
  }

  /**
   * 路径
   *
   * @return {@link String}
   */
  default String path() {
    return directoryName() + "/" + fileName();
  }
}
