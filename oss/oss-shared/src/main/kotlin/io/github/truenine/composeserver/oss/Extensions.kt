package io.github.truenine.composeserver.oss

import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Extension functions for IObjectStorageService */

/** Upload a file to object storage */
suspend fun IObjectStorageService.putObject(
  bucketName: String,
  objectName: String,
  file: File,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  return file.inputStream().use { inputStream -> putObject(bucketName, objectName, inputStream, file.length(), contentType, metadata) }
}

/** Upload a file from Path to object storage */
suspend fun IObjectStorageService.putObject(
  bucketName: String,
  objectName: String,
  path: Path,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  return path.inputStream().use { inputStream -> putObject(bucketName, objectName, inputStream, path.fileSize(), contentType, metadata) }
}

/** Upload byte array to object storage */
suspend fun IObjectStorageService.putObject(
  bucketName: String,
  objectName: String,
  bytes: ByteArray,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  return ByteArrayInputStream(bytes).use { inputStream -> putObject(bucketName, objectName, inputStream, bytes.size.toLong(), contentType, metadata) }
}

/** Upload string content to object storage */
suspend fun IObjectStorageService.putObject(
  bucketName: String,
  objectName: String,
  content: String,
  contentType: String = "text/plain; charset=utf-8",
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  val bytes = content.toByteArray(Charsets.UTF_8)
  return putObject(bucketName, objectName, bytes, contentType, metadata)
}

// putObjectWithBucketCreation extension functions

/** Upload a file to object storage with automatic bucket creation */
suspend fun IObjectStorageService.putObjectWithBucketCreation(
  bucketName: String,
  objectName: String,
  file: File,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  return file.inputStream().use { inputStream -> putObjectWithBucketCreation(bucketName, objectName, inputStream, file.length(), contentType, metadata) }
}

/** Upload a file from Path to object storage with automatic bucket creation */
suspend fun IObjectStorageService.putObjectWithBucketCreation(
  bucketName: String,
  objectName: String,
  path: Path,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  return path.inputStream().use { inputStream -> putObjectWithBucketCreation(bucketName, objectName, inputStream, path.fileSize(), contentType, metadata) }
}

/** Upload byte array to object storage with automatic bucket creation */
suspend fun IObjectStorageService.putObjectWithBucketCreation(
  bucketName: String,
  objectName: String,
  bytes: ByteArray,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  return ByteArrayInputStream(bytes).use { inputStream ->
    putObjectWithBucketCreation(bucketName, objectName, inputStream, bytes.size.toLong(), contentType, metadata)
  }
}

/** Upload string content to object storage with automatic bucket creation */
suspend fun IObjectStorageService.putObjectWithBucketCreation(
  bucketName: String,
  objectName: String,
  content: String,
  contentType: String = "text/plain; charset=utf-8",
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  val bytes = content.toByteArray(Charsets.UTF_8)
  return putObjectWithBucketCreation(bucketName, objectName, bytes, contentType, metadata)
}

/** Download object content as byte array */
suspend fun IObjectStorageService.getObjectBytes(bucketName: String, objectName: String): Result<ByteArray> {
  return getObject(bucketName, objectName).mapCatching { objectContent -> objectContent.use { it.inputStream.readAllBytes() } }
}

/** Download object content as string */
suspend fun IObjectStorageService.getObjectString(bucketName: String, objectName: String, charset: java.nio.charset.Charset = Charsets.UTF_8): Result<String> {
  return getObjectBytes(bucketName, objectName).mapCatching { bytes -> String(bytes, charset) }
}

/** Download object to file */
suspend fun IObjectStorageService.downloadObject(bucketName: String, objectName: String, file: File): Result<Unit> {
  return getObject(bucketName, objectName).mapCatching { objectContent ->
    objectContent.use { content -> file.outputStream().use { outputStream -> content.inputStream.copyTo(outputStream) } }
  }
}

/** Download object to path */
suspend fun IObjectStorageService.downloadObject(bucketName: String, objectName: String, path: Path): Result<Unit> {
  return downloadObject(bucketName, objectName, path.toFile())
}

/** Check if bucket exists and create if not */
suspend fun IObjectStorageService.ensureBucket(bucketName: String): Result<BucketInfo> {
  return bucketExists(bucketName)
    .fold(
      onSuccess = { exists ->
        if (exists) {
          // Get bucket info if exists
          listBuckets().mapCatching { buckets -> buckets.first { it.name == bucketName } }
        } else {
          createBucket(CreateBucketRequest(bucketName))
        }
      },
      onFailure = { Result.failure(it) },
    )
}

/** List all objects in a bucket as a flow */
fun IObjectStorageService.listAllObjectsFlow(bucketName: String, prefix: String? = null): Flow<ObjectInfo> = flow {
  var continuationToken: String? = null
  do {
    val request = ListObjectsRequest(bucketName = bucketName, prefix = prefix, continuationToken = continuationToken)

    val result = listObjects(request).getOrThrow()
    result.objects.forEach { emit(it) }

    continuationToken = result.nextContinuationToken
  } while (result.isTruncated && continuationToken != null)
}

/** Delete all objects with a prefix */
suspend fun IObjectStorageService.deleteObjectsWithPrefix(bucketName: String, prefix: String): Result<List<DeleteResult>> {
  return try {
    val objectNames = mutableListOf<String>()
    listAllObjectsFlow(bucketName, prefix).collect { objectInfo -> objectNames.add(objectInfo.objectName) }

    if (objectNames.isNotEmpty()) {
      deleteObjects(bucketName, objectNames)
    } else {
      Result.success(emptyList())
    }
  } catch (e: Exception) {
    Result.failure(e)
  }
}

// Share Link Extension Functions

/** Generate a simple share link with default settings */
suspend fun IObjectStorageService.generateSimpleShareLink(bucketName: String, objectName: String, expiration: java.time.Duration): Result<ShareLinkInfo> {
  val request = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = expiration)
  return generateShareLink(request)
}

/** Upload a file and generate a share link */
suspend fun IObjectStorageService.uploadFileWithLink(
  bucketName: String,
  objectName: String,
  file: java.io.File,
  shareExpiration: java.time.Duration,
  contentType: String? = null,
  metadata: Map<String, String> = emptyMap(),
): Result<UploadWithLinkResponse> {
  return file.inputStream().use { inputStream ->
    val request =
      UploadWithLinkRequest(
        bucketName = bucketName,
        objectName = objectName,
        inputStream = inputStream,
        size = file.length(),
        contentType = contentType,
        metadata = metadata,
        shareExpiration = shareExpiration,
      )
    uploadWithLink(request)
  }
}

/** Upload a string content and generate a share link */
suspend fun IObjectStorageService.uploadStringWithLink(
  bucketName: String,
  objectName: String,
  content: String,
  shareExpiration: java.time.Duration,
  contentType: String = "text/plain; charset=utf-8",
  metadata: Map<String, String> = emptyMap(),
): Result<UploadWithLinkResponse> {
  val bytes = content.toByteArray(Charsets.UTF_8)
  return ByteArrayInputStream(bytes).use { inputStream ->
    val request =
      UploadWithLinkRequest(
        bucketName = bucketName,
        objectName = objectName,
        inputStream = inputStream,
        size = bytes.size.toLong(),
        contentType = contentType,
        metadata = metadata,
        shareExpiration = shareExpiration,
      )
    uploadWithLink(request)
  }
}

/** Download content from share link as string */
suspend fun IObjectStorageService.downloadStringFromShareLink(shareUrl: String, password: String? = null): Result<String> {
  return downloadFromShareLink(shareUrl, password).mapCatching { objectContent ->
    objectContent.use { it.inputStream.bufferedReader(Charsets.UTF_8).readText() }
  }
}

/** Download content from share link and save to file */
suspend fun IObjectStorageService.downloadFileFromShareLink(shareUrl: String, targetFile: java.io.File, password: String? = null): Result<Unit> {
  return downloadFromShareLink(shareUrl, password).mapCatching { objectContent ->
    objectContent.use { content -> targetFile.outputStream().use { outputStream -> content.inputStream.copyTo(outputStream) } }
  }
}

/** Copy object with new name in the same bucket */
suspend fun IObjectStorageService.copyObject(
  bucketName: String,
  sourceObjectName: String,
  destinationObjectName: String,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  val request =
    CopyObjectRequest(
      sourceBucketName = bucketName,
      sourceObjectName = sourceObjectName,
      destinationBucketName = bucketName,
      destinationObjectName = destinationObjectName,
      metadata = metadata,
    )
  return copyObject(request)
}

/** Move object (copy and delete original) */
suspend fun IObjectStorageService.moveObject(
  sourceBucketName: String,
  sourceObjectName: String,
  destinationBucketName: String,
  destinationObjectName: String,
  metadata: Map<String, String> = emptyMap(),
): Result<ObjectInfo> {
  val copyRequest =
    CopyObjectRequest(
      sourceBucketName = sourceBucketName,
      sourceObjectName = sourceObjectName,
      destinationBucketName = destinationBucketName,
      destinationObjectName = destinationObjectName,
      metadata = metadata,
    )

  return copyObject(copyRequest)
    .fold(
      onSuccess = { objectInfo ->
        deleteObject(sourceBucketName, sourceObjectName).fold(onSuccess = { Result.success(objectInfo) }, onFailure = { Result.failure(it) })
      },
      onFailure = { Result.failure(it) },
    )
}
