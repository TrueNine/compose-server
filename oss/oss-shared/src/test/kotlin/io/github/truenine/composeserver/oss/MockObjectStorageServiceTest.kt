package io.github.truenine.composeserver.oss

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.testtoolkit.log
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Mock implementation of IObjectStorageService for testing the abstract layer */
class MockObjectStorageServiceTest : IObjectStorageServiceTest() {

  override fun createObjectStorageService(): IObjectStorageService {
    return MockObjectStorageService()
  }

  /** Simple mock implementation for testing */
  private class MockObjectStorageService : IObjectStorageService {
    private val buckets = mutableMapOf<String, BucketInfo>()
    private val objects = mutableMapOf<String, MutableMap<String, MockObject>>()

    override val exposedBaseUrl: String = "http://mock-storage.example.com"

    override suspend fun isHealthy(): Boolean = true

    override fun <T : Any> getNativeClient(): T? = null

    override suspend fun createBucket(request: CreateBucketRequest): Result<BucketInfo> {
      if (buckets.containsKey(request.bucketName)) {
        return Result.failure(BucketAlreadyExistsException(request.bucketName))
      }

      val bucketInfo =
        BucketInfo(
          name = request.bucketName,
          creationDate = Instant.now(),
          region = request.region,
          storageClass = request.storageClass,
          versioningEnabled = request.enableVersioning,
          tags = request.tags,
        )

      buckets[request.bucketName] = bucketInfo
      objects[request.bucketName] = mutableMapOf()

      log.debug("Mock: Created bucket ${request.bucketName}")
      return Result.success(bucketInfo)
    }

    override suspend fun bucketExists(bucketName: String): Result<Boolean> {
      return Result.success(buckets.containsKey(bucketName))
    }

    override suspend fun deleteBucket(bucketName: String): Result<Unit> {
      if (!buckets.containsKey(bucketName)) {
        return Result.failure(BucketNotFoundException(bucketName))
      }

      val bucketObjects = objects[bucketName] ?: emptyMap()
      if (bucketObjects.isNotEmpty()) {
        return Result.failure(BucketNotEmptyException(bucketName))
      }

      buckets.remove(bucketName)
      objects.remove(bucketName)

      log.debug("Mock: Deleted bucket $bucketName")
      return Result.success(Unit)
    }

    override suspend fun listBuckets(): Result<List<BucketInfo>> {
      return Result.success(buckets.values.toList())
    }

    override suspend fun setBucketPublicRead(bucketName: String): Result<Unit> {
      if (!buckets.containsKey(bucketName)) {
        return Result.failure(BucketNotFoundException(bucketName))
      }
      log.debug("Mock: Set bucket $bucketName to public read")
      return Result.success(Unit)
    }

    override suspend fun getBucketPolicy(bucketName: String): Result<String> {
      if (!buckets.containsKey(bucketName)) {
        return Result.failure(BucketNotFoundException(bucketName))
      }
      return Result.success("{\"Version\":\"2012-10-17\",\"Statement\":[]}")
    }

    override suspend fun setBucketPolicy(bucketName: String, policy: String): Result<Unit> {
      if (!buckets.containsKey(bucketName)) {
        return Result.failure(BucketNotFoundException(bucketName))
      }
      log.debug("Mock: Set bucket policy for $bucketName")
      return Result.success(Unit)
    }

    override suspend fun putObject(request: PutObjectRequest): Result<ObjectInfo> {
      if (!buckets.containsKey(request.bucketName)) {
        return Result.failure(BucketNotFoundException(request.bucketName))
      }

      val content = request.inputStream.readAllBytes()
      val mockObject =
        MockObject(content = content, contentType = request.contentType, metadata = request.metadata, storageClass = request.storageClass, tags = request.tags)

      objects[request.bucketName]!![request.objectName] = mockObject

      val objectInfo =
        ObjectInfo(
          bucketName = request.bucketName,
          objectName = request.objectName,
          size = content.size.toLong(),
          etag = "mock-etag-${System.currentTimeMillis()}",
          lastModified = Instant.now(),
          contentType = request.contentType,
          metadata = request.metadata,
          storageClass = request.storageClass,
          tags = request.tags,
        )

      log.debug("Mock: Put object ${request.bucketName}/${request.objectName} (${content.size} bytes)")
      return Result.success(objectInfo)
    }

    override suspend fun putObject(
      bucketName: String,
      objectName: String,
      inputStream: InputStream,
      size: Long,
      contentType: String?,
      metadata: Map<String, String>,
    ): Result<ObjectInfo> {
      val request =
        PutObjectRequest(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = inputStream,
          size = size,
          contentType = contentType,
          metadata = metadata,
        )
      return putObject(request)
    }

    override suspend fun getObjectInfo(bucketName: String, objectName: String): Result<ObjectInfo> {
      val bucketObjects = objects[bucketName] ?: return Result.failure(BucketNotFoundException(bucketName))
      val mockObject = bucketObjects[objectName] ?: return Result.failure(ObjectNotFoundException(bucketName, objectName))

      val objectInfo =
        ObjectInfo(
          bucketName = bucketName,
          objectName = objectName,
          size = mockObject.content.size.toLong(),
          etag = "mock-etag-${objectName.hashCode()}",
          lastModified = Instant.now(),
          contentType = mockObject.contentType,
          metadata = mockObject.metadata,
          storageClass = mockObject.storageClass,
          tags = mockObject.tags,
        )

      return Result.success(objectInfo)
    }

    override suspend fun getObject(bucketName: String, objectName: String): Result<ObjectContent> {
      val objectInfoResult = getObjectInfo(bucketName, objectName)
      if (objectInfoResult.isFailure) {
        return Result.failure(objectInfoResult.exceptionOrNull()!!)
      }

      val objectInfo = objectInfoResult.getOrThrow()
      val bucketObjects = objects[bucketName]!!
      val mockObject = bucketObjects[objectName]!!

      val inputStream = ByteArrayInputStream(mockObject.content)
      val objectContent = ObjectContent(objectInfo, inputStream)

      log.debug("Mock: Get object $bucketName/$objectName")
      return Result.success(objectContent)
    }

    override suspend fun getObject(bucketName: String, objectName: String, offset: Long, length: Long): Result<ObjectContent> {
      val objectInfoResult = getObjectInfo(bucketName, objectName)
      if (objectInfoResult.isFailure) {
        return Result.failure(objectInfoResult.exceptionOrNull()!!)
      }

      val objectInfo = objectInfoResult.getOrThrow()
      val bucketObjects = objects[bucketName]!!
      val mockObject = bucketObjects[objectName]!!

      val endIndex = minOf(offset + length, mockObject.content.size.toLong()).toInt()
      val rangeContent = mockObject.content.sliceArray(offset.toInt() until endIndex)
      val inputStream = ByteArrayInputStream(rangeContent)
      val contentRange = ContentRange(offset, endIndex - 1L, mockObject.content.size.toLong())
      val objectContent = ObjectContent(objectInfo, inputStream, contentRange)

      log.debug("Mock: Get object range $bucketName/$objectName [$offset-${endIndex - 1}]")
      return Result.success(objectContent)
    }

    override suspend fun objectExists(bucketName: String, objectName: String): Result<Boolean> {
      val bucketObjects = objects[bucketName] ?: return Result.success(false)
      return Result.success(bucketObjects.containsKey(objectName))
    }

    override suspend fun deleteObject(bucketName: String, objectName: String): Result<Unit> {
      val bucketObjects = objects[bucketName] ?: return Result.failure(BucketNotFoundException(bucketName))

      if (!bucketObjects.containsKey(objectName)) {
        return Result.failure(ObjectNotFoundException(bucketName, objectName))
      }

      bucketObjects.remove(objectName)
      log.debug("Mock: Deleted object $bucketName/$objectName")
      return Result.success(Unit)
    }

    override suspend fun deleteObjects(bucketName: String, objectNames: List<String>): Result<List<DeleteResult>> {
      val bucketObjects = objects[bucketName] ?: return Result.failure(BucketNotFoundException(bucketName))

      val results =
        objectNames.map { objectName ->
          if (bucketObjects.containsKey(objectName)) {
            bucketObjects.remove(objectName)
            DeleteResult(objectName, true)
          } else {
            DeleteResult(objectName, false, "Object not found")
          }
        }

      log.debug("Mock: Deleted ${results.count { it.success }} objects from $bucketName")
      return Result.success(results)
    }

    override suspend fun copyObject(request: CopyObjectRequest): Result<ObjectInfo> {
      val sourceBucketObjects = objects[request.sourceBucketName] ?: return Result.failure(BucketNotFoundException(request.sourceBucketName))
      val sourceObject =
        sourceBucketObjects[request.sourceObjectName] ?: return Result.failure(ObjectNotFoundException(request.sourceBucketName, request.sourceObjectName))

      if (!buckets.containsKey(request.destinationBucketName)) {
        return Result.failure(BucketNotFoundException(request.destinationBucketName))
      }

      val destinationBucketObjects = objects[request.destinationBucketName]!!
      destinationBucketObjects[request.destinationObjectName] =
        sourceObject.copy(
          metadata = request.metadata.ifEmpty { sourceObject.metadata },
          storageClass = request.storageClass,
          tags = request.tags.ifEmpty { sourceObject.tags },
        )

      val objectInfo =
        ObjectInfo(
          bucketName = request.destinationBucketName,
          objectName = request.destinationObjectName,
          size = sourceObject.content.size.toLong(),
          etag = "mock-copy-etag-${System.currentTimeMillis()}",
          lastModified = Instant.now(),
          contentType = sourceObject.contentType,
          metadata = request.metadata.ifEmpty { sourceObject.metadata },
          storageClass = request.storageClass,
          tags = request.tags.ifEmpty { sourceObject.tags },
        )

      log.debug(
        "Mock: Copied object ${request.sourceBucketName}/${request.sourceObjectName} -> ${request.destinationBucketName}/${request.destinationObjectName}"
      )
      return Result.success(objectInfo)
    }

    override suspend fun listObjects(request: ListObjectsRequest): Result<ObjectListing> {
      val bucketObjects = objects[request.bucketName] ?: return Result.failure(BucketNotFoundException(request.bucketName))

      val filteredObjects =
        bucketObjects.entries
          .filter { request.prefix?.let { prefix -> it.key.startsWith(prefix) } ?: true }
          .take(request.maxKeys)
          .map { (objectName, mockObject) ->
            ObjectInfo(
              bucketName = request.bucketName,
              objectName = objectName,
              size = mockObject.content.size.toLong(),
              etag = "mock-etag-${objectName.hashCode()}",
              lastModified = Instant.now(),
              contentType = mockObject.contentType,
              metadata = mockObject.metadata,
              storageClass = mockObject.storageClass,
              tags = mockObject.tags,
            )
          }

      val listing =
        ObjectListing(
          bucketName = request.bucketName,
          objects = filteredObjects,
          commonPrefixes = emptyList(),
          isTruncated = false,
          nextContinuationToken = null,
          maxKeys = request.maxKeys,
          prefix = request.prefix,
          delimiter = request.delimiter,
        )

      return Result.success(listing)
    }

    override fun listObjectsFlow(request: ListObjectsRequest): Flow<ObjectInfo> = flow {
      val result = listObjects(request)
      if (result.isSuccess) {
        result.getOrThrow().objects.forEach { emit(it) }
      }
    }

    override suspend fun generatePresignedUrl(bucketName: String, objectName: String, expiration: Duration, method: HttpMethod): Result<String> {
      if (!buckets.containsKey(bucketName)) {
        return Result.failure(BucketNotFoundException(bucketName))
      }

      val url = "$exposedBaseUrl/$bucketName/$objectName?mock-presigned=true&expires=${System.currentTimeMillis() + expiration.toMillis()}"
      log.debug("Mock: Generated presigned URL for $bucketName/$objectName")
      return Result.success(url)
    }

    // Simplified multipart upload implementations
    override suspend fun initiateMultipartUpload(request: InitiateMultipartUploadRequest): Result<MultipartUpload> {
      if (!buckets.containsKey(request.bucketName)) {
        return Result.failure(BucketNotFoundException(request.bucketName))
      }

      val upload = MultipartUpload(uploadId = "mock-upload-${System.currentTimeMillis()}", bucketName = request.bucketName, objectName = request.objectName)

      log.debug("Mock: Initiated multipart upload for ${request.bucketName}/${request.objectName}")
      return Result.success(upload)
    }

    override suspend fun uploadPart(request: UploadPartRequest): Result<PartInfo> {
      val content = request.inputStream.readAllBytes()
      val partInfo = PartInfo(partNumber = request.partNumber, etag = "mock-part-etag-${request.partNumber}", size = content.size.toLong())

      log.debug("Mock: Uploaded part ${request.partNumber} for ${request.bucketName}/${request.objectName}")
      return Result.success(partInfo)
    }

    override suspend fun completeMultipartUpload(request: CompleteMultipartUploadRequest): Result<ObjectInfo> {
      val totalSize = request.parts.sumOf { it.size }
      val objectInfo =
        ObjectInfo(
          bucketName = request.bucketName,
          objectName = request.objectName,
          size = totalSize,
          etag = "mock-multipart-etag-${System.currentTimeMillis()}",
          lastModified = Instant.now(),
        )

      log.debug("Mock: Completed multipart upload for ${request.bucketName}/${request.objectName}")
      return Result.success(objectInfo)
    }

    override suspend fun abortMultipartUpload(uploadId: String, bucketName: String, objectName: String): Result<Unit> {
      log.debug("Mock: Aborted multipart upload $uploadId for $bucketName/$objectName")
      return Result.success(Unit)
    }

    override suspend fun listParts(uploadId: String, bucketName: String, objectName: String): Result<List<PartInfo>> {
      log.debug("Mock: Listed parts for upload $uploadId")
      return Result.success(emptyList())
    }

    // Share Link Operations Implementation

    override suspend fun generateShareLink(request: ShareLinkRequest): Result<ShareLinkInfo> {
      if (!buckets.containsKey(request.bucketName)) {
        return Result.failure(BucketNotFoundException(request.bucketName))
      }

      val bucketObjects = objects[request.bucketName] ?: return Result.failure(ObjectNotFoundException(request.bucketName, request.objectName))
      if (!bucketObjects.containsKey(request.objectName)) {
        return Result.failure(ObjectNotFoundException(request.bucketName, request.objectName))
      }

      val shareInfo =
        ShareLinkInfo(
          shareUrl =
            "http://mock-storage.example.com/share/${request.bucketName}/${request.objectName}?expires=${System.currentTimeMillis() + request.expiration.toMillis()}",
          bucketName = request.bucketName,
          objectName = request.objectName,
          expiration = java.time.Instant.now().plus(request.expiration),
          method = request.method,
          allowedIps = request.allowedIps,
          maxDownloads = request.maxDownloads,
          remainingDownloads = request.maxDownloads,
          hasPassword = request.password != null,
          metadata = request.metadata,
        )

      return Result.success(shareInfo)
    }

    override suspend fun uploadWithLink(request: UploadWithLinkRequest): Result<UploadWithLinkResponse> {
      // First upload the object
      val putRequest =
        PutObjectRequest(
          bucketName = request.bucketName,
          objectName = request.objectName,
          inputStream = request.inputStream,
          size = request.size,
          contentType = request.contentType,
          metadata = request.metadata,
          storageClass = request.storageClass,
          tags = request.tags,
        )

      val uploadResult = putObject(putRequest)
      if (uploadResult.isFailure) {
        return Result.failure(uploadResult.exceptionOrNull()!!)
      }

      val objectInfo = uploadResult.getOrThrow()

      // Generate share link
      val shareLinkRequest =
        ShareLinkRequest(
          bucketName = request.bucketName,
          objectName = request.objectName,
          expiration = request.shareExpiration,
          method = request.shareMethod,
          allowedIps = request.allowedIps,
          maxDownloads = request.maxDownloads,
          password = request.sharePassword,
          metadata = request.metadata,
        )

      val shareLinkResult = generateShareLink(shareLinkRequest)
      if (shareLinkResult.isFailure) {
        return Result.failure(shareLinkResult.exceptionOrNull()!!)
      }

      val shareLink = shareLinkResult.getOrThrow()
      val publicUrl = objectInfo.getPublicUrl(exposedBaseUrl)

      val response = UploadWithLinkResponse(objectInfo = objectInfo, shareLink = shareLink, publicUrl = publicUrl)

      return Result.success(response)
    }

    override suspend fun downloadFromShareLink(shareUrl: String, password: String?): Result<ObjectContent> {
      // Parse the mock share URL to extract bucket and object name
      val urlPattern = Regex("http://mock-storage\\.example\\.com/share/([^/]+)/([^?]+)")
      val matchResult = urlPattern.find(shareUrl)
      if (matchResult == null) {
        return Result.failure(IllegalArgumentException("Invalid share URL format"))
      }

      val bucketName = matchResult.groupValues[1]
      val objectName = matchResult.groupValues[2]

      return getObject(bucketName, objectName)
    }

    override suspend fun validateShareLink(shareUrl: String, password: String?): Result<ShareLinkInfo> {
      // Parse the mock share URL
      val urlPattern = Regex("http://mock-storage\\.example\\.com/share/([^/]+)/([^?]+)\\?expires=(\\d+)")
      val matchResult = urlPattern.find(shareUrl)
      if (matchResult == null) {
        return Result.failure(IllegalArgumentException("Invalid share URL format"))
      }

      val bucketName = matchResult.groupValues[1]
      val objectName = matchResult.groupValues[2]
      val expiresTimestamp = matchResult.groupValues[3].toLongOrNull() ?: return Result.failure(IllegalArgumentException("Invalid expiration timestamp"))

      // Check if the object exists
      val existsResult = objectExists(bucketName, objectName)
      if (existsResult.isFailure || !existsResult.getOrThrow()) {
        return Result.failure(ObjectNotFoundException(bucketName, objectName))
      }

      // Check if the link has expired
      if (System.currentTimeMillis() > expiresTimestamp) {
        return Result.failure(IllegalArgumentException("Share link has expired"))
      }

      val shareInfo =
        ShareLinkInfo(
          shareUrl = shareUrl,
          bucketName = bucketName,
          objectName = objectName,
          expiration = java.time.Instant.ofEpochMilli(expiresTimestamp),
          method = HttpMethod.GET,
          hasPassword = password != null,
        )

      return Result.success(shareInfo)
    }

    override suspend fun revokeShareLink(shareUrl: String): Result<Unit> {
      // Mock implementation - always succeeds
      return Result.success(Unit)
    }

    private data class MockObject(
      val content: ByteArray,
      val contentType: String? = null,
      val metadata: Map<String, String> = emptyMap(),
      val storageClass: StorageClass = StorageClass.STANDARD,
      val tags: Map<String, String> = emptyMap(),
    ) {
      override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MockObject

        if (!content.contentEquals(other.content)) return false
        if (contentType != other.contentType) return false
        if (metadata != other.metadata) return false
        if (storageClass != other.storageClass) return false
        if (tags != other.tags) return false

        return true
      }

      override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + (contentType?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        result = 31 * result + storageClass.hashCode()
        result = 31 * result + tags.hashCode()
        return result
      }
    }
  }
}
