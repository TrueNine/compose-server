package io.github.truenine.composeserver.oss.volcengine

import com.volcengine.tos.TOSV2
import com.volcengine.tos.comm.common.ACLType
import com.volcengine.tos.comm.common.MetadataDirectiveType
import com.volcengine.tos.model.bucket.CreateBucketV2Input
import com.volcengine.tos.model.bucket.DeleteBucketInput
import com.volcengine.tos.model.bucket.GetBucketPolicyInput
import com.volcengine.tos.model.bucket.HeadBucketV2Input
import com.volcengine.tos.model.bucket.ListBucketsV2Input
import com.volcengine.tos.model.bucket.PutBucketACLInput
import com.volcengine.tos.model.bucket.PutBucketPolicyInput
import com.volcengine.tos.model.`object`.CopyObjectV2Input
import com.volcengine.tos.model.`object`.DeleteObjectInput
import com.volcengine.tos.model.`object`.GetObjectV2Input
import com.volcengine.tos.model.`object`.HeadObjectV2Input
import com.volcengine.tos.model.`object`.ListObjectsType2Input
import com.volcengine.tos.model.`object`.ListObjectsV2Input
import com.volcengine.tos.model.`object`.PreSignedURLInput
import com.volcengine.tos.model.`object`.PutObjectBasicInput
import com.volcengine.tos.model.`object`.PutObjectInput
import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.mapFailure
import io.github.truenine.composeserver.onFailureDo
import io.github.truenine.composeserver.oss.AuthenticationException
import io.github.truenine.composeserver.oss.AuthorizationException
import io.github.truenine.composeserver.oss.BucketAlreadyExistsException
import io.github.truenine.composeserver.oss.BucketInfo as OssBucketInfo
import io.github.truenine.composeserver.oss.BucketNotEmptyException
import io.github.truenine.composeserver.oss.BucketNotFoundException
import io.github.truenine.composeserver.oss.CompleteMultipartUploadRequest
import io.github.truenine.composeserver.oss.ConfigurationException
import io.github.truenine.composeserver.oss.CopyObjectRequest
import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.DeleteResult
import io.github.truenine.composeserver.oss.InitiateMultipartUploadRequest
import io.github.truenine.composeserver.oss.InvalidRequestException
import io.github.truenine.composeserver.oss.ListObjectsRequest
import io.github.truenine.composeserver.oss.MultipartUpload
import io.github.truenine.composeserver.oss.NetworkException
import io.github.truenine.composeserver.oss.ObjectContent
import io.github.truenine.composeserver.oss.ObjectInfo
import io.github.truenine.composeserver.oss.ObjectListing
import io.github.truenine.composeserver.oss.ObjectNotFoundException
import io.github.truenine.composeserver.oss.ObjectStorageException
import io.github.truenine.composeserver.oss.ObjectStorageService
import io.github.truenine.composeserver.oss.PartInfo
import io.github.truenine.composeserver.oss.PutObjectRequest
import io.github.truenine.composeserver.oss.QuotaExceededException
import io.github.truenine.composeserver.oss.ServiceUnavailableException
import io.github.truenine.composeserver.oss.ShareLinkInfo
import io.github.truenine.composeserver.oss.ShareLinkRequest
import io.github.truenine.composeserver.oss.StorageClass
import io.github.truenine.composeserver.oss.UploadPartRequest
import io.github.truenine.composeserver.oss.UploadWithLinkRequest
import io.github.truenine.composeserver.oss.UploadWithLinkResponse
import io.github.truenine.composeserver.safeCallAsync
import java.io.InputStream
import java.net.URI
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Volcengine TOS implementation of ObjectStorageService
 *
 * @param tosClient The native TOS client
 * @param exposedBaseUrl The exposed base URL for public access
 * @author TrueNine
 * @since 2025-08-05
 */
class VolcengineTosObjectStorageService(private val tosClient: TOSV2, override val exposedBaseUrl: String) : ObjectStorageService {

  companion object {
    @JvmStatic private val log = logger<VolcengineTosObjectStorageService>()
  }

  override suspend fun isHealthy(): Boolean {
    val result = safeCallAsync { tosClient.listBuckets(ListBucketsV2Input()) }
    return result.fold(
      onSuccess = {
        log.debug("TOS health check passed")
        true
      },
      onFailure = { e ->
        log.error("TOS health check failed", e)
        false
      },
    )
  }

  override suspend fun createBucket(request: CreateBucketRequest): Result<OssBucketInfo> {
    return safeCallAsync {
        val input = CreateBucketV2Input().setBucket(request.bucketName)
        tosClient.createBucket(input)
        log.info("Created bucket: {}", request.bucketName)

        OssBucketInfo(
          name = request.bucketName,
          creationDate = Instant.now(),
          region = request.region,
          storageClass = request.storageClass,
          versioningEnabled = request.enableVersioning,
          tags = request.tags,
        )
      }
      .onFailureDo { e -> log.error("Failed to create bucket: {}", request.bucketName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun deleteBucket(bucketName: String): Result<Unit> {
    return safeCallAsync {
        val input = DeleteBucketInput().setBucket(bucketName)
        tosClient.deleteBucket(input)
        log.info("Deleted bucket: {}", bucketName)
      }
      .onFailureDo { e -> log.error("Failed to delete bucket: {}", bucketName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun bucketExists(bucketName: String): Result<Boolean> {
    return safeCallAsync {
        val input = HeadBucketV2Input().setBucket(bucketName)
        tosClient.headBucket(input)
        true
      }
      .onFailureDo { e -> log.debug("Bucket existence check failed for: {}", bucketName, e) }
      .recover { e ->
        // For bucket existence check, we want to return false for most errors
        // as they typically indicate the bucket doesn't exist or isn't accessible
        val mappedException = mapTosException(e as? Exception ?: Exception(e))
        when (mappedException) {
          is BucketNotFoundException -> false
          is AuthorizationException -> throw mappedException // Re-throw auth errors
          is AuthenticationException -> throw mappedException // Re-throw auth errors
          else -> false // For other errors, assume bucket doesn't exist
        }
      }
  }

  override suspend fun setBucketPublicRead(bucketName: String): Result<Unit> {
    return safeCallAsync {
        // 使用 TOS SDK 设置存储桶为公共读取
        val input = PutBucketACLInput().setBucket(bucketName).setAcl(ACLType.ACL_PUBLIC_READ)
        tosClient.putBucketACL(input)
        log.info("Set bucket public read: {}", bucketName)
      }
      .onFailureDo { e -> log.error("Failed to set bucket public read: {}", bucketName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun getBucketPolicy(bucketName: String): Result<String> =
    withContext(Dispatchers.IO) {
      val input = GetBucketPolicyInput().setBucket(bucketName)
      val output = tosClient.getBucketPolicy(input)
      Result.success(output.policy ?: "")
    }

  override suspend fun setBucketPolicy(bucketName: String, policy: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      val input = PutBucketPolicyInput().setBucket(bucketName).setPolicy(policy)

      tosClient.putBucketPolicy(input)
      log.info("Set bucket policy: {}", bucketName)
      Result.success(Unit)
    }

  override suspend fun listBuckets(): Result<List<OssBucketInfo>> {
    return safeCallAsync {
        val output = tosClient.listBuckets(ListBucketsV2Input())
        output.buckets?.map { bucket ->
          OssBucketInfo(
            name = bucket.name ?: "",
            creationDate = convertDateToInstant(bucket.creationDate),
            region = bucket.location,
            storageClass = StorageClass.STANDARD,
            versioningEnabled = false,
            tags = emptyMap(),
          )
        } ?: emptyList()
      }
      .onFailureDo { e -> log.error("Failed to list buckets", e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun putObject(request: PutObjectRequest): Result<ObjectInfo> {
    return safeCallAsync {
        // 使用已弃用的 setPutObjectBasicInput，但添加 @Suppress 注解
        val basicInput = PutObjectBasicInput().setBucket(request.bucketName).setKey(request.objectName)
        @Suppress("DEPRECATION") val input = PutObjectInput().setPutObjectBasicInput(basicInput).setContent(request.inputStream)

        val output = tosClient.putObject(input)
        log.info("Uploaded object: {}/{}", request.bucketName, request.objectName)

        ObjectInfo(
          bucketName = request.bucketName,
          objectName = request.objectName,
          size = request.size,
          etag = output.etag ?: "",
          lastModified = Instant.now(),
          contentType = request.contentType,
          metadata = request.metadata,
          storageClass = request.storageClass,
          tags = request.tags,
        )
      }
      .onFailureDo { e -> log.error("Failed to upload object: {}/{}", request.bucketName, request.objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
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

  override suspend fun getObject(bucketName: String, objectName: String): Result<ObjectContent> {
    return safeCallAsync {
        val input = GetObjectV2Input().setBucket(bucketName).setKey(objectName)
        val output = tosClient.getObject(input)

        ObjectContent(
          objectInfo =
            ObjectInfo(
              bucketName = bucketName,
              objectName = objectName,
              size = output.contentLength,
              etag = output.etag ?: "",
              lastModified = convertDateToInstant(output.lastModified),
              contentType = output.contentType,
              metadata = emptyMap(),
              storageClass = StorageClass.STANDARD,
              tags = emptyMap(),
            ),
          inputStream = output.content,
        )
      }
      .onFailureDo { e -> log.error("Failed to get object: {}/{}", bucketName, objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun getObject(bucketName: String, objectName: String, offset: Long, length: Long): Result<ObjectContent> {
    return safeCallAsync {
        val input = GetObjectV2Input().setBucket(bucketName).setKey(objectName).setRange("bytes=$offset-${offset + length - 1}")
        val output = tosClient.getObject(input)

        ObjectContent(
          objectInfo =
            ObjectInfo(
              bucketName = bucketName,
              objectName = objectName,
              size = output.contentLength,
              etag = output.etag ?: "",
              lastModified = convertDateToInstant(output.lastModified),
              contentType = output.contentType,
              metadata = emptyMap(),
              storageClass = StorageClass.STANDARD,
              tags = emptyMap(),
            ),
          inputStream = output.content,
        )
      }
      .onFailureDo { e -> log.error("Failed to get object range: {}/{} (offset: {}, length: {})", bucketName, objectName, offset, length, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun getObjectInfo(bucketName: String, objectName: String): Result<ObjectInfo> {
    return safeCallAsync {
        val input = HeadObjectV2Input().setBucket(bucketName).setKey(objectName)
        val output = tosClient.headObject(input)

        ObjectInfo(
          bucketName = bucketName,
          objectName = objectName,
          size = output.contentLength,
          etag = output.etag ?: "",
          lastModified = convertDateToInstant(output.lastModified),
          contentType = output.contentType,
          metadata = emptyMap(), // TODO: 获取正确的元数据
          storageClass = StorageClass.STANDARD, // TODO: 映射 TOS 的存储类型
          tags = emptyMap(),
        )
      }
      .onFailureDo { e -> log.error("Failed to get object info: {}/{}", bucketName, objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun objectExists(bucketName: String, objectName: String): Result<Boolean> {
    return safeCallAsync {
        val input = HeadObjectV2Input().setBucket(bucketName).setKey(objectName)
        tosClient.headObject(input)
        true
      }
      .onFailureDo { e -> log.debug("Object existence check failed for: {}/{}", bucketName, objectName, e) }
      .recover { e ->
        // For object existence check, we want to return false for "not found" errors
        val mappedException = mapTosException(e as? Exception ?: Exception(e))
        if (mappedException is ObjectNotFoundException) {
          false
        } else {
          throw mappedException
        }
      }
  }

  override suspend fun deleteObject(bucketName: String, objectName: String): Result<Unit> {
    return safeCallAsync {
        val input = DeleteObjectInput().setBucket(bucketName).setKey(objectName)
        tosClient.deleteObject(input)
        log.info("Deleted object: {}/{}", bucketName, objectName)
      }
      .onFailureDo { e -> log.error("Failed to delete object: {}/{}", bucketName, objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun deleteObjects(bucketName: String, objectNames: List<String>): Result<List<DeleteResult>> =
    withContext(Dispatchers.IO) {
      val results = mutableListOf<DeleteResult>()

      // 使用单个删除的方式，因为批量删除 API 可能有问题
      for (objectName in objectNames) {
        val input = DeleteObjectInput().setBucket(bucketName).setKey(objectName)
        tosClient.deleteObject(input)
        results.add(DeleteResult(objectName = objectName, success = true, errorMessage = null))
      }

      log.info("Deleted {} objects from bucket: {}", results.count { it.success }, bucketName)
      Result.success(results)
    }

  override suspend fun copyObject(request: CopyObjectRequest): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      // 尝试使用 TOS SDK 的复制对象 API
      val input =
        CopyObjectV2Input()
          .setBucket(request.destinationBucketName)
          .setKey(request.destinationObjectName)
          .setSrcBucket(request.sourceBucketName)
          .setSrcKey(request.sourceObjectName)

      // 设置元数据
      if (request.metadata.isNotEmpty()) {
        input.setMetadataDirective(MetadataDirectiveType.METADATA_DIRECTIVE_REPLACE)
        // TODO: 设置自定义元数据，需要找到正确的方法
        // input.setMeta(request.metadata)
      }

      val output = tosClient.copyObject(input)
      log.info(
        "Copied object: {}/{} -> {}/{}",
        request.sourceBucketName,
        request.sourceObjectName,
        request.destinationBucketName,
        request.destinationObjectName,
      )

      Result.success(
        ObjectInfo(
          bucketName = request.destinationBucketName,
          objectName = request.destinationObjectName,
          size = 0L, // TOS copyObject 可能不返回大小信息
          etag = output.etag ?: "",
          lastModified = convertDateToInstant(output.lastModified),
          contentType = null,
          metadata = request.metadata,
          storageClass = StorageClass.STANDARD,
          tags = emptyMap(),
        )
      )
    }

  override suspend fun listObjects(request: ListObjectsRequest): Result<ObjectListing> {
    return safeCallAsync {
        val input = ListObjectsV2Input().setBucket(request.bucketName).setMaxKeys(request.maxKeys)

        // 设置可选参数
        request.prefix?.let { input.setPrefix(it) }
        request.delimiter?.let { input.setDelimiter(it) }
        // TODO: 检查 TOS SDK 是否支持 continuation token 和 start after
        // request.continuationToken?.let { input.setContinuationToken(it) }
        // request.startAfter?.let { input.setStartAfter(it) }

        @Suppress("DEPRECATION") val output = tosClient.listObjects(input)

        val objects =
          output.contents?.map { obj ->
            ObjectInfo(
              bucketName = request.bucketName,
              objectName = obj.key ?: "",
              size = obj.size,
              etag = obj.etag ?: "",
              lastModified = convertDateToInstant(obj.lastModified),
              contentType = null, // TOS listObjects 不返回 contentType
              metadata = emptyMap(),
              storageClass = StorageClass.STANDARD, // TODO: 映射 TOS 的存储类型
              tags = emptyMap(),
            )
          } ?: emptyList()

        val commonPrefixes = output.commonPrefixes?.map { it.prefix ?: "" } ?: emptyList()

        ObjectListing(
          bucketName = request.bucketName,
          objects = objects,
          commonPrefixes = commonPrefixes,
          isTruncated = output.isTruncated,
          nextContinuationToken = null, // TODO: 检查 TOS SDK 是否支持 continuation token
          maxKeys = request.maxKeys,
          prefix = request.prefix,
          delimiter = request.delimiter,
        )
      }
      .onFailureDo { e -> log.error("Failed to list objects in bucket: {}", request.bucketName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override fun listObjectsFlow(request: ListObjectsRequest): Flow<ObjectInfo> = flow {
    // TOS SDK 当前版本不支持 continuation token，所以只执行一次查询
    val input = ListObjectsType2Input().setBucket(request.bucketName).setMaxKeys(request.maxKeys)

    // 设置可选参数
    request.prefix?.let { input.setPrefix(it) }
    request.delimiter?.let { input.setDelimiter(it) }
    val output = tosClient.listObjectsType2(input)

    // 发出所有对象
    output.contents?.forEach { obj ->
      emit(
        ObjectInfo(
          bucketName = request.bucketName,
          objectName = obj.key ?: "",
          size = obj.size,
          etag = obj.etag ?: "",
          lastModified = convertDateToInstant(obj.lastModified),
          contentType = null,
          metadata = emptyMap(),
          storageClass = StorageClass.STANDARD,
          tags = emptyMap(),
        )
      )
    }
  }

  override suspend fun generatePresignedUrl(bucketName: String, objectName: String, expiration: Duration, method: HttpMethod): Result<String> {
    return safeCallAsync {
        // 映射 HttpMethod 到 TOS SDK 的字符串
        val tosMethod =
          when (method) {
            HttpMethod.GET -> "GET"
            HttpMethod.PUT -> "PUT"
            HttpMethod.POST -> "POST"
            HttpMethod.DELETE -> "DELETE"
            HttpMethod.HEAD -> "HEAD"
            HttpMethod.PATCH -> "POST" // TOS 不支持 PATCH，使用 POST
            HttpMethod.OPTIONS -> "GET" // TOS 不支持 OPTIONS，使用 GET
            HttpMethod.TRACE -> "GET" // TOS 不支持 TRACE，使用 GET
            HttpMethod.CONNECT -> "GET" // TOS 不支持 CONNECT，使用 GET
          }

        val input = PreSignedURLInput().setBucket(bucketName).setKey(objectName).setHttpMethod(tosMethod).setExpires(expiration.seconds)
        val output = tosClient.preSignedURL(input)
        val presignedUrl = output.signedUrl ?: throw IllegalStateException("TOS SDK returned null presigned URL")

        log.info("Generated presigned URL for: {}/{} (expires in {}s)", bucketName, objectName, expiration.seconds)
        presignedUrl
      }
      .onFailureDo { e -> log.error("Failed to generate presigned URL: {}/{}", bucketName, objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun initiateMultipartUpload(request: InitiateMultipartUploadRequest): Result<MultipartUpload> {
    return safeCallAsync {
        log.debug("Initiating multipart upload: {}/{}", request.bucketName, request.objectName)

        val input = com.volcengine.tos.model.`object`.CreateMultipartUploadInput().setBucket(request.bucketName).setKey(request.objectName)

        // TODO: 添加内容类型和元数据设置支持
        // request.contentType?.let { input.setContentType(it) }
        // if (request.metadata.isNotEmpty()) {
        //   input.setMeta(request.metadata)
        // }

        val output = tosClient.createMultipartUpload(input)
        val uploadId = output.uploadID ?: throw IllegalStateException("TOS SDK returned null upload ID")

        log.info("Initiated multipart upload: {}/{}, uploadId: {}", request.bucketName, request.objectName, uploadId)

        MultipartUpload(uploadId = uploadId, bucketName = request.bucketName, objectName = request.objectName)
      }
      .onFailureDo { e -> log.error("Failed to initiate multipart upload: {}/{}", request.bucketName, request.objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun uploadPart(request: UploadPartRequest): Result<PartInfo> {
    return safeCallAsync {
        log.debug("Uploading part {} for {}/{}, uploadId: {}", request.partNumber, request.bucketName, request.objectName, request.uploadId)

        val input =
          com.volcengine.tos.model.`object`
            .UploadPartV2Input()
            .setBucket(request.bucketName)
            .setKey(request.objectName)
            .setContentLength(request.size)
            .setPartNumber(request.partNumber)
            .setContent(request.inputStream)
            .setUploadID(request.uploadId)

        val output = tosClient.uploadPart(input)
        val etag = output.etag ?: throw IllegalStateException("TOS SDK returned null etag")

        log.info("Uploaded part {} for {}/{}, etag: {}", request.partNumber, request.bucketName, request.objectName, etag)

        PartInfo(partNumber = request.partNumber, etag = etag, size = request.size, lastModified = null)
      }
      .onFailureDo { e -> log.error("Failed to upload part {} for {}/{}", request.partNumber, request.bucketName, request.objectName, e) }
      .mapFailure { e -> mapTosException(e as? Exception ?: Exception(e)) }
  }

  override suspend fun completeMultipartUpload(request: CompleteMultipartUploadRequest): Result<ObjectInfo> =
    withContext(Dispatchers.IO) {
      log.debug("Completing multipart upload: {}/{}, uploadId: {}", request.bucketName, request.objectName, request.uploadId)

      // 创建 UploadedPartV2 列表，基于官方文档的正确实现
      val uploadedParts = request.parts.map { part -> com.volcengine.tos.model.`object`.UploadedPartV2().setPartNumber(part.partNumber).setEtag(part.etag) }

      val input =
        com.volcengine.tos.model.`object`
          .CompleteMultipartUploadV2Input()
          .setBucket(request.bucketName)
          .setKey(request.objectName)
          .setUploadID(request.uploadId)
      input.setUploadedParts(uploadedParts)

      val output = tosClient.completeMultipartUpload(input)
      val etag = output.etag ?: "unknown-etag"

      log.info("Completed multipart upload: {}/{}, etag: {}", request.bucketName, request.objectName, etag)

      Result.success(
        ObjectInfo(
          bucketName = request.bucketName,
          objectName = request.objectName,
          size = request.parts.sumOf { it.size },
          etag = etag,
          lastModified = Instant.now(),
          contentType = null,
          metadata = emptyMap(),
          storageClass = StorageClass.STANDARD,
          tags = emptyMap(),
        )
      )
    }

  override suspend fun abortMultipartUpload(uploadId: String, bucketName: String, objectName: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      log.debug("Aborting multipart upload: {}/{}, uploadId: {}", bucketName, objectName, uploadId)

      val input = com.volcengine.tos.model.`object`.AbortMultipartUploadInput().setBucket(bucketName).setKey(objectName).setUploadID(uploadId)

      tosClient.abortMultipartUpload(input)

      log.info("Multipart upload aborted: {}/{}, uploadId: {}", bucketName, objectName, uploadId)

      Result.success(Unit)
    }

  override suspend fun listParts(uploadId: String, bucketName: String, objectName: String): Result<List<PartInfo>> =
    withContext(Dispatchers.IO) {
      log.debug("Listing parts for multipart upload: {}/{}, uploadId: {}", bucketName, objectName, uploadId)

      val input = com.volcengine.tos.model.`object`.ListPartsInput().setBucket(bucketName).setKey(objectName).setUploadID(uploadId)

      val output = tosClient.listParts(input)
      val parts =
        output.uploadedParts?.map { part ->
          PartInfo(partNumber = part.partNumber, etag = part.etag ?: "unknown-etag", size = part.size, lastModified = part.lastModified?.toInstant())
        } ?: emptyList()

      log.info("Listed {} parts for multipart upload: {}/{}", parts.size, bucketName, objectName)

      Result.success(parts)
    }

  override suspend fun generateShareLink(request: ShareLinkRequest): Result<ShareLinkInfo> =
    withContext(Dispatchers.IO) {
      // 使用预签名URL作为分享链接的基础
      val presignedUrlResult =
        generatePresignedUrl(bucketName = request.bucketName, objectName = request.objectName, expiration = request.expiration, method = request.method)

      presignedUrlResult.fold(
        onSuccess = { shareUrl ->
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

          log.info("Generated share link for: {}/{}", request.bucketName, request.objectName)
          Result.success(shareInfo)
        },
        onFailure = { error ->
          log.error("Error generating share link: {}/{}", request.bucketName, request.objectName, error)
          Result.failure(error)
        },
      )
    }

  override suspend fun uploadWithLink(request: UploadWithLinkRequest): Result<UploadWithLinkResponse> =
    withContext(Dispatchers.IO) {
      log.debug("Uploading object with share link: {}/{}", request.bucketName, request.objectName)

      // 首先上传对象
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
        throw uploadResult.exceptionOrNull()!!
      }

      val objectInfo = uploadResult.getOrThrow()

      // 生成分享链接
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
        throw shareLinkResult.exceptionOrNull()!!
      }

      val shareLink = shareLinkResult.getOrThrow()
      val publicUrl = objectInfo.getPublicUrl(exposedBaseUrl)

      val response = UploadWithLinkResponse(objectInfo = objectInfo, shareLink = shareLink, publicUrl = publicUrl)

      log.info("Uploaded object with share link: {}/{}", request.bucketName, request.objectName)
      Result.success(response)
    }

  override suspend fun downloadFromShareLink(shareUrl: String, password: String?): Result<ObjectContent> =
    withContext(Dispatchers.IO) {
      log.debug("Downloading from share link: {}", shareUrl)

      // 对于 TOS 预签名 URL，我们可以直接使用 URL 进行下载
      // 这里简化实现，假设 shareUrl 就是有效的预签名 URL
      // 在实际应用中，可能需要额外的验证逻辑（如密码验证、下载次数限制等）

      if (password != null) {
        log.warn("Password validation not implemented for TOS presigned URLs")
      }

      // 解析 URL 以获取 bucket 和 object 信息
      val (bucketName, objectName) = parseShareUrl(shareUrl)

      // 使用常规的 getObject 方法
      val result = getObject(bucketName, objectName)

      if (result.isSuccess) {
        log.info("Downloaded from share link: {}/{}", bucketName, objectName)
      } else {
        log.error("Failed to download from share link: {}", shareUrl)
      }

      result
    }

  override suspend fun validateShareLink(shareUrl: String, password: String?): Result<ShareLinkInfo> =
    withContext(Dispatchers.IO) {
      log.debug("Validating share link: {}", shareUrl)

      // 对于 TOS 预签名 URL，验证主要是检查 URL 是否有效以及是否过期
      // 这里提供一个基础的实现

      if (password != null) {
        log.warn("Password validation not implemented for TOS presigned URLs")
      }

      // 解析 URL 以获取基本信息
      val (bucketName, objectName) = parseShareUrl(shareUrl)

      // 检查对象是否存在（这也间接验证了 URL 的有效性）
      val existsResult = objectExists(bucketName, objectName)
      if (existsResult.isFailure) {
        throw existsResult.exceptionOrNull()!!
      }

      val exists = existsResult.getOrThrow()
      if (!exists) {
        throw ObjectNotFoundException(bucketName, objectName)
      }

      // 创建 ShareLinkInfo（注意：某些信息可能无法从 URL 中准确获取）
      val shareInfo =
        ShareLinkInfo(
          shareUrl = shareUrl,
          bucketName = bucketName,
          objectName = objectName,
          expiration = Instant.now().plusSeconds(3600), // 默认1小时，实际应该从 URL 参数中解析
          method = HttpMethod.GET, // 默认 GET，实际应该从 URL 或上下文中获取
          allowedIps = emptyList(),
          maxDownloads = null,
          remainingDownloads = null,
          hasPassword = password != null,
          metadata = emptyMap(),
        )

      log.info("Share link validated: {}/{}", bucketName, objectName)
      Result.success(shareInfo)
    }

  override suspend fun revokeShareLink(shareUrl: String): Result<Unit> =
    withContext(Dispatchers.IO) {
      log.debug("Attempting to revoke share link: {}", shareUrl)

      // TOS 预签名 URL 本身无法被撤销，因为它们是基于时间的签名
      // 在实际应用中，可能需要以下几种方案：
      // 1. 维护一个黑名单数据库来记录被撤销的 URL
      // 2. 使用短期 URL 并通过代理服务来控制访问
      // 3. 删除或移动原始对象（这会使所有相关的预签名 URL 失效）

      log.warn("TOS presigned URLs cannot be directly revoked")
      log.warn("Consider implementing a blacklist mechanism or using short-lived URLs")

      // 这里我们提供一个警告性的成功响应，表示操作已被记录但可能无法立即生效
      log.info("Share link revocation request recorded: {}", shareUrl)
      log.info("Note: The URL may remain accessible until its natural expiration")

      Result.success(Unit)
    }

  override fun <T : Any> getNativeClient(): T? {
    @Suppress("UNCHECKED_CAST")
    return tosClient as? T
  }

  /** Convert TOS SDK date value to Instant Handles both Date objects and RFC 2822 date strings */
  private fun convertDateToInstant(dateValue: Any?): Instant {
    return dateValue?.let { date ->
      log.debug("Date value type: {}, value: {}", date::class.java.name, date)
      when (date) {
        is String -> {
          // Try multiple date formats
          when {
            // RFC 1123 format like "Fri, 30 Jul 2021 08:05:36 GMT"
            date.contains(',') -> {
              val formatter = java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
              java.time.ZonedDateTime.parse(date, formatter).toInstant()
            }
            // ISO 8601 format like "2025-06-19T07:01:46.000Z"
            date.contains('T') && date.endsWith('Z') -> {
              Instant.parse(date)
            }

            else -> {
              // Try ISO 8601 as fallback
              Instant.parse(date)
            }
          }
        }

        else -> {
          // Try reflection for Date objects
          val timeMethod = date::class.java.getMethod("getTime")
          val timeMillis = timeMethod.invoke(date) as Long
          Instant.ofEpochMilli(timeMillis)
        }
      }
    } ?: Instant.now()
  }

  /** Map TOS SDK exceptions to our unified exception hierarchy */
  private fun mapTosException(e: Exception): ObjectStorageException {
    return when (e) {
      // TOS SDK specific exceptions
      is com.volcengine.tos.TosException -> {
        val errorCode = e.code
        val errorMessage = e.message ?: "Unknown TOS error"
        val statusCode = e.statusCode

        log.debug("TOS exception - Code: {}, Status: {}, Message: {}", errorCode, statusCode, errorMessage)

        when (errorCode) {
          "NoSuchBucket" -> BucketNotFoundException(extractBucketName(errorMessage), e)
          "BucketAlreadyExists",
          "BucketAlreadyOwnedByYou" -> BucketAlreadyExistsException(extractBucketName(errorMessage), e)

          "BucketNotEmpty" -> BucketNotEmptyException(extractBucketName(errorMessage), e)
          "NoSuchKey" -> ObjectNotFoundException(extractBucketName(errorMessage), extractObjectName(errorMessage), e)
          "AccessDenied" -> AuthorizationException("Access denied: $errorMessage", e)
          "InvalidAccessKeyId",
          "SignatureDoesNotMatch" -> AuthenticationException("Authentication failed: $errorMessage", e)

          "InvalidArgument",
          "InvalidRequest" -> InvalidRequestException("Invalid request: $errorMessage", e)

          "ServiceUnavailable" -> ServiceUnavailableException("TOS service unavailable: $errorMessage", e)
          "InternalError" -> ObjectStorageException("TOS internal error: $errorMessage", e)
          "RequestTimeout" -> NetworkException("Request timeout: $errorMessage", e)
          "SlowDown" -> QuotaExceededException("Rate limit exceeded: $errorMessage", e)
          else -> ObjectStorageException("TOS operation failed: $errorMessage (code: $errorCode)", e)
        }
      }

      // Network and IO exceptions
      is java.net.SocketTimeoutException -> NetworkException("Socket timeout", e)
      is java.net.ConnectException -> NetworkException("Connection failed", e)
      is java.net.UnknownHostException -> NetworkException("Unknown host", e)
      is java.io.IOException -> NetworkException("Network error: ${e.message}", e)

      // Security exceptions
      is java.security.InvalidKeyException -> AuthenticationException("Invalid key: ${e.message}", e)
      is java.security.NoSuchAlgorithmException -> ConfigurationException("Algorithm not supported: ${e.message}", e)

      // Generic exceptions
      is IllegalArgumentException -> InvalidRequestException("Invalid argument: ${e.message}", e)
      is IllegalStateException -> ObjectStorageException("Invalid state: ${e.message}", e)

      else -> ObjectStorageException("TOS operation failed: ${e.message}", e)
    }
  }

  /** Extract bucket name from error message */
  private fun extractBucketName(errorMessage: String): String {
    // Try to extract bucket name from common error message patterns
    val bucketPattern = Regex("bucket[\\s:]+([a-zA-Z0-9.-]+)", RegexOption.IGNORE_CASE)
    return bucketPattern.find(errorMessage)?.groupValues?.get(1) ?: "unknown"
  }

  /** Extract object name from error message */
  private fun extractObjectName(errorMessage: String): String {
    // Try to extract object name from common error message patterns
    val objectPattern = Regex("object[\\s:]+(\\S+)", RegexOption.IGNORE_CASE)
    return objectPattern.find(errorMessage)?.groupValues?.get(1) ?: "unknown"
  }

  /** Parse TOS share URL to extract bucket and object information */
  private fun parseShareUrl(shareUrl: String): Pair<String, String> {
    try {
      val uri = URI(shareUrl)
      val host = uri.host ?: throw IllegalArgumentException("Invalid URL: missing host")
      val path = uri.path ?: throw IllegalArgumentException("Invalid URL: missing path")

      // Extract bucket name from host (assuming format: bucket.tos.domain.com)
      val hostParts = host.split(".")
      val bucketName = hostParts.firstOrNull() ?: throw IllegalArgumentException("Invalid URL: cannot extract bucket from host")

      // Extract object name from path (remove leading slash)
      val objectName = path.removePrefix("/").takeIf { it.isNotEmpty() } ?: throw IllegalArgumentException("Invalid URL: empty object name")

      log.debug("Parsed share URL - bucket: {}, object: {}", bucketName, objectName)
      return bucketName to objectName
    } catch (e: Exception) {
      log.error("Failed to parse share URL: {}", shareUrl, e)
      throw IllegalArgumentException("Invalid share URL format: ${e.message}", e)
    }
  }
}
