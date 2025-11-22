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

/** Access level for buckets and objects */
enum class BucketAccessLevel {
  PUBLIC,
  PRIVATE,
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
    return io.github.truenine.composeserver.buildObjectUrl(baseUrl, bucketName, objectName)
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

// region Tagging

/** Represents a single tag for a bucket or object */
data class Tag(val key: String, val value: String)

// endregion

// region Versioning

/** Request to list object versions */
data class ListObjectVersionsRequest(
  val bucketName: String,
  val prefix: String? = null,
  val delimiter: String? = null,
  val maxKeys: Int = 1000,
  val keyMarker: String? = null,
  val versionIdMarker: String? = null,
)

/** Information about a specific version of an object */
data class ObjectVersionInfo(
  val bucketName: String,
  val objectName: String,
  val versionId: String,
  val isLatest: Boolean,
  val lastModified: Instant,
  val etag: String,
  val size: Long,
  val storageClass: StorageClass,
  val isDeleteMarker: Boolean = false,
)

/** Result of listing object versions */
data class ObjectVersionListing(
  val bucketName: String,
  val prefix: String?,
  val keyMarker: String?,
  val versionIdMarker: String?,
  val nextKeyMarker: String?,
  val nextVersionIdMarker: String?,
  val versions: List<ObjectVersionInfo>,
  val commonPrefixes: List<String>,
  val isTruncated: Boolean,
  val maxKeys: Int,
  val delimiter: String?,
)

// endregion

// region Lifecycle

/** Status of a lifecycle rule */
enum class LifecycleRuleStatus {
  ENABLED,
  DISABLED,
}

/**
 * Represents a lifecycle rule for a bucket
 *
 * @property id Unique identifier for the rule.
 * @property prefix The object key prefix this rule applies to.
 * @property status Whether the rule is enabled or disabled.
 * @property transition Defines when objects are transitioned to a different storage class.
 * @property expiration Defines when objects are permanently deleted.
 * @property noncurrentVersionTransition Defines when noncurrent object versions are transitioned.
 * @property noncurrentVersionExpiration Defines when noncurrent object versions are permanently deleted.
 * @property abortIncompleteMultipartUpload Defines when to abort incomplete multipart uploads.
 */
data class LifecycleRule(
  val id: String,
  val prefix: String?,
  val status: LifecycleRuleStatus,
  val tags: List<Tag> = emptyList(),
  val transition: LifecycleTransition? = null,
  val expiration: LifecycleExpiration? = null,
  val noncurrentVersionTransition: LifecycleNoncurrentVersionTransition? = null,
  val noncurrentVersionExpiration: LifecycleNoncurrentVersionExpiration? = null,
  val abortIncompleteMultipartUpload: AbortIncompleteMultipartUpload? = null,
)

/** Defines the transition of an object to another storage class */
data class LifecycleTransition(val days: Int, val storageClass: StorageClass)

/** Defines the expiration of an object */
data class LifecycleExpiration(val days: Int)

/** Defines the transition of noncurrent object versions */
data class LifecycleNoncurrentVersionTransition(val days: Int, val storageClass: StorageClass)

/** Defines the expiration of noncurrent object versions */
data class LifecycleNoncurrentVersionExpiration(val days: Int)

/** Defines when to abort incomplete multipart uploads */
data class AbortIncompleteMultipartUpload(val daysAfterInitiation: Int)

// endregion

// region CORS

/**
 * Represents a CORS rule for a bucket
 *
 * @property id Optional unique identifier for the rule.
 * @property allowedOrigins List of allowed origins.
 * @property allowedMethods List of allowed HTTP methods.
 * @property allowedHeaders List of allowed headers.
 * @property exposeHeaders List of headers to expose to the client.
 * @property maxAgeSeconds The maximum time in seconds that the browser can cache the preflight response.
 */
data class CorsRule(
  val id: String? = null,
  val allowedOrigins: List<String>,
  val allowedMethods: List<HttpMethod>,
  val allowedHeaders: List<String> = emptyList(),
  val exposeHeaders: List<String> = emptyList(),
  val maxAgeSeconds: Int? = null,
)

// endregion
