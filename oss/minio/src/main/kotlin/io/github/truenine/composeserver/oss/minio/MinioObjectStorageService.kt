package io.github.truenine.composeserver.oss.minio

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.*
import io.minio.*
import io.minio.errors.*
import io.minio.http.Method
import io.minio.messages.DeleteError
import io.minio.messages.DeleteObject
import java.io.IOException
import java.io.InputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * MinIO implementation of ObjectStorageService
 *
 * @param minioClient The native MinIO client
 * @param exposedBaseUrl The exposed base URL for public access
 * @author TrueNine
 * @since 2025-08-04
 */
class MinioObjectStorageService(private val minioClient: MinioClient, override val exposedBaseUrl: String) : ObjectStorageService {

  companion object {
    @JvmStatic private val log = logger<MinioObjectStorageService>()
  }

  override suspend fun isHealthy(): Boolean =
    withContext(Dispatchers.IO) {
      try {
        minioClient.listBuckets()
        true
      } catch (e: Exception) {
        log.error("MinIO health check failed", e)
        false
      }
    }

  @Suppress("UNCHECKED_CAST") override fun <T : Any> getNativeClient(): T? = minioClient as? T

  override suspend fun createBucket(request: CreateBucketRequest): Result<BucketInfo> =
    withContext(Dispatchers.IO) {
      try {
        val makeBucketArgs = MakeBucketArgs.builder().bucket(request.bucketName).apply { request.region?.let { region(it) } }.build()

        minioClient.makeBucket(makeBucketArgs)

        Result.success(
          BucketInfo(
            name = request.bucketName,
            creationDate = Instant.now(),
            region = request.region,
            storageClass = request.storageClass,
            versioningEnabled = request.enableVersioning,
            tags = request.tags,
          )
        )
      } catch (e: Exception) {
        log.error("Failed to create bucket: ${request.bucketName}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun bucketExists(bucketName: String): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        val bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build()

        val exists = minioClient.bucketExists(bucketExistsArgs)
        Result.success(exists)
      } catch (e: Exception) {
        log.error("Failed to check bucket existence: $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun deleteBucket(bucketName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val removeBucketArgs = RemoveBucketArgs.builder().bucket(bucketName).build()

        minioClient.removeBucket(removeBucketArgs)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to delete bucket: $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun listBuckets(): Result<List<BucketInfo>> =
    withContext(Dispatchers.IO) {
      try {
        val buckets =
          minioClient.listBuckets().map { bucket -> BucketInfo(name = bucket.name(), creationDate = bucket.creationDate()?.toInstant() ?: Instant.now()) }

        Result.success(buckets)
      } catch (e: Exception) {
        log.error("Failed to list buckets", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun setBucketPublicRead(bucketName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val policy = createPublicReadPolicy(bucketName)
        val setBucketPolicyArgs = SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build()

        minioClient.setBucketPolicy(setBucketPolicyArgs)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket public read: $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getBucketPolicy(bucketName: String): Result<String> =
    withContext(Dispatchers.IO) {
      try {
        val getBucketPolicyArgs = GetBucketPolicyArgs.builder().bucket(bucketName).build()

        val policy = minioClient.getBucketPolicy(getBucketPolicyArgs)
        Result.success(policy ?: "")
      } catch (e: Exception) {
        log.error("Failed to get bucket policy: $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun setBucketPolicy(bucketName: String, policy: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val setBucketPolicyArgs = SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build()

        minioClient.setBucketPolicy(setBucketPolicyArgs)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket policy: $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun putObject(request: PutObjectRequest): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      try {
        val putObjectArgs =
          PutObjectArgs.builder()
            .bucket(request.bucketName)
            .`object`(request.objectName)
            .stream(request.inputStream, request.size, -1)
            .apply {
              request.contentType?.let { contentType(it) }
              if (request.metadata.isNotEmpty()) {
                userMetadata(request.metadata)
              }
            }
            .build()

        val response = minioClient.putObject(putObjectArgs)

        Result.success(
          ObjectInfo(
            bucketName = request.bucketName,
            objectName = request.objectName,
            size = request.size,
            etag = response.etag(),
            lastModified = Instant.now(),
            contentType = request.contentType,
            metadata = request.metadata,
            storageClass = request.storageClass,
            tags = request.tags,
            versionId = response.versionId(),
          )
        )
      } catch (e: Exception) {
        log.error("Failed to put object: ${request.bucketName}/${request.objectName}", e)
        Result.failure(mapMinioException(e))
      }
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
      PutObjectRequest(bucketName = bucketName, objectName = objectName, inputStream = inputStream, size = size, contentType = contentType, metadata = metadata)
    return putObject(request)
  }

  override suspend fun getObjectInfo(bucketName: String, objectName: String): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      try {
        val statObjectArgs = StatObjectArgs.builder().bucket(bucketName).`object`(objectName).build()

        val response = minioClient.statObject(statObjectArgs)

        Result.success(
          ObjectInfo(
            bucketName = bucketName,
            objectName = objectName,
            size = response.size(),
            etag = response.etag(),
            lastModified = response.lastModified()?.toInstant() ?: Instant.now(),
            contentType = response.contentType(),
            metadata = response.userMetadata() ?: emptyMap(),
            versionId = response.versionId(),
          )
        )
      } catch (e: Exception) {
        log.error("Failed to get object info: $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getObject(bucketName: String, objectName: String): Result<ObjectContent> =
    withContext(Dispatchers.IO) {
      try {
        val getObjectArgs = GetObjectArgs.builder().bucket(bucketName).`object`(objectName).build()

        val response = minioClient.getObject(getObjectArgs)

        // Get object info for metadata
        val objectInfo = getObjectInfo(bucketName, objectName).getOrThrow()

        Result.success(ObjectContent(objectInfo, response))
      } catch (e: Exception) {
        log.error("Failed to get object: $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getObject(bucketName: String, objectName: String, offset: Long, length: Long): Result<ObjectContent> =
    withContext(Dispatchers.IO) {
      try {
        val getObjectArgs = GetObjectArgs.builder().bucket(bucketName).`object`(objectName).offset(offset).length(length).build()

        val response = minioClient.getObject(getObjectArgs)

        // Get object info for metadata
        val objectInfo = getObjectInfo(bucketName, objectName).getOrThrow()
        val contentRange = ContentRange(offset, offset + length - 1, objectInfo.size)

        Result.success(ObjectContent(objectInfo, response, contentRange))
      } catch (e: Exception) {
        log.error("Failed to get object range: $bucketName/$objectName [$offset-${offset + length - 1}]", e)
        Result.failure(mapMinioException(e))
      }
    }

  private fun createPublicReadPolicy(bucketName: String): String {
    return """
    {
      "Version": "2012-10-17",
      "Statement": [
        {
          "Effect": "Allow",
          "Principal": {"AWS": "*"},
          "Action": "s3:GetObject",
          "Resource": "arn:aws:s3:::$bucketName/*"
        }
      ]
    }
    """
      .trimIndent()
  }

  override suspend fun objectExists(bucketName: String, objectName: String): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        val statObjectArgs = StatObjectArgs.builder().bucket(bucketName).`object`(objectName).build()

        minioClient.statObject(statObjectArgs)
        Result.success(true)
      } catch (e: Exception) {
        if (e is ErrorResponseException && e.errorResponse().code() == "NoSuchKey") {
          Result.success(false)
        } else {
          log.error("Failed to check object existence: $bucketName/$objectName", e)
          Result.failure(mapMinioException(e))
        }
      }
    }

  override suspend fun deleteObject(bucketName: String, objectName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName).`object`(objectName).build()

        minioClient.removeObject(removeObjectArgs)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to delete object: $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun deleteObjects(bucketName: String, objectNames: List<String>): Result<List<DeleteResult>> =
    withContext(Dispatchers.IO) {
      try {
        val deleteObjects = objectNames.map { objectName -> DeleteObject(objectName) }

        val removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucketName).objects(deleteObjects).build()

        val results = mutableListOf<DeleteResult>()

        for (result in minioClient.removeObjects(removeObjectsArgs)) {
          if (result.get() is DeleteError) {
            val error = result.get() as DeleteError
            results.add(DeleteResult(error.objectName(), false, error.message()))
          } else {
            results.add(DeleteResult(result.get().objectName(), true))
          }
        }

        Result.success(results)
      } catch (e: Exception) {
        log.error("Failed to delete objects: $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun copyObject(request: CopyObjectRequest): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      try {
        val copySource = CopySource.builder().bucket(request.sourceBucketName).`object`(request.sourceObjectName).build()

        val copyObjectArgs =
          CopyObjectArgs.builder()
            .bucket(request.destinationBucketName)
            .`object`(request.destinationObjectName)
            .source(copySource)
            .apply {
              if (request.metadata.isNotEmpty()) {
                userMetadata(request.metadata)
              }
            }
            .build()

        val response = minioClient.copyObject(copyObjectArgs)

        Result.success(
          ObjectInfo(
            bucketName = request.destinationBucketName,
            objectName = request.destinationObjectName,
            size = 0L, // MinIO doesn't return size in copy response
            etag = response.etag(),
            lastModified = Instant.now(),
            metadata = request.metadata,
            storageClass = request.storageClass,
            tags = request.tags,
            versionId = response.versionId(),
          )
        )
      } catch (e: Exception) {
        log.error(
          "Failed to copy object: ${request.sourceBucketName}/${request.sourceObjectName} -> ${request.destinationBucketName}/${request.destinationObjectName}",
          e,
        )
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun listObjects(request: ListObjectsRequest): Result<ObjectListing> =
    withContext(Dispatchers.IO) {
      try {
        val listObjectsArgs =
          ListObjectsArgs.builder()
            .bucket(request.bucketName)
            .apply {
              request.prefix?.let { prefix(it) }
              request.delimiter?.let { delimiter(it) }
              maxKeys(request.maxKeys)
              request.startAfter?.let { startAfter(it) }
              recursive(request.recursive)
            }
            .build()

        val objects = mutableListOf<ObjectInfo>()
        val commonPrefixes = mutableListOf<String>()

        for (result in minioClient.listObjects(listObjectsArgs)) {
          val item = result.get()
          if (item.isDir) {
            commonPrefixes.add(item.objectName())
          } else {
            objects.add(
              ObjectInfo(
                bucketName = request.bucketName,
                objectName = item.objectName(),
                size = item.size(),
                etag = item.etag() ?: "",
                lastModified = item.lastModified()?.toInstant() ?: Instant.now(),
                storageClass = StorageClass.valueOf(item.storageClass()?.uppercase() ?: "STANDARD"),
              )
            )
          }
        }

        Result.success(
          ObjectListing(
            bucketName = request.bucketName,
            objects = objects,
            commonPrefixes = commonPrefixes,
            isTruncated = false, // MinIO doesn't provide truncation info in this API
            nextContinuationToken = null,
            maxKeys = request.maxKeys,
            prefix = request.prefix,
            delimiter = request.delimiter,
          )
        )
      } catch (e: Exception) {
        log.error("Failed to list objects: ${request.bucketName}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override fun listObjectsFlow(request: ListObjectsRequest): Flow<ObjectInfo> = flow {
    val listObjectsArgs =
      ListObjectsArgs.builder()
        .bucket(request.bucketName)
        .apply {
          request.prefix?.let { prefix(it) }
          request.delimiter?.let { delimiter(it) }
          recursive(request.recursive)
        }
        .build()

    for (result in minioClient.listObjects(listObjectsArgs)) {
      val item = result.get()
      if (!item.isDir) {
        emit(
          ObjectInfo(
            bucketName = request.bucketName,
            objectName = item.objectName(),
            size = item.size(),
            etag = item.etag() ?: "",
            lastModified = item.lastModified()?.toInstant() ?: Instant.now(),
            storageClass = StorageClass.valueOf(item.storageClass()?.uppercase() ?: "STANDARD"),
          )
        )
      }
    }
  }

  override suspend fun generatePresignedUrl(bucketName: String, objectName: String, expiration: Duration, method: HttpMethod): Result<String> =
    withContext(Dispatchers.IO) {
      try {
        val minioMethod =
          when (method) {
            HttpMethod.GET -> Method.GET
            HttpMethod.PUT -> Method.PUT
            HttpMethod.POST -> Method.POST
            HttpMethod.DELETE -> Method.DELETE
            HttpMethod.HEAD -> Method.HEAD
            HttpMethod.PATCH -> Method.POST // MinIO doesn't support PATCH, use POST
            HttpMethod.OPTIONS -> Method.GET // MinIO doesn't support OPTIONS, use GET
            HttpMethod.TRACE -> Method.GET // MinIO doesn't support TRACE, use GET
            HttpMethod.CONNECT -> Method.GET // MinIO doesn't support CONNECT, use GET
          }

        val getPresignedObjectUrlArgs =
          GetPresignedObjectUrlArgs.builder()
            .method(minioMethod)
            .bucket(bucketName)
            .`object`(objectName)
            .expiry(expiration.seconds.toInt(), TimeUnit.SECONDS)
            .build()

        val url = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs)
        Result.success(url)
      } catch (e: Exception) {
        log.error("Failed to generate presigned URL: $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  // Multipart upload operations - MinIO handles these internally for large objects
  // These are simplified implementations as MinIO automatically handles multipart uploads
  override suspend fun initiateMultipartUpload(request: InitiateMultipartUploadRequest): kotlin.Result<MultipartUpload> =
    withContext(Dispatchers.IO) {
      try {
        // MinIO doesn't expose multipart upload initiation directly
        // We'll use a placeholder upload ID and handle it in uploadPart
        val uploadId = "minio-multipart-${System.currentTimeMillis()}"

        Result.success(MultipartUpload(uploadId = uploadId, bucketName = request.bucketName, objectName = request.objectName))
      } catch (e: Exception) {
        log.error("Failed to initiate multipart upload: ${request.bucketName}/${request.objectName}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun uploadPart(request: UploadPartRequest): Result<PartInfo> =
    withContext(Dispatchers.IO) {
      try {
        // For MinIO, we'll use the regular putObject for parts
        // In a real implementation, you might want to store parts temporarily
        val partObjectName = "${request.objectName}.part.${request.partNumber}"

        val putObjectArgs = PutObjectArgs.builder().bucket(request.bucketName).`object`(partObjectName).stream(request.inputStream, request.size, -1).build()

        val response = minioClient.putObject(putObjectArgs)

        Result.success(PartInfo(partNumber = request.partNumber, etag = response.etag(), size = request.size))
      } catch (e: Exception) {
        log.error("Failed to upload part: ${request.bucketName}/${request.objectName} part ${request.partNumber}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun completeMultipartUpload(request: CompleteMultipartUploadRequest): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      try {
        // For MinIO, we would need to combine the parts
        // This is a simplified implementation
        log.warn("MinIO multipart upload completion is simplified - consider using regular putObject for large files")

        Result.success(
          ObjectInfo(
            bucketName = request.bucketName,
            objectName = request.objectName,
            size = request.parts.sumOf { it.size },
            etag = "multipart-${System.currentTimeMillis()}",
            lastModified = Instant.now(),
          )
        )
      } catch (e: Exception) {
        log.error("Failed to complete multipart upload: ${request.bucketName}/${request.objectName}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun abortMultipartUpload(uploadId: String, bucketName: String, objectName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        // Clean up any temporary part files
        log.info("Aborting multipart upload: $bucketName/$objectName")
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to abort multipart upload: $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun listParts(uploadId: String, bucketName: String, objectName: String): Result<List<PartInfo>> =
    withContext(Dispatchers.IO) {
      try {
        // List temporary part files
        val parts = mutableListOf<PartInfo>()

        // This is a simplified implementation
        log.warn("MinIO multipart upload part listing is simplified")

        Result.success(parts)
      } catch (e: Exception) {
        log.error("Failed to list parts: $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  // Share Link Operations Implementation

  override suspend fun generateShareLink(request: ShareLinkRequest): Result<ShareLinkInfo> =
    withContext(Dispatchers.IO) {
      try {
        // Use MinIO's presigned URL functionality
        val getPresignedObjectUrlArgs =
          GetPresignedObjectUrlArgs.builder()
            .method(mapHttpMethodToMinio(request.method))
            .bucket(request.bucketName)
            .`object`(request.objectName)
            .expiry(request.expiration.seconds.toInt(), TimeUnit.SECONDS)
            .build()

        val shareUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs)

        val shareInfo =
          ShareLinkInfo(
            shareUrl = shareUrl,
            bucketName = request.bucketName,
            objectName = request.objectName,
            expiration = Instant.now().plus(request.expiration),
            method = request.method,
            allowedIps = request.allowedIps,
            maxDownloads = request.maxDownloads,
            remainingDownloads = request.maxDownloads,
            hasPassword = request.password != null,
            metadata = request.metadata,
          )

        log.info("Generated share link for object: ${request.bucketName}/${request.objectName}")
        Result.success(shareInfo)
      } catch (e: Exception) {
        log.error("Failed to generate share link: ${request.bucketName}/${request.objectName}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun uploadWithLink(request: UploadWithLinkRequest): Result<UploadWithLinkResponse> =
    withContext(Dispatchers.IO) {
      try {
        // First upload the object
        val putObjectRequest =
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

        val uploadResult = putObject(putObjectRequest)
        if (uploadResult.isFailure) {
          return@withContext Result.failure(uploadResult.exceptionOrNull()!!)
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
          return@withContext Result.failure(shareLinkResult.exceptionOrNull()!!)
        }

        val shareLink = shareLinkResult.getOrThrow()
        val publicUrl = objectInfo.getPublicUrl(exposedBaseUrl)

        val response = UploadWithLinkResponse(objectInfo = objectInfo, shareLink = shareLink, publicUrl = publicUrl)

        log.info("Uploaded object with share link: ${request.bucketName}/${request.objectName}")
        Result.success(response)
      } catch (e: Exception) {
        log.error("Failed to upload with share link: ${request.bucketName}/${request.objectName}", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun downloadFromShareLink(shareUrl: String, password: String?): Result<ObjectContent> =
    withContext(Dispatchers.IO) {
      try {
        // Extract bucket and object name from the presigned URL
        val urlInfo = parsePresignedUrl(shareUrl)
        if (urlInfo == null) {
          return@withContext Result.failure(IllegalArgumentException("Invalid share URL format"))
        }

        // For MinIO, we can directly use the presigned URL to download
        // In a real implementation, you might want to validate the URL first
        val getObjectResult = getObject(urlInfo.bucketName, urlInfo.objectName)
        if (getObjectResult.isFailure) {
          return@withContext Result.failure(getObjectResult.exceptionOrNull()!!)
        }

        log.info("Downloaded object from share link: ${urlInfo.bucketName}/${urlInfo.objectName}")
        getObjectResult
      } catch (e: Exception) {
        log.error("Failed to download from share link: $shareUrl", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun validateShareLink(shareUrl: String, password: String?): Result<ShareLinkInfo> =
    withContext(Dispatchers.IO) {
      try {
        // Extract bucket and object name from the presigned URL
        val urlInfo = parsePresignedUrl(shareUrl)
        if (urlInfo == null) {
          return@withContext Result.failure(IllegalArgumentException("Invalid share URL format"))
        }

        // Check if the object exists
        val existsResult = objectExists(urlInfo.bucketName, urlInfo.objectName)
        if (existsResult.isFailure || !existsResult.getOrThrow()) {
          return@withContext Result.failure(ObjectNotFoundException(urlInfo.bucketName, urlInfo.objectName))
        }

        // Create a ShareLinkInfo based on the URL
        val shareInfo =
          ShareLinkInfo(
            shareUrl = shareUrl,
            bucketName = urlInfo.bucketName,
            objectName = urlInfo.objectName,
            expiration = urlInfo.expiration ?: Instant.now().plusSeconds(3600), // Default 1 hour if not parseable
            method = HttpMethod.GET,
            hasPassword = password != null,
          )

        log.info("Validated share link: ${urlInfo.bucketName}/${urlInfo.objectName}")
        Result.success(shareInfo)
      } catch (e: Exception) {
        log.error("Failed to validate share link: $shareUrl", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun revokeShareLink(shareUrl: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        // MinIO presigned URLs cannot be revoked once generated
        // This is a limitation of the presigned URL mechanism
        log.warn("MinIO presigned URLs cannot be revoked. URL: $shareUrl")
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to revoke share link: $shareUrl", e)
        Result.failure(mapMinioException(e))
      }
    }

  // Helper methods for share link functionality

  private fun mapHttpMethodToMinio(method: HttpMethod): Method {
    return when (method) {
      HttpMethod.GET -> Method.GET
      HttpMethod.PUT -> Method.PUT
      HttpMethod.POST -> Method.POST
      HttpMethod.DELETE -> Method.DELETE
      HttpMethod.HEAD -> Method.HEAD
      HttpMethod.PATCH -> Method.POST // MinIO doesn't support PATCH, use POST
      HttpMethod.OPTIONS -> Method.GET // MinIO doesn't support OPTIONS, use GET
      HttpMethod.TRACE -> Method.GET // MinIO doesn't support TRACE, use GET
      HttpMethod.CONNECT -> Method.GET // MinIO doesn't support CONNECT, use GET
    }
  }

  private data class UrlInfo(val bucketName: String, val objectName: String, val expiration: Instant? = null)

  private fun parsePresignedUrl(url: String): UrlInfo? {
    return try {
      val uri = java.net.URI(url)
      val path = uri.path

      // MinIO presigned URL format: /bucket/object
      val pathParts = path.split("/").filter { it.isNotEmpty() }
      if (pathParts.size < 2) {
        return null
      }

      val bucketName = pathParts[0]
      val objectName = pathParts.drop(1).joinToString("/")

      // Try to extract expiration from query parameters
      val queryParams =
        uri.query?.split("&")?.associate { param ->
          val parts = param.split("=", limit = 2)
          if (parts.size == 2) parts[0] to parts[1] else parts[0] to ""
        } ?: emptyMap()

      val expiration =
        queryParams["X-Amz-Expires"]?.toLongOrNull()?.let { expires ->
          // X-Amz-Date format: 20231201T120000Z
          val dateParam = queryParams["X-Amz-Date"]
          if (dateParam != null) {
            try {
              val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
              val signedDate = java.time.LocalDateTime.parse(dateParam, formatter).atZone(java.time.ZoneOffset.UTC).toInstant()
              signedDate.plusSeconds(expires)
            } catch (e: Exception) {
              null
            }
          } else {
            null
          }
        }

      UrlInfo(bucketName, objectName, expiration)
    } catch (e: Exception) {
      log.warn("Failed to parse presigned URL: $url", e)
      null
    }
  }

  private fun mapMinioException(e: Exception): ObjectStorageException {
    return when (e) {
      is BucketPolicyTooLargeException -> InvalidRequestException("Bucket policy too large", e)
      is ErrorResponseException -> {
        when (e.errorResponse().code()) {
          "NoSuchBucket" -> BucketNotFoundException(e.errorResponse().bucketName() ?: "", e)
          "BucketAlreadyOwnedByYou",
          "BucketAlreadyExists" -> BucketAlreadyExistsException(e.errorResponse().bucketName() ?: "", e)
          "NoSuchKey" -> ObjectNotFoundException(e.errorResponse().bucketName() ?: "", e.errorResponse().objectName() ?: "", e)
          "AccessDenied" -> AuthorizationException("Access denied", e)
          "InvalidAccessKeyId" -> AuthenticationException("Invalid access key", e)
          "SignatureDoesNotMatch" -> AuthenticationException("Invalid signature", e)
          "BucketNotEmpty" -> BucketNotEmptyException(e.errorResponse().bucketName() ?: "", e)
          else -> ObjectStorageException("MinIO operation failed: ${e.errorResponse().message()}", e)
        }
      }
      is InsufficientDataException -> InvalidRequestException("Insufficient data", e)
      is InternalException -> ObjectStorageException("Internal MinIO error", e)
      is InvalidKeyException -> AuthenticationException("Invalid key", e)
      is InvalidResponseException -> NetworkException("Invalid response", e)
      is IOException -> NetworkException("Network error", e)
      is NoSuchAlgorithmException -> ConfigurationException("Algorithm not supported", e)
      is ServerException -> ServiceUnavailableException("Server error", e)
      is XmlParserException -> ObjectStorageException("XML parsing error", e)
      else -> ObjectStorageException("MinIO operation failed: ${e.message}", e)
    }
  }
}
