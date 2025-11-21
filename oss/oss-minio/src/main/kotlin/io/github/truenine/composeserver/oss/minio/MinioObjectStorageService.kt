package io.github.truenine.composeserver.oss.minio

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.mapFailure
import io.github.truenine.composeserver.onFailureDo
import io.github.truenine.composeserver.oss.AbortIncompleteMultipartUpload
import io.github.truenine.composeserver.oss.AuthenticationException
import io.github.truenine.composeserver.oss.AuthorizationException
import io.github.truenine.composeserver.oss.BucketAccessLevel
import io.github.truenine.composeserver.oss.BucketAlreadyExistsException
import io.github.truenine.composeserver.oss.BucketInfo
import io.github.truenine.composeserver.oss.BucketNotEmptyException
import io.github.truenine.composeserver.oss.BucketNotFoundException
import io.github.truenine.composeserver.oss.CompleteMultipartUploadRequest
import io.github.truenine.composeserver.oss.ConfigurationException
import io.github.truenine.composeserver.oss.ContentRange
import io.github.truenine.composeserver.oss.CopyObjectRequest
import io.github.truenine.composeserver.oss.CorsRule
import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.DeleteResult
import io.github.truenine.composeserver.oss.IObjectStorageService
import io.github.truenine.composeserver.oss.InitiateMultipartUploadRequest
import io.github.truenine.composeserver.oss.InvalidRequestException
import io.github.truenine.composeserver.oss.LifecycleExpiration
import io.github.truenine.composeserver.oss.LifecycleNoncurrentVersionExpiration
import io.github.truenine.composeserver.oss.LifecycleNoncurrentVersionTransition
import io.github.truenine.composeserver.oss.LifecycleRule
import io.github.truenine.composeserver.oss.LifecycleRuleStatus
import io.github.truenine.composeserver.oss.LifecycleTransition
import io.github.truenine.composeserver.oss.ListObjectVersionsRequest
import io.github.truenine.composeserver.oss.ListObjectsRequest
import io.github.truenine.composeserver.oss.MultipartUpload
import io.github.truenine.composeserver.oss.NetworkException
import io.github.truenine.composeserver.oss.ObjectContent
import io.github.truenine.composeserver.oss.ObjectInfo
import io.github.truenine.composeserver.oss.ObjectListing
import io.github.truenine.composeserver.oss.ObjectNotFoundException
import io.github.truenine.composeserver.oss.ObjectStorageException
import io.github.truenine.composeserver.oss.ObjectVersionInfo
import io.github.truenine.composeserver.oss.ObjectVersionListing
import io.github.truenine.composeserver.oss.PartInfo
import io.github.truenine.composeserver.oss.PutObjectRequest
import io.github.truenine.composeserver.oss.ServiceUnavailableException
import io.github.truenine.composeserver.oss.ShareLinkInfo
import io.github.truenine.composeserver.oss.ShareLinkRequest
import io.github.truenine.composeserver.oss.StorageClass
import io.github.truenine.composeserver.oss.Tag
import io.github.truenine.composeserver.oss.UploadPartRequest
import io.github.truenine.composeserver.oss.UploadWithLinkRequest
import io.github.truenine.composeserver.oss.UploadWithLinkResponse
import io.github.truenine.composeserver.safeCallAsync
import io.github.truenine.composeserver.slf4j
import io.minio.BucketExistsArgs
import io.minio.CopyObjectArgs
import io.minio.CopySource
import io.minio.DeleteBucketCorsArgs
import io.minio.DeleteBucketLifecycleArgs
import io.minio.DeleteBucketTagsArgs
import io.minio.DeleteObjectTagsArgs
import io.minio.GetBucketCorsArgs
import io.minio.GetBucketLifecycleArgs
import io.minio.GetBucketPolicyArgs
import io.minio.GetBucketTagsArgs
import io.minio.GetObjectArgs
import io.minio.GetObjectTagsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.ListObjectVersionsResponse
import io.minio.ListObjectsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioAsyncClient
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveBucketArgs
import io.minio.RemoveObjectArgs
import io.minio.RemoveObjectsArgs
import io.minio.S3Escaper
import io.minio.SetBucketCorsArgs
import io.minio.SetBucketLifecycleArgs
import io.minio.SetBucketPolicyArgs
import io.minio.SetBucketTagsArgs
import io.minio.SetBucketVersioningArgs
import io.minio.SetObjectTagsArgs
import io.minio.StatObjectArgs
import io.minio.errors.BucketPolicyTooLargeException
import io.minio.errors.ErrorResponseException
import io.minio.errors.InsufficientDataException
import io.minio.errors.InternalException
import io.minio.errors.InvalidResponseException
import io.minio.errors.ServerException
import io.minio.errors.XmlParserException
import io.minio.http.Method
import io.minio.messages.AbortIncompleteMultipartUpload as MinioAbortIncompleteMultipartUpload
import io.minio.messages.AndOperator
import io.minio.messages.CORSConfiguration
import io.minio.messages.DeleteError
import io.minio.messages.DeleteObject
import io.minio.messages.Expiration as MinioExpiration
import io.minio.messages.Item
import io.minio.messages.LifecycleConfiguration
import io.minio.messages.LifecycleRule as MinioLifecycleRule
import io.minio.messages.NoncurrentVersionExpiration as MinioNoncurrentVersionExpiration
import io.minio.messages.NoncurrentVersionTransition as MinioNoncurrentVersionTransition
import io.minio.messages.Part
import io.minio.messages.ResponseDate
import io.minio.messages.RuleFilter
import io.minio.messages.Status as MinioRuleStatus
import io.minio.messages.Tag as MinioTag
import io.minio.messages.Tags
import io.minio.messages.Transition as MinioTransition
import io.minio.messages.VersioningConfiguration
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * MinIO implementation of IObjectStorageService
 *
 * @param minioClient The native MinIO client
 * @param exposedBaseUrl The exposed base URL for public access
 * @author TrueNine
 * @since 2025-08-04
 */
class MinioObjectStorageService(private val minioClient: MinioClient, override val exposedBaseUrl: String) : IObjectStorageService {

  companion object {
    @JvmStatic private val log = slf4j<MinioObjectStorageService>()
  }

  private val asyncClient: MinioAsyncClient by lazy {
    val field = MinioClient::class.java.getDeclaredField("asyncClient").apply { isAccessible = true }
    field.get(minioClient) as MinioAsyncClient
  }

  private val listObjectVersionsMethod by lazy {
    val method =
      MinioAsyncClient::class
        .java
        .superclass
        .getDeclaredMethod(
          "listObjectVersions",
          String::class.java,
          String::class.java,
          String::class.java,
          String::class.java,
          String::class.java,
          java.lang.Integer::class.java,
          String::class.java,
          String::class.java,
          Multimap::class.java,
          Multimap::class.java,
        )
    method.isAccessible = true
    method
  }

  override suspend fun isHealthy(): Boolean {
    val result = safeCallAsync { minioClient.listBuckets() }
    return result.fold(
      onSuccess = { true },
      onFailure = { e ->
        log.error("MinIO health check failed", e)
        false
      },
    )
  }

  @Suppress("UNCHECKED_CAST") override fun <T : Any> getNativeClient(): T? = minioClient as? T

  override suspend fun createBucket(request: CreateBucketRequest): Result<BucketInfo> {
    return safeCallAsync {
        val makeBucketArgs = MakeBucketArgs.builder().bucket(request.bucketName).apply { request.region?.let { region(it) } }.build()

        minioClient.makeBucket(makeBucketArgs)

        BucketInfo(
          name = request.bucketName,
          creationDate = Instant.now(),
          region = request.region,
          storageClass = request.storageClass,
          versioningEnabled = request.enableVersioning,
          tags = request.tags,
        )
      }
      .onFailureDo { e -> log.error("Failed to create bucket: ${request.bucketName}", e) }
      .mapFailure { e -> mapMinioException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun bucketExists(bucketName: String): Result<Boolean> {
    return safeCallAsync {
        val bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build()
        minioClient.bucketExists(bucketExistsArgs)
      }
      .onFailureDo { e -> log.error("Failed to check bucket existence: $bucketName", e) }
      .mapFailure { e -> mapMinioException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun deleteBucket(bucketName: String): Result<Unit> {
    return safeCallAsync {
        val removeBucketArgs = RemoveBucketArgs.builder().bucket(bucketName).build()
        minioClient.removeBucket(removeBucketArgs)
      }
      .onFailureDo { e -> log.error("Failed to delete bucket: $bucketName", e) }
      .mapFailure { e -> mapMinioException(e as? Exception ?: Exception(e)) }
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

  override suspend fun setBucketAccess(bucketName: String, accessLevel: BucketAccessLevel): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val policy =
          when (accessLevel) {
            BucketAccessLevel.PUBLIC -> createPublicReadPolicy(bucketName)
            BucketAccessLevel.PRIVATE -> createPrivatePolicy(bucketName)
          }

        val setBucketPolicyArgs = SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build()
        minioClient.setBucketPolicy(setBucketPolicyArgs)

        log.info("Set bucket access level to ${accessLevel.name.lowercase()}: $bucketName")
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket access level: $bucketName", e)
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

  private fun createPrivatePolicy(bucketName: String): String {
    return """
    {
      "Version": "2012-10-17",
      "Statement": []
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

  // Multipart upload operations - rely on MinIO async client under the hood
  override suspend fun initiateMultipartUpload(request: InitiateMultipartUploadRequest): Result<MultipartUpload> =
    withContext(Dispatchers.IO) {
      try {
        val headers = HashMultimap.create<String, String>()
        request.contentType?.let { headers.put("Content-Type", it) }
        if (request.metadata.isNotEmpty()) {
          request.metadata.forEach { (key, value) ->
            val headerName = if (key.startsWith("x-amz-meta-")) key else "x-amz-meta-$key"
            headers.put(headerName, value)
          }
        }
        if (request.tags.isNotEmpty()) {
          val taggingHeader = request.tags.entries.joinToString("&") { (key, value) -> "${S3Escaper.encode(key)}=${S3Escaper.encode(value)}" }
          headers.put("x-amz-tagging", taggingHeader)
        }

        val response = asyncClient.createMultipartUploadAsync(request.bucketName, null, request.objectName, headers, HashMultimap.create()).get()

        Result.success(MultipartUpload(uploadId = response.result().uploadId(), bucketName = request.bucketName, objectName = request.objectName))
      } catch (e: Exception) {
        val actual = unwrapException(e)
        log.error("Failed to initiate multipart upload: ${request.bucketName}/${request.objectName}", actual)
        Result.failure(mapMinioException(actual))
      }
    }

  override suspend fun uploadPart(request: UploadPartRequest): Result<PartInfo> =
    withContext(Dispatchers.IO) {
      try {
        val response =
          asyncClient
            .uploadPartAsync(
              request.bucketName,
              null,
              request.objectName,
              request.inputStream,
              request.size,
              request.uploadId,
              request.partNumber,
              HashMultimap.create(),
              HashMultimap.create(),
            )
            .get()

        Result.success(PartInfo(partNumber = response.partNumber(), etag = response.etag(), size = request.size))
      } catch (e: Exception) {
        val actual = unwrapException(e)
        log.error("Failed to upload part: ${request.bucketName}/${request.objectName} part ${request.partNumber}", actual)
        Result.failure(mapMinioException(actual))
      }
    }

  override suspend fun completeMultipartUpload(request: CompleteMultipartUploadRequest): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      try {
        val parts = request.parts.sortedBy { it.partNumber }.map { Part(it.partNumber, it.etag) }.toTypedArray()
        val response =
          asyncClient
            .completeMultipartUploadAsync(request.bucketName, null, request.objectName, request.uploadId, parts, HashMultimap.create(), HashMultimap.create())
            .get()

        Result.success(
          ObjectInfo(
            bucketName = response.bucket(),
            objectName = response.`object`(),
            size = 0L, // Size is not returned in response; callers can stat if needed
            etag = response.etag() ?: "",
            lastModified = Instant.now(),
            versionId = response.versionId(),
          )
        )
      } catch (e: Exception) {
        val actual = unwrapException(e)
        log.error("Failed to complete multipart upload: ${request.bucketName}/${request.objectName}", actual)
        Result.failure(mapMinioException(actual))
      }
    }

  override suspend fun abortMultipartUpload(uploadId: String, bucketName: String, objectName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        asyncClient.abortMultipartUploadAsync(bucketName, null, objectName, uploadId, HashMultimap.create(), HashMultimap.create()).get()
        Result.success(Unit)
      } catch (e: Exception) {
        val actual = unwrapException(e)
        log.error("Failed to abort multipart upload: $bucketName/$objectName", actual)
        Result.failure(mapMinioException(actual))
      }
    }

  override suspend fun listParts(uploadId: String, bucketName: String, objectName: String): Result<List<PartInfo>> =
    withContext(Dispatchers.IO) {
      try {
        val response = asyncClient.listPartsAsync(bucketName, null, objectName, null, null, uploadId, HashMultimap.create(), HashMultimap.create()).get()

        val parts =
          response.result().partList().map { part ->
            PartInfo(
              partNumber = part.partNumber(),
              etag = part.etag(),
              size = part.partSize(),
              lastModified = runCatching { part.lastModified()?.toInstant() }.getOrNull(),
            )
          }

        Result.success(parts)
      } catch (e: Exception) {
        val actual = unwrapException(e)
        log.error("Failed to list parts: $bucketName/$objectName", actual)
        Result.failure(mapMinioException(actual))
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

  // region Tagging

  override suspend fun setObjectTags(bucketName: String, objectName: String, tags: List<Tag>): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val minioTags = Tags.newObjectTags(tags.associate { it.key to it.value })
        val args = SetObjectTagsArgs.builder().bucket(bucketName).`object`(objectName).tags(minioTags).build()
        minioClient.setObjectTags(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set object tags for $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getObjectTags(bucketName: String, objectName: String): Result<List<Tag>> =
    withContext(Dispatchers.IO) {
      try {
        val args = GetObjectTagsArgs.builder().bucket(bucketName).`object`(objectName).build()
        val minioTags = minioClient.getObjectTags(args)
        val tags = minioTags.get().map { (key, value) -> Tag(key, value) }
        Result.success(tags)
      } catch (e: Exception) {
        log.error("Failed to get object tags for $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun deleteObjectTags(bucketName: String, objectName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val args = DeleteObjectTagsArgs.builder().bucket(bucketName).`object`(objectName).build()
        minioClient.deleteObjectTags(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to delete object tags for $bucketName/$objectName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun setBucketTags(bucketName: String, tags: List<Tag>): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val minioTags = Tags.newBucketTags(tags.associate { it.key to it.value })
        val args = SetBucketTagsArgs.builder().bucket(bucketName).tags(minioTags).build()
        minioClient.setBucketTags(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket tags for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getBucketTags(bucketName: String): Result<List<Tag>> =
    withContext(Dispatchers.IO) {
      try {
        val args = GetBucketTagsArgs.builder().bucket(bucketName).build()
        val minioTags = minioClient.getBucketTags(args)
        val tags = minioTags.get().map { (key, value) -> Tag(key, value) }
        Result.success(tags)
      } catch (e: Exception) {
        log.error("Failed to get bucket tags for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun deleteBucketTags(bucketName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val args = DeleteBucketTagsArgs.builder().bucket(bucketName).build()
        minioClient.deleteBucketTags(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to delete bucket tags for {}", bucketName, e)
        Result.failure(mapMinioException(e))
      }
    }

  // endregion

  // region Versioning

  override suspend fun setBucketVersioning(bucketName: String, enabled: Boolean): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val status = if (enabled) VersioningConfiguration.Status.ENABLED else VersioningConfiguration.Status.SUSPENDED
        val config = VersioningConfiguration(status, false)
        val args = SetBucketVersioningArgs.builder().bucket(bucketName).config(config).build()
        minioClient.setBucketVersioning(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket versioning for {}", bucketName, e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun listObjectVersions(request: ListObjectVersionsRequest): Result<ObjectVersionListing> =
    withContext(Dispatchers.IO) {
      try {
        val response =
          fetchObjectVersionsResponse(
            bucketName = request.bucketName,
            delimiter = request.delimiter,
            keyMarker = request.keyMarker,
            maxKeys = request.maxKeys,
            prefix = request.prefix,
            versionIdMarker = request.versionIdMarker,
          )

        val result = response.result()

        val versions = mutableListOf<ObjectVersionInfo>()
        result.contents().forEach { versions += it.toVersionInfo(request.bucketName) }
        result.deleteMarkers().forEach { versions += it.toVersionInfo(request.bucketName) }

        val commonPrefixes = result.commonPrefixes().map { it.toItem().objectName() }

        Result.success(
          ObjectVersionListing(
            bucketName = request.bucketName,
            prefix = result.prefix(),
            keyMarker = result.keyMarker(),
            versionIdMarker = result.versionIdMarker(),
            nextKeyMarker = result.nextKeyMarker(),
            nextVersionIdMarker = result.nextVersionIdMarker(),
            versions = versions.sortedBy { it.objectName },
            commonPrefixes = commonPrefixes,
            isTruncated = result.isTruncated(),
            maxKeys = result.maxKeys(),
            delimiter = result.delimiter(),
          )
        )
      } catch (e: Exception) {
        val actual = unwrapException(e)
        log.error("Failed to list object versions for ${request.bucketName}", actual)
        Result.failure(mapMinioException(actual))
      }
    }

  // endregion

  // region Lifecycle

  override suspend fun setBucketLifecycle(bucketName: String, rules: List<LifecycleRule>): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val minioRules = rules.map { it.toMinio() }
        val config = LifecycleConfiguration(minioRules)
        val args = SetBucketLifecycleArgs.builder().bucket(bucketName).config(config).build()
        minioClient.setBucketLifecycle(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket lifecycle for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getBucketLifecycle(bucketName: String): Result<List<LifecycleRule>> =
    withContext(Dispatchers.IO) {
      try {
        val args = GetBucketLifecycleArgs.builder().bucket(bucketName).build()
        val config = minioClient.getBucketLifecycle(args)
        val rules = config.rules()?.map { it.toComposeServer() } ?: emptyList()
        Result.success(rules)
      } catch (e: Exception) {
        log.error("Failed to get bucket lifecycle for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun deleteBucketLifecycle(bucketName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val args = DeleteBucketLifecycleArgs.builder().bucket(bucketName).build()
        minioClient.deleteBucketLifecycle(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to delete bucket lifecycle for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  // endregion

  // region CORS

  /**
   * Set CORS configuration for a bucket.
   *
   * **Important**: Bucket-level CORS configuration is only supported in MinIO AiStor (paid version). The community edition of MinIO does not support per-bucket
   * CORS settings and will return an error: "A header you provided implies functionality that is not implemented"
   *
   * For the community edition, use cluster-wide CORS via the `MINIO_API_CORS_ALLOW_ORIGIN` environment variable.
   *
   * @see <a href="https://github.com/minio/minio/discussions/20841">MinIO CORS Discussion</a>
   */
  override suspend fun setBucketCors(bucketName: String, rules: List<CorsRule>): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val minioRules = rules.map { it.toMinio() }
        val config = CORSConfiguration(minioRules)
        val args = SetBucketCorsArgs.builder().bucket(bucketName).config(config).build()
        minioClient.setBucketCors(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to set bucket CORS for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun getBucketCors(bucketName: String): Result<List<CorsRule>> =
    withContext(Dispatchers.IO) {
      try {
        val args = GetBucketCorsArgs.builder().bucket(bucketName).build()
        val minioConfig = minioClient.getBucketCors(args)
        val rules = minioConfig.rules().map { it.toComposeServer() }
        Result.success(rules)
      } catch (e: Exception) {
        log.error("Failed to get bucket CORS for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  override suspend fun deleteBucketCors(bucketName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        val args = DeleteBucketCorsArgs.builder().bucket(bucketName).build()
        minioClient.deleteBucketCors(args)
        Result.success(Unit)
      } catch (e: Exception) {
        log.error("Failed to delete bucket CORS for $bucketName", e)
        Result.failure(mapMinioException(e))
      }
    }

  // endregion

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

  private fun fetchObjectVersionsResponse(
    bucketName: String,
    delimiter: String?,
    keyMarker: String?,
    maxKeys: Int,
    prefix: String?,
    versionIdMarker: String?,
  ): ListObjectVersionsResponse {
    val headers = HashMultimap.create<String, String>()
    val queryParams = HashMultimap.create<String, String>()
    return try {
      listObjectVersionsMethod.invoke(asyncClient, bucketName, null, delimiter, null, keyMarker, maxKeys, prefix, versionIdMarker, headers, queryParams)
        as ListObjectVersionsResponse
    } catch (e: InvocationTargetException) {
      val cause = e.cause as? Exception ?: e
      throw cause
    }
  }

  private fun Item.toVersionInfo(bucketName: String): ObjectVersionInfo {
    val storageClassName = storageClass()?.uppercase() ?: StorageClass.STANDARD.name
    val storageClass = runCatching { StorageClass.valueOf(storageClassName) }.getOrDefault(StorageClass.STANDARD)
    val lastModifiedInstant = runCatching { lastModified()?.toInstant() }.getOrNull() ?: Instant.EPOCH
    val sizeValue = runCatching { size() }.getOrDefault(0L)
    val versionIdValue = versionId() ?: ""

    return ObjectVersionInfo(
      bucketName = bucketName,
      objectName = objectName(),
      versionId = versionIdValue,
      isLatest = isLatest(),
      lastModified = lastModifiedInstant,
      etag = etag() ?: "",
      size = sizeValue,
      storageClass = storageClass,
      isDeleteMarker = isDeleteMarker(),
    )
  }

  private fun unwrapException(e: Exception): Exception {
    return when (e) {
      is ExecutionException -> (e.cause as? Exception) ?: e
      is CompletionException -> (e.cause as? Exception) ?: e
      else -> e
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

  // Mappers
  private fun LifecycleRule.toMinio(): MinioLifecycleRule {
    val tagMap = if (tags.isNotEmpty()) tags.associate { it.key to it.value } else emptyMap()
    val hasPrefix = !prefix.isNullOrBlank()
    val filter =
      when {
        hasPrefix && tagMap.isNotEmpty() -> RuleFilter(AndOperator(prefix, tagMap))
        tagMap.size > 1 -> RuleFilter(AndOperator(null, tagMap))
        tagMap.size == 1 -> {
          val entry = tagMap.entries.first()
          RuleFilter(MinioTag(entry.key, entry.value))
        }
        hasPrefix -> RuleFilter(prefix!!)
        else -> RuleFilter("")
      }

    return MinioLifecycleRule(
      if (status == LifecycleRuleStatus.ENABLED) MinioRuleStatus.ENABLED else MinioRuleStatus.DISABLED,
      abortIncompleteMultipartUpload?.let { MinioAbortIncompleteMultipartUpload(it.daysAfterInitiation) },
      expiration?.let { MinioExpiration(null as ResponseDate?, it.days, null) },
      filter,
      id,
      noncurrentVersionExpiration?.let { MinioNoncurrentVersionExpiration(it.days) },
      noncurrentVersionTransition?.let { MinioNoncurrentVersionTransition(it.days, it.storageClass.name) },
      transition?.let { MinioTransition(null as ResponseDate?, it.days, it.storageClass.name) },
    )
  }

  private fun MinioLifecycleRule.toComposeServer(): LifecycleRule {
    val ruleFilter = filter()
    val andOperator = ruleFilter?.andOperator()

    val prefixValue =
      when {
        !ruleFilter?.prefix().isNullOrBlank() -> ruleFilter?.prefix()
        !andOperator?.prefix().isNullOrBlank() -> andOperator?.prefix()
        else -> null
      }

    val ruleTags =
      when {
        andOperator?.tags() != null -> andOperator.tags().entries.map { Tag(it.key, it.value) }
        ruleFilter?.tag() != null -> listOf(Tag(ruleFilter.tag().key(), ruleFilter.tag().value()))
        else -> emptyList()
      }

    val ruleStatus = if (status() == MinioRuleStatus.ENABLED) LifecycleRuleStatus.ENABLED else LifecycleRuleStatus.DISABLED

    val transitionValue =
      transition()?.let { transition ->
        transition.days()?.let { days ->
          val storageClass = runCatching { StorageClass.valueOf(transition.storageClass().uppercase()) }.getOrDefault(StorageClass.STANDARD)
          LifecycleTransition(days, storageClass)
        }
      }

    val expirationValue = expiration()?.days()?.let { LifecycleExpiration(it) }

    val nonCurrentTransitionValue =
      noncurrentVersionTransition()?.let { nvt ->
        val storageClass = runCatching { StorageClass.valueOf(nvt.storageClass().uppercase()) }.getOrDefault(StorageClass.STANDARD)
        LifecycleNoncurrentVersionTransition(nvt.noncurrentDays(), storageClass)
      }

    val nonCurrentExpirationValue = noncurrentVersionExpiration()?.let { LifecycleNoncurrentVersionExpiration(it.noncurrentDays()) }

    val abortMultipart = abortIncompleteMultipartUpload()?.let { AbortIncompleteMultipartUpload(it.daysAfterInitiation()) }

    return LifecycleRule(
      id = id(),
      prefix = prefixValue,
      status = ruleStatus,
      tags = ruleTags,
      transition = transitionValue,
      expiration = expirationValue,
      noncurrentVersionTransition = nonCurrentTransitionValue,
      noncurrentVersionExpiration = nonCurrentExpirationValue,
      abortIncompleteMultipartUpload = abortMultipart,
    )
  }

  private fun CorsRule.toMinio(): CORSConfiguration.CORSRule {
    val allowedMethodStrings = allowedMethods.map { it.name }
    return CORSConfiguration.CORSRule(
      allowedHeaders.ifEmpty { null },
      allowedMethodStrings.ifEmpty { null },
      allowedOrigins.ifEmpty { null },
      exposeHeaders.ifEmpty { null },
      id,
      maxAgeSeconds,
    )
  }

  private fun CORSConfiguration.CORSRule.toComposeServer(): CorsRule {
    val methods = allowedMethods()?.mapNotNull { method -> runCatching { HttpMethod.valueOf(method) }.getOrNull() } ?: emptyList()
    return CorsRule(
      id = id(),
      allowedOrigins = allowedOrigins() ?: emptyList(),
      allowedMethods = methods,
      allowedHeaders = allowedHeaders() ?: emptyList(),
      exposeHeaders = exposeHeaders() ?: emptyList(),
      maxAgeSeconds = maxAgeSeconds(),
    )
  }
}
