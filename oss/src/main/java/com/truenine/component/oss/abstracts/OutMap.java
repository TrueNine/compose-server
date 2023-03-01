package com.truenine.component.oss.abstracts;

import java.io.OutputStream;

/**
 * 文件输出参数构造器
 *
 * @author TuueNine
 * @since 2023-02-20
 */
public interface OutMap extends StreamsMap {
  /**
   * @return 使用的输出流
   */
  OutputStream usedStream();

  /**
   * @return 文件媒体类型
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

  /**
   * @return 文件大小
   */
  long size();


}
