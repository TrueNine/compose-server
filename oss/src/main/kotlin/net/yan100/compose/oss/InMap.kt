package net.yan100.compose.oss

import java.io.InputStream

/**
 * 文件输入参数构造器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
interface InMap : StreamsMap {
  override val usedStream: InputStream
  override val mediaType: String
  override val fName: String
  override val dirName: String
  override val size: Long
}
