package net.yan100.compose.oss.common

import java.io.InputStream

/**
 * 文件输入参数构造器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
interface InMap {
  val usedStream: InputStream
  val mediaType: String
  val objectName: String
  val bucketName: String
  val size: Long
  val exposeBaseUrl: String
}
