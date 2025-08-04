package io.github.truenine.composeserver.oss

import io.github.truenine.composeserver.enums.HttpMethod
import java.io.InputStream
import java.time.Instant

/** Storage class for objects */
enum class StorageClass {
  STANDARD,
  REDUCED_REDUNDANCY,
  GLACIER,
  DEEP_ARCHIVE,
  INTELLIGENT_TIERING,
}

/** Request to create a bucket */
data class CreateBucketRequest(
  val bucketName: String,
  val region: String? = null,
  val storageClass: StorageClass = StorageClass.STANDARD,
  val enableVersioning: Boolean = false,
  val tags: Map<String, String> = emptyMap(),
)

/** Bucket information */
data class BucketInfo(
  val name: String,
  val creationDate: Instant,
  val region: String? = null,
  val storageClass: StorageClass = StorageClass.STANDARD,
  val versioningEnabled: Boolean = false,
  val tags: Map<String, String> = emptyMap(),
)

/** Request to put an object */
data class PutObjectRequest(
  val bucketName: String,
  val objectName: String,
  val inputStream: InputStream,
  val size: Long,
  val contentType: String? = null,
  val metadata: Map<String, String> = emptyMap(),
  val storageClass: StorageClass = StorageClass.STANDARD,
  val tags: Map<String, String> = emptyMap(),
)

/** Object information */
data class ObjectInfo(
  val bucketName: String,
  val objectName: String,
  val size: Long,
  val etag: String,
  val lastModified: Instant,
  val contentType: String? = null,
  val metadata: Map<String, String> = emptyMap(),
  val storageClass: StorageClass = StorageClass.STANDARD,
  val tags: Map<String, String> = emptyMap(),
  val versionId: String? = null,
) {
  /** Generate the public URL for this object */
  fun getPublicUrl(baseUrl: String): String {
    return "$baseUrl/$bucketName/$objectName"
  }
}

/** Object content with input stream */
data class ObjectContent(val objectInfo: ObjectInfo, val inputStream: InputStream, val contentRange: ContentRange? = null) : AutoCloseable {
  override fun close() {
    inputStream.close()
  }
}

/** Content range for partial object retrieval */
data class ContentRange(val start: Long, val end: Long, val total: Long)

/** Request to copy an object */
data class CopyObjectRequest(
  val sourceBucketName: String,
  val sourceObjectName: String,
  val destinationBucketName: String,
  val destinationObjectName: String,
  val metadata: Map<String, String> = emptyMap(),
  val storageClass: StorageClass = StorageClass.STANDARD,
  val tags: Map<String, String> = emptyMap(),
)

/** Request to list objects */
data class ListObjectsRequest(
  val bucketName: String,
  val prefix: String? = null,
  val delimiter: String? = null,
  val maxKeys: Int = 1000,
  val continuationToken: String? = null,
  val startAfter: String? = null,
  val recursive: Boolean = true,
)

/** Object listing result */
data class ObjectListing(
  val bucketName: String,
  val objects: List<ObjectInfo>,
  val commonPrefixes: List<String> = emptyList(),
  val isTruncated: Boolean = false,
  val nextContinuationToken: String? = null,
  val maxKeys: Int,
  val prefix: String? = null,
  val delimiter: String? = null,
)

/** Delete operation result */
data class DeleteResult(val objectName: String, val success: Boolean, val errorMessage: String? = null)

/** Request to initiate multipart upload */
data class InitiateMultipartUploadRequest(
  val bucketName: String,
  val objectName: String,
  val contentType: String? = null,
  val metadata: Map<String, String> = emptyMap(),
  val storageClass: StorageClass = StorageClass.STANDARD,
  val tags: Map<String, String> = emptyMap(),
)

/** Multipart upload information */
data class MultipartUpload(val uploadId: String, val bucketName: String, val objectName: String)

/** Request to upload a part */
data class UploadPartRequest(
  val uploadId: String,
  val bucketName: String,
  val objectName: String,
  val partNumber: Int,
  val inputStream: InputStream,
  val size: Long,
)

/** Part information */
data class PartInfo(val partNumber: Int, val etag: String, val size: Long, val lastModified: Instant? = null)

/** Request to complete multipart upload */
data class CompleteMultipartUploadRequest(val uploadId: String, val bucketName: String, val objectName: String, val parts: List<PartInfo>)

/** Request to generate a share link for an object */
data class ShareLinkRequest(
  val bucketName: String,
  val objectName: String,
  val expiration: java.time.Duration,
  val method: HttpMethod = HttpMethod.GET,
  val allowedIps: List<String> = emptyList(),
  val maxDownloads: Int? = null,
  val password: String? = null,
  val metadata: Map<String, String> = emptyMap(),
)

/** Share link information */
data class ShareLinkInfo(
  val shareUrl: String,
  val bucketName: String,
  val objectName: String,
  val expiration: Instant,
  val method: HttpMethod,
  val allowedIps: List<String> = emptyList(),
  val maxDownloads: Int? = null,
  val remainingDownloads: Int? = null,
  val hasPassword: Boolean = false,
  val metadata: Map<String, String> = emptyMap(),
  val createdAt: Instant = Instant.now(),
)

/** Request to upload an object and return a share link */
data class UploadWithLinkRequest(
  val bucketName: String,
  val objectName: String,
  val inputStream: InputStream,
  val size: Long,
  val contentType: String? = null,
  val metadata: Map<String, String> = emptyMap(),
  val storageClass: StorageClass = StorageClass.STANDARD,
  val tags: Map<String, String> = emptyMap(),
  val shareExpiration: java.time.Duration,
  val shareMethod: HttpMethod = HttpMethod.GET,
  val allowedIps: List<String> = emptyList(),
  val maxDownloads: Int? = null,
  val sharePassword: String? = null,
)

/** Response from upload with link operation */
data class UploadWithLinkResponse(val objectInfo: ObjectInfo, val shareLink: ShareLinkInfo, val publicUrl: String? = null)
