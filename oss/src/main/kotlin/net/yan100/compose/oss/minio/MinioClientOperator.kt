package net.yan100.compose.oss.minio

import io.minio.*
import io.minio.messages.Bucket
import io.minio.messages.Item
import java.io.InputStream
import java.io.OutputStream
import net.yan100.compose.core.consts.IHeaders
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.amazon.S3PolicyCreator
import okhttp3.Headers

/**
 * minio 基础层
 *
 * @author TrueNine
 * @since 2022-12-29
 */
open class MinioClientOperator
protected constructor(private val client: MinioClient) {
  open fun headerContentType(headers: Headers): String? {
    return headers[IHeaders.CONTENT_TYPE]
  }

  open fun headerSizeStr(headers: Headers): String? {
    return headers[IHeaders.CONTENT_LENGTH]
  }

  open fun headerSize(headers: Headers): Long? {
    return headerSizeStr(headers)?.toLong()
  }

  open fun getObject(
    fileInfo: FileArgs,
    stream: OutputStream,
  ): GetObjectResponse? {
    return client.getObject(
      GetObjectArgs.builder()
        .bucket(fileInfo.dir)
        .`object`(fileInfo.fileName)
        .build()
    )
  }

  open fun publicBucket(bucketName: String) {
    client.setBucketPolicy(
      SetBucketPolicyArgs.builder()
        .bucket(bucketName)
        .config(S3PolicyCreator.publicBucketAndReadOnly(bucketName).json())
        .build()
    )
  }

  open fun bucketExists(bucketName: String): Boolean {
    return client.bucketExists(
      BucketExistsArgs.builder().bucket(bucketName).build()
    )
  }

  open fun bucketNotExists(bucketName: String): Boolean {
    return !bucketExists(bucketName)
  }

  open fun removeObject(fileInfo: FileArgs): Boolean {
    if (bucketNotExists(fileInfo.dir)) return false
    try {
      client.removeObject(
        RemoveObjectArgs.builder()
          .bucket(fileInfo.dir)
          .`object`(fileInfo.fileName)
          .build()
      )
      return true
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  open fun putObject(
    fileInfo: FileArgs,
    stream: InputStream,
  ): ObjectWriteResponse? {
    if (bucketNotExists(fileInfo.dir)) {
      client.makeBucket(MakeBucketArgs.builder().bucket(fileInfo.dir).build())
    }

    return client.putObject(
      PutObjectArgs.builder()
        .bucket(fileInfo.dir)
        .`object`(fileInfo.fileName)
        .contentType(fileInfo.mimeType)
        .stream(stream, fileInfo.size, -1)
        .build()
    )
  }

  open fun listFiles(dir: String): List<String> {
    if (bucketNotExists(dir)) return listOf()
    return client
      .listObjects(ListObjectsArgs.builder().bucket(dir).build())
      .map { it.get().objectName() }
  }

  open fun listDir(): List<String> {
    return client.listBuckets().map { obj -> obj.name() }
  }

  open fun createBucket(dirName: String) {
    client.makeBucket(MakeBucketArgs.builder().bucket(dirName).build())
  }

  open val buckets: List<Bucket>
    get() = client.listBuckets()

  open fun getObjects(dir: String): Iterable<Result<Item>> {
    return client.listObjects(ListObjectsArgs.builder().bucket(dir).build())
  }
}
