package io.github.truenine.composeserver.oss.volcengine

import com.volcengine.tos.TOSV2
import com.volcengine.tos.comm.common.*
import com.volcengine.tos.model.bucket.*
import com.volcengine.tos.model.bucket.CORSRule as TosCorsRule
import com.volcengine.tos.model.bucket.Expiration as TosExpiration
import com.volcengine.tos.model.bucket.LifecycleRule as TosLifecycleRule
import com.volcengine.tos.model.bucket.Tag as TosTag
import com.volcengine.tos.model.`object`.*
import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.*
import io.github.truenine.composeserver.oss.BucketInfo as OssBucketInfo
import io.github.truenine.composeserver.oss.LifecycleRule
import io.github.truenine.composeserver.oss.Tag
import java.io.InputStream
import java.net.URI
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class VolcengineTosObjectStorageService(private val tosClient: TOSV2, override val exposedBaseUrl: String) : IObjectStorageService {
  companion object {
    private val log = logger<VolcengineTosObjectStorageService>()
  }

  override suspend fun isHealthy(): Boolean =
    withContext(Dispatchers.IO) {
      try {
        tosClient.listBuckets(ListBucketsV2Input())
        true
      } catch (e: Exception) {
        log.error("Volcengine TOS health check failed", e)
        false
      }
    }

  override fun <T : Any> getNativeClient(): T? = tosClient as? T

  override suspend fun createBucket(request: CreateBucketRequest): Result<OssBucketInfo> =
    execute("Failed to create bucket ${request.bucketName}") {
      tosClient.createBucket(CreateBucketV2Input().setBucket(request.bucketName))
      OssBucketInfo(
        name = request.bucketName,
        creationDate = Instant.now(),
        region = request.region,
        storageClass = request.storageClass,
        versioningEnabled = request.enableVersioning,
        tags = request.tags,
      )
    }

  override suspend fun deleteBucket(bucketName: String): Result<Unit> =
    execute("Failed to delete bucket $bucketName") {
      try {
        tosClient.deleteBucket(DeleteBucketInput().setBucket(bucketName))
      } catch (e: Exception) {
        // If the bucket doesn't exist, consider it a successful deletion
        val mapped = mapTosException(e)
        if (mapped is BucketNotFoundException) {
          // Successfully deleted a non-existent bucket
          return@execute Unit
        } else {
          // Re-throw other exceptions
          throw e
        }
      }
    }

  override suspend fun bucketExists(bucketName: String): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        tosClient.headBucket(HeadBucketV2Input().setBucket(bucketName))
        Result.success(true)
      } catch (e: Exception) {
        val mapped = mapTosException(e)
        when (mapped) {
          is BucketNotFoundException -> Result.success(false)
          is AuthorizationException,
          is AuthenticationException -> Result.failure(mapped)

          else -> Result.success(false)
        }
      }
    }

  override suspend fun setBucketPublicRead(bucketName: String): Result<Unit> =
    execute("Failed to set bucket $bucketName public read") {
      tosClient.putBucketACL(PutBucketACLInput().setBucket(bucketName).setAcl(ACLType.ACL_PUBLIC_READ))
    }

  override suspend fun getBucketPolicy(bucketName: String): Result<String> =
    withContext(Dispatchers.IO) {
      try {
        val output = tosClient.getBucketPolicy(GetBucketPolicyInput().setBucket(bucketName))
        Result.success(output.policy ?: "")
      } catch (e: Exception) {
        log.error("Failed to get bucket policy for $bucketName", e)
        Result.failure(mapTosException(e))
      }
    }

  override suspend fun setBucketPolicy(bucketName: String, policy: String): Result<Unit> =
    execute("Failed to set bucket policy for $bucketName") { tosClient.putBucketPolicy(PutBucketPolicyInput().setBucket(bucketName).setPolicy(policy)) }

  override suspend fun setBucketAccess(bucketName: String, accessLevel: BucketAccessLevel): Result<Unit> {
    val acl =
      when (accessLevel) {
        BucketAccessLevel.PUBLIC -> ACLType.ACL_PUBLIC_READ
        BucketAccessLevel.PRIVATE -> ACLType.ACL_PRIVATE
      }
    return execute("Failed to set bucket access for $bucketName") { tosClient.putBucketACL(PutBucketACLInput().setBucket(bucketName).setAcl(acl)) }
  }

  override suspend fun listBuckets(): Result<List<OssBucketInfo>> =
    execute("Failed to list buckets") {
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

  override suspend fun putObject(request: PutObjectRequest): Result<ObjectInfo> =
    execute("Failed to upload object ${request.bucketName}/${request.objectName}") {
      val basic = PutObjectBasicInput().setBucket(request.bucketName).setKey(request.objectName)
      @Suppress("DEPRECATION") val input = PutObjectInput().setPutObjectBasicInput(basic).setContent(request.inputStream)
      val response = tosClient.putObject(input)
      ObjectInfo(
        bucketName = request.bucketName,
        objectName = request.objectName,
        size = request.size,
        etag = response.etag ?: "",
        lastModified = Instant.now(),
        contentType = request.contentType,
        metadata = request.metadata,
        storageClass = request.storageClass,
        tags = request.tags,
      )
    }

  override suspend fun putObject(
    bucketName: String,
    objectName: String,
    inputStream: InputStream,
    size: Long,
    contentType: String?,
    metadata: Map<String, String>,
  ): Result<ObjectInfo> = putObject(PutObjectRequest(bucketName, objectName, inputStream, size, contentType, metadata))

  override suspend fun getObjectInfo(bucketName: String, objectName: String): Result<ObjectInfo> =
    execute("Failed to stat object $bucketName/$objectName") {
      val output = tosClient.headObject(HeadObjectV2Input().setBucket(bucketName).setKey(objectName))
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
      )
    }

  override suspend fun getObject(bucketName: String, objectName: String): Result<ObjectContent> =
    execute("Failed to download object $bucketName/$objectName") {
      val output = tosClient.getObject(GetObjectV2Input().setBucket(bucketName).setKey(objectName))
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

  override suspend fun getObject(bucketName: String, objectName: String, offset: Long, length: Long): Result<ObjectContent> =
    execute("Failed to download range for $bucketName/$objectName") {
      val range = "bytes=$offset-${offset + length - 1}"
      val output = tosClient.getObject(GetObjectV2Input().setBucket(bucketName).setKey(objectName).setRange(range))
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

  override suspend fun objectExists(bucketName: String, objectName: String): Result<Boolean> =
    withContext(Dispatchers.IO) {
      try {
        tosClient.headObject(HeadObjectV2Input().setBucket(bucketName).setKey(objectName))
        Result.success(true)
      } catch (e: Exception) {
        val mapped = mapTosException(e)
        when (mapped) {
          is ObjectNotFoundException -> Result.success(false)
          else -> Result.failure(mapped)
        }
      }
    }

  override suspend fun deleteObject(bucketName: String, objectName: String): Result<Unit> =
    execute("Failed to delete object $bucketName/$objectName") { tosClient.deleteObject(DeleteObjectInput().setBucket(bucketName).setKey(objectName)) }

  override suspend fun deleteObjects(bucketName: String, objectNames: List<String>): Result<List<DeleteResult>> =
    execute("Failed to delete objects in $bucketName") {
      val results = mutableListOf<DeleteResult>()
      objectNames.forEach { key ->
        try {
          tosClient.deleteObject(DeleteObjectInput().setBucket(bucketName).setKey(key))
          results += DeleteResult(objectName = key, success = true, errorMessage = null)
        } catch (e: Exception) {
          val mapped = mapTosException(e)
          log.error("Failed to delete object {} from {}", key, bucketName, mapped)
          results += DeleteResult(objectName = key, success = false, errorMessage = mapped.message)
        }
      }
      results
    }

  override suspend fun copyObject(request: CopyObjectRequest): Result<ObjectInfo> =
    execute("Failed to copy object ${request.sourceBucketName}/${request.sourceObjectName}") {
      val input =
        CopyObjectV2Input()
          .setBucket(request.destinationBucketName)
          .setKey(request.destinationObjectName)
          .setSrcBucket(request.sourceBucketName)
          .setSrcKey(request.sourceObjectName)
      val output = tosClient.copyObject(input)
      ObjectInfo(
        bucketName = request.destinationBucketName,
        objectName = request.destinationObjectName,
        size = 0,
        etag = output.etag ?: "",
        lastModified = convertDateToInstant(output.lastModified),
        contentType = null,
        metadata = request.metadata,
        storageClass = request.storageClass,
        tags = request.tags,
      )
    }

  override suspend fun listObjects(request: ListObjectsRequest): Result<ObjectListing> =
    execute("Failed to list objects in ${request.bucketName}") {
      val input = ListObjectsV2Input().setBucket(request.bucketName).setMaxKeys(request.maxKeys)
      request.prefix?.let { input.setPrefix(it) }
      request.delimiter?.let { input.setDelimiter(it) }
      @Suppress("DEPRECATION") val output = tosClient.listObjects(input)
      val objects =
        output.contents?.map { obj ->
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
        } ?: emptyList()
      val prefixes = output.commonPrefixes?.map { it.prefix ?: "" } ?: emptyList()
      ObjectListing(
        bucketName = request.bucketName,
        objects = objects,
        commonPrefixes = prefixes,
        isTruncated = output.isTruncated,
        nextContinuationToken = null,
        maxKeys = request.maxKeys,
        prefix = request.prefix,
        delimiter = request.delimiter,
      )
    }

  override fun listObjectsFlow(request: ListObjectsRequest): Flow<ObjectInfo> = flow {
    val input = ListObjectsType2Input().setBucket(request.bucketName).setMaxKeys(request.maxKeys)
    request.prefix?.let { input.setPrefix(it) }
    request.delimiter?.let { input.setDelimiter(it) }
    val output = tosClient.listObjectsType2(input)
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

  override suspend fun generatePresignedUrl(bucketName: String, objectName: String, expiration: Duration, method: HttpMethod): Result<String> =
    execute("Failed to generate presigned URL for $bucketName/$objectName") {
      val tosMethod =
        when (method) {
          HttpMethod.GET -> "GET"
          HttpMethod.PUT -> "PUT"
          HttpMethod.POST -> "POST"
          HttpMethod.DELETE -> "DELETE"
          HttpMethod.HEAD -> "HEAD"
          HttpMethod.PATCH,
          HttpMethod.OPTIONS,
          HttpMethod.TRACE,
          HttpMethod.CONNECT -> "GET"
        }
      val input = PreSignedURLInput().setBucket(bucketName).setKey(objectName).setHttpMethod(tosMethod).setExpires(expiration.seconds)
      val output = tosClient.preSignedURL(input)
      output.signedUrl ?: throw IllegalStateException("TOS returned null presigned URL")
    }

  override suspend fun initiateMultipartUpload(request: InitiateMultipartUploadRequest): Result<MultipartUpload> =
    execute("Failed to initiate multipart upload for ${request.bucketName}/${request.objectName}") {
      val input = CreateMultipartUploadInput().setBucket(request.bucketName).setKey(request.objectName)
      val output = tosClient.createMultipartUpload(input)
      val uploadId = output.uploadID ?: throw IllegalStateException("TOS returned null upload ID")
      MultipartUpload(uploadId = uploadId, bucketName = request.bucketName, objectName = request.objectName)
    }

  override suspend fun uploadPart(request: UploadPartRequest): Result<PartInfo> =
    execute("Failed to upload part ${request.partNumber} for ${request.bucketName}/${request.objectName}") {
      val input =
        UploadPartV2Input()
          .setBucket(request.bucketName)
          .setKey(request.objectName)
          .setUploadID(request.uploadId)
          .setPartNumber(request.partNumber)
          .setContentLength(request.size)
          .setContent(request.inputStream)
      val output = tosClient.uploadPart(input)
      val etag = output.etag ?: throw IllegalStateException("TOS returned null etag")
      PartInfo(partNumber = request.partNumber, etag = etag, size = request.size, lastModified = null)
    }

  override suspend fun completeMultipartUpload(request: CompleteMultipartUploadRequest): Result<ObjectInfo> =
    execute("Failed to complete multipart upload for ${request.bucketName}/${request.objectName}") {
      val uploadedParts = request.parts.map { part -> UploadedPartV2().setPartNumber(part.partNumber).setEtag(part.etag) }
      val input =
        CompleteMultipartUploadV2Input().setBucket(request.bucketName).setKey(request.objectName).setUploadID(request.uploadId).setUploadedParts(uploadedParts)
      val output = tosClient.completeMultipartUpload(input)
      val etag = output.etag ?: "unknown-etag"
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
    }

  override suspend fun abortMultipartUpload(uploadId: String, bucketName: String, objectName: String): Result<Unit> =
    execute("Failed to abort multipart upload for $bucketName/$objectName") {
      val input = AbortMultipartUploadInput().setBucket(bucketName).setKey(objectName).setUploadID(uploadId)
      tosClient.abortMultipartUpload(input)
    }

  override suspend fun listParts(uploadId: String, bucketName: String, objectName: String): Result<List<PartInfo>> =
    execute("Failed to list multipart upload parts for $bucketName/$objectName") {
      val input = ListPartsInput().setBucket(bucketName).setKey(objectName).setUploadID(uploadId)
      val output = tosClient.listParts(input)
      output.uploadedParts?.map { part ->
        PartInfo(partNumber = part.partNumber, etag = part.etag ?: "", size = part.size, lastModified = part.lastModified?.toInstant())
      } ?: emptyList()
    }

  override suspend fun generateShareLink(request: ShareLinkRequest): Result<ShareLinkInfo> =
    generatePresignedUrl(request.bucketName, request.objectName, request.expiration, request.method).map { url ->
      ShareLinkInfo(
        shareUrl = url,
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
    }

  override suspend fun uploadWithLink(request: UploadWithLinkRequest): Result<UploadWithLinkResponse> {
    val putResult = putObject(request.bucketName, request.objectName, request.inputStream, request.size, request.contentType, request.metadata)
    if (putResult.isFailure) {
      return Result.failure(putResult.exceptionOrNull()!!)
    }
    val shareRequest =
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
    val shareResult = generateShareLink(shareRequest)
    if (shareResult.isFailure) {
      return Result.failure(shareResult.exceptionOrNull()!!)
    }
    val objectInfo = putResult.getOrThrow()
    val shareInfo = shareResult.getOrThrow()
    val response = UploadWithLinkResponse(objectInfo = objectInfo, shareLink = shareInfo, publicUrl = objectInfo.getPublicUrl(exposedBaseUrl))
    return Result.success(response)
  }

  override suspend fun downloadFromShareLink(shareUrl: String, password: String?): Result<ObjectContent> =
    try {
      val (bucket, key) = parseShareUrl(shareUrl)
      getObject(bucket, key)
    } catch (e: Exception) {
      Result.failure(mapTosException(e as? Exception ?: Exception(e)))
    }

  override suspend fun validateShareLink(shareUrl: String, password: String?): Result<ShareLinkInfo> =
    try {
      val (bucket, key) = parseShareUrl(shareUrl)
      val exists = objectExists(bucket, key).getOrElse { throw it }
      if (!exists) {
        Result.failure(ObjectNotFoundException(bucket, key))
      } else {
        Result.success(
          ShareLinkInfo(
            shareUrl = shareUrl,
            bucketName = bucket,
            objectName = key,
            expiration = Instant.now(),
            method = HttpMethod.GET,
            hasPassword = password != null,
          )
        )
      }
    } catch (e: Exception) {
      Result.failure(mapTosException(e as? Exception ?: Exception(e)))
    }

  override suspend fun revokeShareLink(shareUrl: String): Result<Unit> = Result.success(Unit)

  override suspend fun setObjectTags(bucketName: String, objectName: String, tags: List<Tag>): Result<Unit> = unsupported("object tagging")

  override suspend fun getObjectTags(bucketName: String, objectName: String): Result<List<Tag>> = unsupported("object tagging")

  override suspend fun deleteObjectTags(bucketName: String, objectName: String): Result<Unit> = unsupported("object tagging")

  override suspend fun setBucketTags(bucketName: String, tags: List<Tag>): Result<Unit> =
    execute("Failed to set bucket tags for $bucketName") {
      val tosTags = tags.map { TosTag().setKey(it.key).setValue(it.value) }
      val input = PutBucketTaggingInput().setBucket(bucketName).setTagSet(TagSet().setTags(tosTags))
      tosClient.putBucketTagging(input)
    }

  override suspend fun getBucketTags(bucketName: String): Result<List<Tag>> =
    withContext(Dispatchers.IO) {
      try {
        val input = GetBucketTaggingInput().setBucket(bucketName)
        val output = tosClient.getBucketTagging(input)
        Result.success(output.tagSet?.tags?.map { Tag(it.key, it.value) } ?: emptyList())
      } catch (e: com.volcengine.tos.TosException) {
        if (e.message?.contains("TagSet does not exist") == true) {
          Result.success(emptyList())
        } else {
          log.error("Failed to get bucket tags for $bucketName", e)
          Result.failure(mapTosException(e))
        }
      } catch (e: Exception) {
        log.error("Failed to get bucket tags for $bucketName", e)
        Result.failure(mapTosException(e))
      }
    }

  override suspend fun deleteBucketTags(bucketName: String): Result<Unit> =
    execute("Failed to delete bucket tags for $bucketName") {
      val input = DeleteBucketTaggingInput().setBucket(bucketName)
      tosClient.deleteBucketTagging(input)
    }

  override suspend fun setBucketVersioning(bucketName: String, enabled: Boolean): Result<Unit> =
    execute("Failed to set bucket versioning for $bucketName") {
      val status =
        if (enabled) {
          VersioningStatusType.VERSIONING_STATUS_ENABLED
        } else {
          VersioningStatusType.VERSIONING_STATUS_SUSPENDED
        }
      val input = PutBucketVersioningInput().setBucket(bucketName).setStatus(status)
      tosClient.putBucketVersioning(input)
    }

  override suspend fun listObjectVersions(request: ListObjectVersionsRequest): Result<ObjectVersionListing> =
    execute("Failed to list object versions in ${request.bucketName}") {
      val input = ListObjectVersionsV2Input().setBucket(request.bucketName).setMaxKeys(request.maxKeys)
      request.prefix?.let { input.setPrefix(it) }
      request.delimiter?.let { input.setDelimiter(it) }
      request.keyMarker?.let { input.setKeyMarker(it) }
      request.versionIdMarker?.let { input.setVersionIDMarker(it) }

      val output = tosClient.listObjectVersions(input)

      val versions =
        output.versions?.map { version ->
          ObjectVersionInfo(
            bucketName = request.bucketName,
            objectName = version.key,
            versionId = version.versionID,
            isLatest = version.isLatest,
            lastModified = convertDateToInstant(version.lastModified),
            etag = version.etag,
            size = version.size,
            storageClass = StorageClass.STANDARD,
            isDeleteMarker = false,
          )
        } ?: emptyList()

      val deleteMarkers =
        output.deleteMarkers?.map { marker ->
          ObjectVersionInfo(
            bucketName = request.bucketName,
            objectName = marker.key,
            versionId = marker.versionID,
            isLatest = marker.isLatest,
            lastModified = convertDateToInstant(marker.lastModified),
            etag = "",
            size = 0,
            storageClass = StorageClass.STANDARD,
            isDeleteMarker = true,
          )
        } ?: emptyList()

      val allVersions = (versions + deleteMarkers).sortedByDescending { it.lastModified }

      ObjectVersionListing(
        bucketName = request.bucketName,
        prefix = request.prefix,
        keyMarker = request.keyMarker,
        versionIdMarker = request.versionIdMarker,
        nextKeyMarker = output.nextKeyMarker,
        nextVersionIdMarker = output.nextVersionIDMarker,
        versions = allVersions,
        commonPrefixes = output.commonPrefixes?.map { it.prefix } ?: emptyList(),
        isTruncated = output.isTruncated,
        maxKeys = request.maxKeys,
        delimiter = request.delimiter,
      )
    }

  override suspend fun setBucketLifecycle(bucketName: String, rules: List<LifecycleRule>): Result<Unit> =
    execute("Failed to set bucket lifecycle for $bucketName") {
      val tosRules =
        rules.map { rule ->
          val tosRule =
            TosLifecycleRule()
              .setId(rule.id)
              .setPrefix(rule.prefix)
              .setStatus(if (rule.status == LifecycleRuleStatus.ENABLED) StatusType.STATUS_ENABLED else StatusType.STATUS_DISABLED)

          rule.expiration?.let {
            val expiration = TosExpiration().setDays(it.days)
            tosRule.setExpiration(expiration)
          }
          tosRule
        }
      val input = PutBucketLifecycleInput().setBucket(bucketName).setRules(tosRules)
      tosClient.putBucketLifecycle(input)
    }

  override suspend fun getBucketLifecycle(bucketName: String): Result<List<LifecycleRule>> =
    execute("Failed to get bucket lifecycle for $bucketName") {
      val input = GetBucketLifecycleInput().setBucket(bucketName)
      val output = tosClient.getBucketLifecycle(input)
      output.rules?.map { rule ->
        LifecycleRule(
          id = rule.id,
          prefix = rule.prefix,
          status = if (rule.status == StatusType.STATUS_ENABLED) LifecycleRuleStatus.ENABLED else LifecycleRuleStatus.DISABLED,
          expiration = rule.expiration?.let { LifecycleExpiration(it.days) },
        )
      } ?: emptyList()
    }

  override suspend fun deleteBucketLifecycle(bucketName: String): Result<Unit> =
    execute("Failed to delete bucket lifecycle for $bucketName") { tosClient.deleteBucketLifecycle(DeleteBucketLifecycleInput().setBucket(bucketName)) }

  override suspend fun setBucketCors(bucketName: String, rules: List<io.github.truenine.composeserver.oss.CorsRule>): Result<Unit> =
    execute("Failed to set bucket CORS for $bucketName") {
      val tosRules =
        rules.map { rule ->
          val tosRule =
            TosCorsRule()
              .setAllowedOrigins(rule.allowedOrigins)
              .setAllowedMethods(rule.allowedMethods.map { it.name })
              .setAllowedHeaders(rule.allowedHeaders)
              .setExposeHeaders(rule.exposeHeaders)
          rule.maxAgeSeconds?.let { tosRule.setMaxAgeSeconds(it) }
          tosRule
        }
      tosClient.putBucketCORS(PutBucketCORSInput().setBucket(bucketName).setRules(tosRules))
    }

  override suspend fun getBucketCors(bucketName: String): Result<List<io.github.truenine.composeserver.oss.CorsRule>> =
    execute("Failed to get bucket CORS for $bucketName") {
      val output = tosClient.getBucketCORS(GetBucketCORSInput().setBucket(bucketName))
      output.rules?.map { rule ->
        io.github.truenine.composeserver.oss.CorsRule(
          allowedOrigins = rule.allowedOrigins,
          allowedMethods = rule.allowedMethods.map { HttpMethod.valueOf(it) },
          allowedHeaders = rule.allowedHeaders ?: emptyList(),
          exposeHeaders = rule.exposeHeaders ?: emptyList(),
          maxAgeSeconds = rule.maxAgeSeconds,
        )
      } ?: emptyList()
    }

  override suspend fun deleteBucketCors(bucketName: String): Result<Unit> =
    execute("Failed to delete bucket CORS for $bucketName") { tosClient.deleteBucketCORS(DeleteBucketCORSInput().setBucket(bucketName)) }

  private suspend fun <T> execute(message: String, block: () -> T): Result<T> =
    withContext(Dispatchers.IO) {
      try {
        Result.success(block())
      } catch (e: Exception) {
        log.error(message, e)
        Result.failure(mapTosException(e))
      }
    }

  private fun unsupported(feature: String): Result<Nothing> =
    Result.failure(UnsupportedOperationException("Volcengine TOS does not currently support $feature"))

  private fun mapTosException(e: Exception): ObjectStorageException =
    when (e) {
      is com.volcengine.tos.TosException -> {
        val code = e.code ?: "Unknown"
        val message = e.message ?: "Unknown TOS error"
        when (code) {
          "NoSuchBucket" -> BucketNotFoundException(extractBucketName(message), e)
          "BucketAlreadyExists",
          "BucketAlreadyOwnedByYou" -> BucketAlreadyExistsException(extractBucketName(message), e)

          "BucketNotEmpty" -> BucketNotEmptyException(extractBucketName(message), e)
          "NoSuchKey" -> ObjectNotFoundException(extractBucketName(message), extractObjectName(message), e)
          "AccessDenied" -> AuthorizationException("Access denied: $message", e)
          "InvalidAccessKeyId",
          "SignatureDoesNotMatch" -> AuthenticationException("Authentication failed: $message", e)

          "InvalidArgument",
          "InvalidRequest" -> InvalidRequestException("Invalid request: $message", e)

          "ServiceUnavailable" -> ServiceUnavailableException("Service unavailable: $message", e)
          "InternalError" -> ObjectStorageException("TOS internal error: $message", e)
          "RequestTimeout" -> NetworkException("Request timeout", e)
          "SlowDown" -> QuotaExceededException("Rate limit exceeded", e)
          else -> ObjectStorageException("TOS operation failed: $message (code: $code)", e)
        }
      }

      is java.net.SocketTimeoutException -> NetworkException("Socket timeout", e)
      is java.net.ConnectException -> NetworkException("Connection failed", e)
      is java.net.UnknownHostException -> NetworkException("Unknown host", e)
      is java.io.IOException -> NetworkException("Network error: ${e.message}", e)
      is java.security.InvalidKeyException -> AuthenticationException("Invalid key: ${e.message}", e)
      is java.security.NoSuchAlgorithmException -> ConfigurationException("Algorithm not supported: ${e.message}", e)
      is IllegalArgumentException -> InvalidRequestException("Invalid argument: ${e.message}", e)
      is IllegalStateException -> ObjectStorageException("Invalid state: ${e.message}", e)
      else -> ObjectStorageException("TOS operation failed: ${e.message}", e)
    }

  private fun extractBucketName(message: String): String {
    val pattern = Regex("bucket[\\s:]+([a-zA-Z0-9.-]+)", RegexOption.IGNORE_CASE)
    return pattern.find(message)?.groupValues?.getOrNull(1) ?: "unknown"
  }

  private fun extractObjectName(message: String): String {
    val pattern = Regex("object[\\s:]+(\\S+)", RegexOption.IGNORE_CASE)
    return pattern.find(message)?.groupValues?.getOrNull(1) ?: "unknown"
  }

  private fun parseShareUrl(url: String): Pair<String, String> {
    val uri = URI(url)
    val host = uri.host ?: throw IllegalArgumentException("Invalid share URL: missing host")
    val path = uri.path ?: throw IllegalArgumentException("Invalid share URL: missing path")
    val bucket = host.substringBefore('.')
    val key = path.removePrefix("/")
    if (bucket.isBlank() || key.isBlank()) {
      throw IllegalArgumentException("Invalid share URL: $url")
    }
    return bucket to key
  }

  private fun convertDateToInstant(dateValue: Any?): Instant {
    return dateValue?.let { value ->
      when (value) {
        is String -> {
          when {
            value.contains(',') -> java.time.ZonedDateTime.parse(value, java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()
            value.contains('T') && value.endsWith('Z') -> Instant.parse(value)
            else -> Instant.parse(value)
          }
        }

        else -> {
          val timeMethod = value::class.java.getMethod("getTime")
          val millis = timeMethod.invoke(value) as Long
          Instant.ofEpochMilli(millis)
        }
      }
    } ?: Instant.now()
  }
}
