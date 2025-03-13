package net.yan100.compose.oss.common

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * ## 对象存储系统 （Object Storage Service）
 *
 * @author TrueNine
 * @since 2023-06-07
 */
interface Oss {
  fun createBucketByName(bucketName: String)

  fun setBucketPolicyToPublicReadonly(bucketName: String)

  fun existsBucketByName(bucketName: String): Boolean

  @Deprecated("java api") fun removeObject(objectInfo: FileArgs): Boolean

  fun removeObject(info: ObjectArgs)

  fun fetchAllBucketNames(): List<String>

  fun fetchAllObjectNameByBucketName(bucketName: String): List<String>

  fun <T : Any> getNativeClient(): T?

  val exposedBaseUrl: String

  /**
   * 上传
   *
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [InMap]
   */
  @Deprecated("java api")
  fun uploadObject(stream: InputStream, fileInfo: FileArgs): InMap

  /**
   * 上传
   *
   * @param stream 流
   * @param fileInfo 文件信息
   * @param afterExec 后执行
   * @return [InMap]
   */
  @Deprecated("java api")
  fun uploadObject(
    stream: InputStream,
    fileInfo: FileArgs,
    afterExec: Runnable,
  ): InMap

  /**
   * 下载
   *
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  @Deprecated("java api")
  fun downloadObject(stream: OutputStream, fileInfo: FileArgs): OutMap

  /**
   * 下载
   *
   * @param beforeExec 在执行之前
   * @param stream 流
   * @param fileInfo 文件信息
   * @return [OutMap]
   * @throws IOException ioexception
   */
  @Deprecated("java api")
  fun downloadObject(
    beforeExec: Runnable,
    stream: OutputStream,
    fileInfo: FileArgs,
  ): OutMap
}
