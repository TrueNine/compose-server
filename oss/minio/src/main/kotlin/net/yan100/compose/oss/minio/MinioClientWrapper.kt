package net.yan100.compose.oss.minio

import io.minio.BucketExistsArgs
import io.minio.GetObjectArgs
import io.minio.GetObjectResponse
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.SetBucketPolicyArgs
import net.yan100.compose.consts.IHeaders
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.InMap
import net.yan100.compose.oss.ObjectArgs
import net.yan100.compose.oss.Oss
import net.yan100.compose.oss.OutMap
import net.yan100.compose.oss.S3PolicyCreator
import net.yan100.compose.slf4j
import okhttp3.Headers
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * oss 的 minio 实现
 *
 * @author TrueNine
 * @since 2023-02-20
 */
class MinioClientWrapper(
  private val client: MinioClient,
  private val exposeUrl: String,
) : Oss {
  fun getHeaderContentType(headers: Headers): String? {
    return headers[IHeaders.CONTENT_TYPE]
  }

  private fun getHeaderSizeString(headers: Headers): String? {
    return headers[IHeaders.CONTENT_LENGTH]
  }

  fun getHeaderSize(headers: Headers): Long? {
    return getHeaderSizeString(headers)?.toLongOrNull()
  }

  private fun getObject(fileInfo: FileArgs): GetObjectResponse? {
    return client.getObject(
      GetObjectArgs.builder()
        .bucket(fileInfo.dir)
        .`object`(fileInfo.fileName)
        .build()
    )
  }

  override fun removeObject(objectInfo: FileArgs): Boolean {
    if (!existsBucketByName(objectInfo.dir)) return true
    client.removeObject(
      RemoveObjectArgs.builder()
        .bucket(objectInfo.dir)
        .`object`(objectInfo.fileName)
        .build()
    )
    return true
  }

  private fun createInputMap(
    resp: ObjectWriteResponse,
    stream: InputStream,
  ): InMap {
    return object : InMap {
      override val usedStream
        get() = stream

      override val mediaType
        get() = getHeaderContentType(resp.headers())!!

      override val objectName
        get() = resp.`object`()

      override val bucketName
        get() = resp.bucket()

      override val size
        get() = getHeaderSize(resp.headers())!!

      override val exposeBaseUrl: String
        get() = exposeUrl
    }
  }

  private fun createOutputMap(
    resp: GetObjectResponse,
    stream: OutputStream,
  ): OutMap {
    return object : OutMap {
      override val usedStream
        get() = stream

      override val mediaType
        get() = getHeaderContentType(resp.headers())!!

      override val objectName
        get() = resp.`object`()

      override val bucketName
        get() = resp.bucket()

      override val size
        get() = getHeaderSize(resp.headers())!!

      override val exposeBaseUrl: String
        get() = exposeUrl
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> getNativeClient(): T? {
    return client as? T
  }

  override val exposedBaseUrl: String
    get() = this.exposeUrl

  override fun createBucketByName(bucketName: String) {
    client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
  }

  override fun existsBucketByName(bucketName: String): Boolean {
    return client.bucketExists(
      BucketExistsArgs.builder().bucket(bucketName).build()
    )
  }

  override fun removeObject(info: ObjectArgs) {
    TODO("Not yet implemented")
  }

  override fun setBucketPolicyToPublicReadonly(bucketName: String) {
    client.setBucketPolicy(
      SetBucketPolicyArgs.builder()
        .bucket(bucketName)
        .config(S3PolicyCreator.publicReadonlyBucket(bucketName).json())
        .build()
    )
  }

  override fun uploadObject(stream: InputStream, fileInfo: FileArgs): InMap {
    if (!existsBucketByName(fileInfo.dir)) {
      createBucketByName(fileInfo.dir)
    }
    val ins =
      client.putObject(
        PutObjectArgs.builder()
          .bucket(fileInfo.dir)
          .`object`(fileInfo.fileName)
          .contentType(fileInfo.mimeType)
          .stream(stream, fileInfo.size, -1)
          .build()
      )

    return createInputMap(ins!!, stream)
  }

  override fun uploadObject(
    stream: InputStream,
    fileInfo: FileArgs,
    afterExec: Runnable,
  ): InMap {
    val ins = uploadObject(stream, fileInfo)
    afterExec.run()
    return ins
  }

  @Throws(IOException::class)
  override fun downloadObject(
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap {
    val outs = getObject(fileInfo)
    outs?.transferTo(stream)
    return createOutputMap(outs!!, stream)
  }

  @Throws(IOException::class)
  override fun downloadObject(
    beforeExec: Runnable,
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap {
    val outs = getObject(fileInfo)
    beforeExec.run()
    outs?.transferTo(stream)
    return downloadObject(stream, fileInfo)
  }

  override val isConnected: Boolean
    get() =
      try {
        client.listBuckets()
        true
      } catch (e: Exception) {
        log.error("minio client connect error", e)
        false
      }

  companion object {
    @JvmStatic
    private val log = slf4j<MinioClientWrapper>()
  }

  override fun fetchAllObjectNameByBucketName(
    bucketName: String,
  ): List<String> {
    if (!existsBucketByName(bucketName)) return listOf()
    return client
      .listObjects(ListObjectsArgs.builder().bucket(bucketName).build())
      .map { it.get().objectName() }
  }

  override fun fetchAllBucketNames(): List<String> {
    return client.listBuckets().map { it.name() }
  }
}
