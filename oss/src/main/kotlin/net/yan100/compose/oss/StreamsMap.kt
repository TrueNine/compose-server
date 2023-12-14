package net.yan100.compose.oss

import java.io.Closeable

/**
 * 流图
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@JvmDefaultWithoutCompatibility
interface StreamsMap {
  /**
   * 使用过的流
   *
   * @return [Closeable]
   */
  val usedStream: Closeable?

  /**
   * mime类型
   *
   * @return [String]
   */
  val mediaType: String

  /**
   * 文件名称
   *
   * @return [String]
   */
  val fName: String

  /**
   * 目录名称
   *
   * @return [String]
   */
  val dirName: String

  /**
   * 对外暴露的 url
   */
  val exposeBaseUrl: String

  /**
   * 大小
   *
   * @return long
   */
  val size: Long

  /**
   * 字符串表示的 size
   *
   * @return [String]
   */
  val sizeStr: String get() = size.toString()


  /**
   * 路径
   *
   * @return [String]
   */
  val path: String get() = "$dirName/$fName"


}
