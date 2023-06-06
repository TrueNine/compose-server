package net.yan100.compose.oss.minio

import io.minio.*
import io.minio.messages.Bucket
import io.minio.messages.Item
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.amazon.S3PolicyCreator
import okhttp3.Headers
import java.io.InputStream
import java.io.OutputStream
import java.util.stream.Collectors

/**
 * minio 基础层
 *
 * @author TrueNine
 * @since 2022-12-29
 */
open class MinioClientOperator
protected constructor(
  private val client: MinioClient
) {
  open fun headerContentType(headers: Headers): String {
    return headers[net.yan100.compose.core.http.Headers.CONTENT_TYPE]!!
  }

  open fun headerSizeStr(headers: Headers): String? {
    return headers[net.yan100.compose.core.http.Headers.CONTENT_LENGTH]
  }

  open fun headerSize(headers: Headers): Long {
    return headerSizeStr(headers)!!.toLong()
  }

  open fun getObject(fileInfo: FileArgs, stream: OutputStream): GetObjectResponse {
    return client.getObject(GetObjectArgs.builder().bucket(fileInfo.dir).`object`(fileInfo.fileName).build())
  }

  open fun publicBucket(bucketName: String) {
    client.setBucketPolicy(
      SetBucketPolicyArgs.builder()
        .bucket(bucketName)
        .config(S3PolicyCreator.publicBucketAndReadOnly(bucketName).json())
        .build()
    )
  }

  open fun putObject(fileInfo: FileArgs, stream: InputStream): ObjectWriteResponse {
    return client.putObject(
      PutObjectArgs.builder().bucket(fileInfo.dir).`object`(fileInfo.fileName).contentType(fileInfo.mimeType)
        .stream(stream, fileInfo.size, -1).build()
    )
  }

  open fun listFiles(dir: String): List<String> {
    val items = client.listObjects(
      ListObjectsArgs.builder()
        .bucket(dir)
        .build()
    )
    val results: MutableList<String> = ArrayList()
    for (item in items) {
      results += item.get().objectName()
    }
    return results
  }

  open fun listDir(): List<String> {
    return client.listBuckets().stream().map { obj: Bucket -> obj.name() }
      .collect(Collectors.toList())
  }

  open fun createBucket(dirName: String) {
    client.makeBucket(
      MakeBucketArgs.builder()
        .bucket(dirName)
        .build()
    )
  }

  open val buckets: List<Bucket>
    get() = client.listBuckets()

  open fun getObjects(dir: String): Iterable<Result<Item>> {
    return client.listObjects(
      ListObjectsArgs.builder()
        .bucket(dir)
        .build()
    )
  }
}
