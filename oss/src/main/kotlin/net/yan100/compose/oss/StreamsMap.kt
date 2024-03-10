/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
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

  /** 对外暴露的 url */
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
  val sizeStr: String
    get() = size.toString()

  /**
   * 路径
   *
   * @return [String]
   */
  val path: String
    get() = "$dirName/$fName"
}
