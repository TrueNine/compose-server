package io.github.truenine.composeserver.oss

import io.github.truenine.composeserver.enums.HttpMethod
import java.io.InputStream
import java.time.Duration
import kotlinx.coroutines.flow.Flow

/**
 * Modern object storage service interface with Kotlin coroutines support
 *
 * This interface provides a unified API for object storage operations across different providers like MinIO, Volcengine TOS, Aliyun OSS, etc.
 *
 * @author TrueNine
 * @since 2025-08-04
 */
interface IObjectStorageService {

  /** Check if the service is connected and healthy */
  suspend fun isHealthy(): Boolean

  /** Get the native client instance for provider-specific operations */
  fun <T : Any> getNativeClient(): T?

  /** Get the exposed base URL for public access */
  val exposedBaseUrl: String

  // Bucket Operations

  /** Create a new bucket */
  suspend fun createBucket(request: CreateBucketRequest): Result<BucketInfo>

  /** Check if a bucket exists */
  suspend fun bucketExists(bucketName: String): Result<Boolean>

  /** Delete a bucket (must be empty) */
  suspend fun deleteBucket(bucketName: String): Result<Unit>

  /** List all buckets */
  suspend fun listBuckets(): Result<List<BucketInfo>>

  /** Set bucket policy for public read access */
  suspend fun setBucketPublicRead(bucketName: String): Result<Unit>

  /** Get bucket policy */
  suspend fun getBucketPolicy(bucketName: String): Result<String>

  /** Set custom bucket policy */
  suspend fun setBucketPolicy(bucketName: String, policy: String): Result<Unit>

  /** Set bucket and all its objects access level to public or private */
  suspend fun setBucketAccess(bucketName: String, accessLevel: BucketAccessLevel): Result<Unit>

  // Object Operations

  /** Upload an object */
  suspend fun putObject(request: PutObjectRequest): Result<ObjectInfo>

  /** Upload an object with input stream */
  suspend fun putObject(
    bucketName: String,
    objectName: String,
    inputStream: InputStream,
    size: Long,
    contentType: String? = null,
    metadata: Map<String, String> = emptyMap(),
  ): Result<ObjectInfo>

  /** Upload an object with automatic bucket creation if bucket does not exist */
  suspend fun putObjectWithBucketCreation(request: PutObjectRequest): Result<ObjectInfo> {
    return putObjectWithBucketCreation(
      bucketName = request.bucketName,
      objectName = request.objectName,
      inputStream = request.inputStream,
      size = request.size,
      contentType = request.contentType,
      metadata = request.metadata,
    )
  }

  /** Upload an object with automatic bucket creation if bucket does not exist */
  suspend fun putObjectWithBucketCreation(
    bucketName: String,
    objectName: String,
    inputStream: InputStream,
    size: Long,
    contentType: String? = null,
    metadata: Map<String, String> = emptyMap(),
  ): Result<ObjectInfo> {
    return bucketExists(bucketName)
      .fold(
        onSuccess = { exists ->
          if (exists) {
            putObject(bucketName, objectName, inputStream, size, contentType, metadata)
          } else {
            createBucket(CreateBucketRequest(bucketName = bucketName))
              .fold(
                onSuccess = { putObject(bucketName, objectName, inputStream, size, contentType, metadata) },
                onFailure = { exception -> Result.failure(exception) },
              )
          }
        },
        onFailure = { exception -> Result.failure(exception) },
      )
  }

  /** Get object information */
  suspend fun getObjectInfo(bucketName: String, objectName: String): Result<ObjectInfo>

  /** Get object content as input stream */
  suspend fun getObject(bucketName: String, objectName: String): Result<ObjectContent>

  /** Get object content with range */
  suspend fun getObject(bucketName: String, objectName: String, offset: Long, length: Long): Result<ObjectContent>

  /** Check if an object exists */
  suspend fun objectExists(bucketName: String, objectName: String): Result<Boolean>

  /** Delete an object */
  suspend fun deleteObject(bucketName: String, objectName: String): Result<Unit>

  /** Delete multiple objects */
  suspend fun deleteObjects(bucketName: String, objectNames: List<String>): Result<List<DeleteResult>>

  /** Copy an object */
  suspend fun copyObject(request: CopyObjectRequest): Result<ObjectInfo>

  /** List objects in a bucket */
  suspend fun listObjects(request: ListObjectsRequest): Result<ObjectListing>

  /** List objects as a flow for large datasets */
  fun listObjectsFlow(request: ListObjectsRequest): Flow<ObjectInfo>

  /** Generate a presigned URL for object access */
  suspend fun generatePresignedUrl(bucketName: String, objectName: String, expiration: Duration, method: HttpMethod = HttpMethod.GET): Result<String>

  // Multipart Upload Operations

  /** Initiate multipart upload */
  suspend fun initiateMultipartUpload(request: InitiateMultipartUploadRequest): Result<MultipartUpload>

  /** Upload a part */
  suspend fun uploadPart(request: UploadPartRequest): Result<PartInfo>

  /** Complete multipart upload */
  suspend fun completeMultipartUpload(request: CompleteMultipartUploadRequest): Result<ObjectInfo>

  /** Abort multipart upload */
  suspend fun abortMultipartUpload(uploadId: String, bucketName: String, objectName: String): Result<Unit>

  /** List parts of a multipart upload */
  suspend fun listParts(uploadId: String, bucketName: String, objectName: String): Result<List<PartInfo>>

  // Share Link Operations

  /** Generate a share link for an object with advanced options */
  suspend fun generateShareLink(request: ShareLinkRequest): Result<ShareLinkInfo>

  /** Upload an object and return both object info and share link */
  suspend fun uploadWithLink(request: UploadWithLinkRequest): Result<UploadWithLinkResponse>

  /** Download an object using a share link */
  suspend fun downloadFromShareLink(shareUrl: String, password: String? = null): Result<ObjectContent>

  /** Validate a share link without downloading */
  suspend fun validateShareLink(shareUrl: String, password: String? = null): Result<ShareLinkInfo>

  /** Revoke a share link (if supported by the provider) */
  suspend fun revokeShareLink(shareUrl: String): Result<Unit>

  // region Tagging

  /** Set tags for an object */
  suspend fun setObjectTags(bucketName: String, objectName: String, tags: List<Tag>): Result<Unit>

  /** Get tags for an object */
  suspend fun getObjectTags(bucketName: String, objectName: String): Result<List<Tag>>

  /** Delete all tags from an object */
  suspend fun deleteObjectTags(bucketName: String, objectName: String): Result<Unit>

  /** Set tags for a bucket */
  suspend fun setBucketTags(bucketName: String, tags: List<Tag>): Result<Unit>

  /** Get tags for a bucket */
  suspend fun getBucketTags(bucketName: String): Result<List<Tag>>

  /** Delete all tags from a bucket */
  suspend fun deleteBucketTags(bucketName: String): Result<Unit>

  // endregion

  // region Versioning

  /** Set versioning configuration for a bucket */
  suspend fun setBucketVersioning(bucketName: String, enabled: Boolean): Result<Unit>

  /** List all versions of objects in a bucket */
  suspend fun listObjectVersions(request: ListObjectVersionsRequest): Result<ObjectVersionListing>

  // endregion

  // region Lifecycle

  /** Set lifecycle configuration for a bucket */
  suspend fun setBucketLifecycle(bucketName: String, rules: List<LifecycleRule>): Result<Unit>

  /** Get lifecycle configuration for a bucket */
  suspend fun getBucketLifecycle(bucketName: String): Result<List<LifecycleRule>>

  /** Delete lifecycle configuration for a bucket */
  suspend fun deleteBucketLifecycle(bucketName: String): Result<Unit>

  // endregion

  // region CORS

  /** Set CORS configuration for a bucket */
  suspend fun setBucketCors(bucketName: String, rules: List<CorsRule>): Result<Unit>

  /** Get CORS configuration for a bucket */
  suspend fun getBucketCors(bucketName: String): Result<List<CorsRule>>

  /** Delete CORS configuration for a bucket */
  suspend fun deleteBucketCors(bucketName: String): Result<Unit>

  // endregion
}
