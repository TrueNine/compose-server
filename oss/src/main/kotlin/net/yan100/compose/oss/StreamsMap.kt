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
   * 使用流
   *
   * @return [Closeable]
   */
  fun usedStream(): Closeable?

  /**
   * mime类型
   *
   * @return [String]
   */
  fun mimeType(): String

  /**
   * 文件名称
   *
   * @return [String]
   */
  fun fileName(): String

  /**
   * 目录名称
   *
   * @return [String]
   */
  fun directoryName(): String

  /**
   * 大小
   *
   * @return long
   */
  fun size(): Long

  /**
   * 字符串表示的 size
   *
   * @return [String]
   */
  fun sSize(): String? {
    return java.lang.Long.toString(size())
  }

  /**
   * 路径
   *
   * @return [String]
   */
  fun path(): String? {
    return directoryName() + "/" + fileName()
  }
}
