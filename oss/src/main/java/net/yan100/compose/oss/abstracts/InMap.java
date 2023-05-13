package net.yan100.compose.oss.abstracts;

import java.io.InputStream;


/**
 * 文件输入参数构造器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
public interface InMap extends StreamsMap {
  /**
   * @return 使用的输入流
   */
  InputStream usedStream();

  /**
   * @return 媒体类型
   */
  String mimeType();

  /**
   * @return 文件名
   */
  String fileName();

  /**
   * @return 目录名
   */
  String directoryName();

  long size();
}
