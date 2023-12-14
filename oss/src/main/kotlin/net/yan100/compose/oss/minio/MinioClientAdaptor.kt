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
open class MinioClientAdaptor protected constructor(
  protected val client: MinioClient,
  protected val exBaseUrl: String = "http://localhost:9000"
) : MinioClientOperator(client) {
  open fun ins(resp: ObjectWriteResponse, stream: InputStream): InMap {
    return object : InMap {
      override val usedStream get() = stream
      override val mediaType get() = headerContentType(resp.headers())!!
      override val fName get() = resp.`object`()
      override val dirName get() = resp.bucket()
      override val size get() = headerSize(resp.headers())!!
      override val exposeBaseUrl: String get() = exBaseUrl
    }
  }

  open fun outs(resp: GetObjectResponse, stream: OutputStream): OutMap {
    return object : OutMap {
      override val usedStream get() = stream
      override val mediaType get() = headerContentType(resp.headers())!!
      override val fName get() = resp.`object`()
      override val dirName get() = resp.bucket()
      override val size get() = headerSize(resp.headers())!!
      override val exposeBaseUrl: String get() = exBaseUrl
    }
  }
}
