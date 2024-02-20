/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.oss

import java.io.OutputStream

/**
 * 文件输出参数构造器
 *
 * @author TuueNine
 * @since 2023-02-20
 */
interface OutMap : StreamsMap {
  /** @return 使用的输出流 */
  override val usedStream: OutputStream

  /** @return 文件媒体类型 */
  override val mediaType: String

  /** @return 文件名 */
  override val fName: String

  /** @return 目录名 */
  override val dirName: String

  /** @return 文件大小 */
  override val size: Long

  override val exposeBaseUrl: String
}
