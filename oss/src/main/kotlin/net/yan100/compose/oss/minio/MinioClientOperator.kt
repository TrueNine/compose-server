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
package net.yan100.compose.oss.minio

import io.minio.*
import io.minio.messages.Bucket
import io.minio.messages.Item
import net.yan100.compose.oss.FileArgs
import net.yan100.compose.oss.amazon.S3PolicyCreator
import okhttp3.Headers
import java.io.InputStream
import java.io.OutputStream

/**
 * minio 基础层
 *
 * @author TrueNine
 * @since 2022-12-29
 */
open class MinioClientOperator protected constructor(private val client: MinioClient) {
  open fun headerContentType(headers: Headers): String? {
    return headers[net.yan100.compose.core.http.Headers.CONTENT_TYPE]
  }

  open fun headerSizeStr(headers: Headers): String? {
    return headers[net.yan100.compose.core.http.Headers.CONTENT_LENGTH]
  }

  open fun headerSize(headers: Headers): Long? {
    return headerSizeStr(headers)?.toLong()
  }

  open fun getObject(fileInfo: FileArgs, stream: OutputStream): GetObjectResponse? {
    return client.getObject(GetObjectArgs.builder().bucket(fileInfo.dir).`object`(fileInfo.fileName).build())
  }

  open fun publicBucket(bucketName: String) {
    client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(S3PolicyCreator.publicBucketAndReadOnly(bucketName).json()).build())
  }

  open fun bucketExists(bucketName: String): Boolean {
    return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
  }

  open fun bucketNotExists(bucketName: String): Boolean {
    return !bucketExists(bucketName)
  }

  open fun removeObject(fileInfo: FileArgs): Boolean {
    if (bucketNotExists(fileInfo.dir)) return false
    try {
      client.removeObject(RemoveObjectArgs.builder().bucket(fileInfo.dir).`object`(fileInfo.fileName).build())
      return true
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  open fun putObject(fileInfo: FileArgs, stream: InputStream): ObjectWriteResponse? {
    if (bucketNotExists(fileInfo.dir)) {
      client.makeBucket(MakeBucketArgs.builder().bucket(fileInfo.dir).build())
    }

    return client.putObject(
      PutObjectArgs.builder().bucket(fileInfo.dir).`object`(fileInfo.fileName).contentType(fileInfo.mimeType).stream(stream, fileInfo.size, -1).build()
    )
  }

  open fun listFiles(dir: String): List<String> {
    if (bucketNotExists(dir)) return listOf()
    return client.listObjects(ListObjectsArgs.builder().bucket(dir).build()).map { it.get().objectName() }
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
