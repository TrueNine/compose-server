package net.yan100.compose.oss

import net.yan100.compose.oss.StreamsMap
import java.io.InputStream

/**
 * 文件输入参数构造器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
interface InMap : StreamsMap {
  /**
   * @return 使用的输入流
   */
  override fun usedStream(): InputStream

  /**
   * @return 媒体类型
   */
  override fun mimeType(): String

  /**
   * @return 文件名
   */
  override fun fileName(): String

  /**
   * @return 目录名
   */
  override fun directoryName(): String
  override fun size(): Long
}
