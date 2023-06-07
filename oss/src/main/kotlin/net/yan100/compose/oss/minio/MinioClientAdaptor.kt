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
  protected val client: MinioClient
) : MinioClientOperator(client) {

  open fun ins(resp: ObjectWriteResponse, stream: InputStream): InMap {
    return object : InMap {
      override fun usedStream(): InputStream {
        return stream
      }

      override fun mimeType(): String {
        return headerContentType(resp.headers())!!
      }

      override fun fileName(): String {
        return resp.`object`()
      }

      override fun directoryName(): String {
        return resp.bucket()
      }

      override fun size(): Long {
        return headerSize(resp.headers())!!
      }
    }
  }

  open fun outs(resp: GetObjectResponse, stream: OutputStream): OutMap {
    return object : OutMap {
      override fun usedStream(): OutputStream {
        return stream
      }

      override fun mimeType(): String {
        return headerContentType(resp.headers())!!
      }

      override fun fileName(): String {
        return resp.`object`()
      }

      override fun directoryName(): String {
        return resp.bucket()
      }

      override fun size(): Long {
        return headerSize(resp.headers())!!
      }
    }
  }


}
