package net.yan100.compose.oss

import java.io.OutputStream

/**
 * 文件输出参数构造器
 *
 * @author TuueNine
 * @since 2023-02-20
 */
interface OutMap {
  /** @return 使用的输出流 */
  val usedStream: OutputStream

  /** @return 文件媒体类型 */
  val mediaType: String

  /** @return 文件名 */
  val objectName: String

  /** @return 目录名 */
  val bucketName: String

  /** @return 文件大小 */
  val size: Long

  val exposeBaseUrl: String
}
