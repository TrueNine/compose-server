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
package net.yan100.compose.oss.minio

import io.minio.GetObjectResponse
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import net.yan100.compose.oss.InMap
import net.yan100.compose.oss.OutMap
import java.io.InputStream
import java.io.OutputStream

/**
 * OSS 抽象与 minio 具体实现的隔离继承层
 *
 * @author TrueNine
 * @since 2023-02-20
 */
open class MinioClientAdaptor
protected constructor(
  protected val client: MinioClient,
  protected val exBaseUrl: String = "http://localhost:9000"
) : MinioClientOperator(client) {
  open fun ins(resp: ObjectWriteResponse, stream: InputStream): InMap {
    return object : InMap {
      override val usedStream
        get() = stream

      override val mediaType
        get() = headerContentType(resp.headers())!!

      override val fName
        get() = resp.`object`()

      override val dirName
        get() = resp.bucket()

      override val size
        get() = headerSize(resp.headers())!!

      override val exposeBaseUrl: String
        get() = exBaseUrl
    }
  }

  open fun outs(resp: GetObjectResponse, stream: OutputStream): OutMap {
    return object : OutMap {
      override val usedStream
        get() = stream

      override val mediaType
        get() = headerContentType(resp.headers())!!

      override val fName
        get() = resp.`object`()

      override val dirName
        get() = resp.bucket()

      override val size
        get() = headerSize(resp.headers())!!

      override val exposeBaseUrl: String
        get() = exBaseUrl
    }
  }
}
