package net.yan100.compose.oss

import java.io.OutputStream

/**
 * 文件输出参数构造器
 *
 * @author TuueNine
 * @since 2023-02-20
 */
interface OutMap : StreamsMap {
  /**
   * @return 使用的输出流
   */
  override val usedStream: OutputStream

  /**
   * @return 文件媒体类型
   */
  override val mediaType: String

  /**
   * @return 文件名
   */
  override val fName: String

  /**
   * @return 目录名
   */
  override val dirName: String

  /**
   * @return 文件大小
   */
  override val size: Long

  override val exposeBaseUrl: String
}
