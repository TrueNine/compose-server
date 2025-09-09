package io.github.truenine.composeserver.oss.volcengine

import com.volcengine.tos.TOSV2
import com.volcengine.tos.model.bucket.CreateBucketV2Input
import com.volcengine.tos.model.bucket.CreateBucketV2Output
import com.volcengine.tos.model.bucket.DeleteBucketInput
import com.volcengine.tos.model.bucket.DeleteBucketOutput
import com.volcengine.tos.model.bucket.GetBucketPolicyInput
import com.volcengine.tos.model.bucket.GetBucketPolicyOutput
import com.volcengine.tos.model.bucket.HeadBucketV2Input
import com.volcengine.tos.model.bucket.HeadBucketV2Output
import com.volcengine.tos.model.bucket.ListBucketsV2Input
import com.volcengine.tos.model.bucket.ListBucketsV2Output
import com.volcengine.tos.model.bucket.ListedBucket
import com.volcengine.tos.model.bucket.PutBucketACLInput
import com.volcengine.tos.model.bucket.PutBucketACLOutput
import com.volcengine.tos.model.bucket.PutBucketPolicyInput
import com.volcengine.tos.model.bucket.PutBucketPolicyOutput
import com.volcengine.tos.model.`object`.CopyObjectV2Input
import com.volcengine.tos.model.`object`.CopyObjectV2Output
import com.volcengine.tos.model.`object`.DeleteObjectInput
import com.volcengine.tos.model.`object`.DeleteObjectOutput
import com.volcengine.tos.model.`object`.GetObjectV2Input
import com.volcengine.tos.model.`object`.GetObjectV2Output
import com.volcengine.tos.model.`object`.HeadObjectV2Input
import com.volcengine.tos.model.`object`.HeadObjectV2Output
import com.volcengine.tos.model.`object`.ListObjectsV2Input
import com.volcengine.tos.model.`object`.ListObjectsV2Output
import com.volcengine.tos.model.`object`.ListedObjectV2
import com.volcengine.tos.model.`object`.PreSignedURLInput
import com.volcengine.tos.model.`object`.PreSignedURLOutput
import com.volcengine.tos.model.`object`.PutObjectInput
import com.volcengine.tos.model.`object`.PutObjectOutput
import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.BucketAccessLevel
import io.github.truenine.composeserver.oss.CompleteMultipartUploadRequest
import io.github.truenine.composeserver.oss.CopyObjectRequest
import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.InitiateMultipartUploadRequest
import io.github.truenine.composeserver.oss.ListObjectsRequest
import io.github.truenine.composeserver.oss.PartInfo
import io.github.truenine.composeserver.oss.PutObjectRequest
import io.github.truenine.composeserver.oss.ShareLinkRequest
import io.github.truenine.composeserver.oss.UploadPartRequest
import io.github.truenine.composeserver.oss.UploadWithLinkRequest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 简化的 Volcengine TOS 对象存储服务测试
 *
 * 由于主代码是简化实现，这些测试主要验证基本功能和接口
 */
class VolcengineTosObjectStorageServiceTest {

  private lateinit var tosClient: TOSV2
  private lateinit var service: VolcengineTosObjectStorageService
  private val exposedBaseUrl = "https://test.example.com"

  @BeforeEach
  fun setUp() {
    clearAllMocks()
    tosClient = mockk<TOSV2>()

    // Configure mock responses for all TOS SDK methods
    setupTosClientMocks()

    service = VolcengineTosObjectStorageService(tosClient, exposedBaseUrl)
  }

  private fun setupTosClientMocks() {
    // Mock bucket operations
    every { tosClient.createBucket(any<CreateBucketV2Input>()) } returns mockk<CreateBucketV2Output> { every { location } returns "test-location" }

    every { tosClient.deleteBucket(any<DeleteBucketInput>()) } returns mockk<DeleteBucketOutput>()

    every { tosClient.listBuckets(any<ListBucketsV2Input>()) } returns mockk<ListBucketsV2Output> { every { buckets } returns emptyList<ListedBucket>() }

    every { tosClient.headBucket(any<HeadBucketV2Input>()) } returns mockk<HeadBucketV2Output> { every { region } returns "test-region" }

    every { tosClient.putBucketACL(any<PutBucketACLInput>()) } returns mockk<PutBucketACLOutput>()

    every { tosClient.getBucketPolicy(any<GetBucketPolicyInput>()) } returns mockk<GetBucketPolicyOutput> { every { policy } returns "" }

    every { tosClient.putBucketPolicy(any<PutBucketPolicyInput>()) } returns mockk<PutBucketPolicyOutput>()

    // Mock object operations
    every { tosClient.putObject(any<PutObjectInput>()) } returns
      mockk<PutObjectOutput> {
        every { etag } returns "mock-etag"
        every { versionID } returns "test-version"
      }

    every { tosClient.getObject(any<GetObjectV2Input>()) } returns
      mockk<GetObjectV2Output> {
        every { content } returns "test content".byteInputStream()
        every { contentLength } returns 12L
        every { etag } returns "test-etag"
        every { lastModified } returns null
        every { contentType } returns "text/plain"
      }

    every { tosClient.headObject(any<HeadObjectV2Input>()) } returns
      mockk<HeadObjectV2Output> {
        every { etag } returns "mock-etag"
        every { contentLength } returns 12L
        every { lastModified } returns null
        every { contentType } returns "text/plain"
        // every { meta } returns mapOf("test-key" to "test-value")
      }

    every { tosClient.deleteObject(any<DeleteObjectInput>()) } returns mockk<DeleteObjectOutput> { every { versionID } returns "test-version" }

    every { tosClient.copyObject(any<CopyObjectV2Input>()) } returns
      mockk<CopyObjectV2Output> {
        every { etag } returns "copy-etag"
        every { lastModified } returns null
      }

    @Suppress("DEPRECATION")
    every { tosClient.listObjects(any<ListObjectsV2Input>()) } returns
      mockk<ListObjectsV2Output> {
        every { contents } returns emptyList<ListedObjectV2>()
        every { isTruncated } returns false
        // every { nextContinuationToken } returns null
        every { commonPrefixes } returns emptyList()
      }

    every { tosClient.preSignedURL(any<PreSignedURLInput>()) } returns
      mockk<PreSignedURLOutput> { every { signedUrl } returns "https://test-bucket.tos.example.com/test-object.txt?signature=test" }

    // Mock multipart upload operations
    every { tosClient.createMultipartUpload(any<com.volcengine.tos.model.`object`.CreateMultipartUploadInput>()) } returns
      mockk<com.volcengine.tos.model.`object`.CreateMultipartUploadOutput> { every { uploadID } returns "mock-upload-id-123" }

    every { tosClient.uploadPart(any<com.volcengine.tos.model.`object`.UploadPartV2Input>()) } returns
      mockk<com.volcengine.tos.model.`object`.UploadPartV2Output> { every { etag } returns "mock-etag-1" }

    every { tosClient.completeMultipartUpload(any<com.volcengine.tos.model.`object`.CompleteMultipartUploadV2Input>()) } returns
      mockk<com.volcengine.tos.model.`object`.CompleteMultipartUploadV2Output> { every { etag } returns "mock-etag-complete" }

    every { tosClient.abortMultipartUpload(any<com.volcengine.tos.model.`object`.AbortMultipartUploadInput>()) } returns
      mockk<com.volcengine.tos.model.`object`.AbortMultipartUploadOutput>()

    every { tosClient.listParts(any<com.volcengine.tos.model.`object`.ListPartsInput>()) } returns
      mockk<com.volcengine.tos.model.`object`.ListPartsOutput> { every { uploadedParts } returns emptyList() }
  }

  @Nested
  inner class HealthCheck {

    @Test
    fun `测试健康检查成功`() = runTest {
      val result = service.isHealthy()

      assertTrue(result)
    }
  }

  @Nested
  inner class NativeClient {

    @Test
    fun `测试获取原生客户端`() {
      val nativeClient = service.getNativeClient<TOSV2>()

      assertNotNull(nativeClient)
      assertEquals(tosClient, nativeClient)
    }
  }

  @Nested
  inner class ExposedBaseUrl {

    @Test
    fun `测试获取暴露的基础URL`() {
      assertEquals(exposedBaseUrl, service.exposedBaseUrl)
    }
  }

  @Nested
  inner class BucketOperations {

    @Test
    fun `测试创建存储桶成功`() = runTest {
      val request = CreateBucketRequest(bucketName = "test-bucket", region = "cn-beijing")

      val result = service.createBucket(request)

      assertTrue(result.isSuccess)
      val bucketInfo = result.getOrNull()!!
      assertEquals("test-bucket", bucketInfo.name)
      assertEquals("cn-beijing", bucketInfo.region)
    }

    @Test
    fun `测试检查存储桶存在`() = runTest {
      val result = service.bucketExists("test-bucket")

      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `测试删除存储桶成功`() = runTest {
      val result = service.deleteBucket("test-bucket")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试列出存储桶`() = runTest {
      val result = service.listBuckets()

      assertTrue(result.isSuccess)
      val buckets = result.getOrNull()!!
      assertTrue(buckets.isEmpty()) // 简化实现返回空列表
    }

    @Test
    fun `测试设置存储桶公共读取`() = runTest {
      val result = service.setBucketPublicRead("test-bucket")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试获取存储桶策略`() = runTest {
      val result = service.getBucketPolicy("test-bucket")

      assertTrue(result.isSuccess)
      assertEquals("", result.getOrNull()) // 简化实现返回空字符串
    }

    @Test
    fun `测试设置存储桶策略`() = runTest {
      val policyJson = """{"Version":"2012-10-17","Statement":[]}"""
      val result = service.setBucketPolicy("test-bucket", policyJson)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试设置存储桶访问级别为公共`() = runTest {
      val result = service.setBucketAccess("test-bucket", BucketAccessLevel.PUBLIC)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试设置存储桶访问级别为私有`() = runTest {
      val result = service.setBucketAccess("test-bucket", BucketAccessLevel.PRIVATE)

      assertTrue(result.isSuccess)
    }

    @Nested
    inner class `异常处理测试` {

      @Test
      fun `测试存储桶不存在异常`() = runTest {
        // 使用一个通用异常来测试异常处理逻辑
        every { tosClient.headBucket(any<HeadBucketV2Input>()) } throws RuntimeException("Bucket not found")

        val result = service.bucketExists("non-existent-bucket")

        // 对于bucketExists方法，任何异常都应该被捕获并转换为false
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!)
      }

      @Test
      fun `测试网络异常处理`() = runTest {
        every { tosClient.listBuckets(any<ListBucketsV2Input>()) } throws java.net.SocketTimeoutException("Connection timeout")

        val result = service.listBuckets()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is io.github.truenine.composeserver.oss.NetworkException)
      }
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `测试上传对象成功`() = runTest {
      val request =
        PutObjectRequest(
          bucketName = "test-bucket",
          objectName = "test-object.txt",
          inputStream = "test content".byteInputStream(),
          size = 12L,
          contentType = "text/plain",
        )

      val result = service.putObject(request)

      assertTrue(result.isSuccess)
      val objectInfo = result.getOrNull()!!
      assertEquals("test-bucket", objectInfo.bucketName)
      assertEquals("test-object.txt", objectInfo.objectName)
      assertEquals(12L, objectInfo.size)
      assertEquals("mock-etag", objectInfo.etag)
    }

    @Test
    fun `测试简化上传对象成功`() = runTest {
      val result =
        service.putObject(
          bucketName = "test-bucket",
          objectName = "test-object.txt",
          inputStream = "test content".byteInputStream(),
          size = 12L,
          contentType = "text/plain",
          metadata = mapOf("key" to "value"),
        )

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试获取对象信息`() = runTest {
      val result = service.getObjectInfo("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
      val objectInfo = result.getOrNull()!!
      assertEquals("test-bucket", objectInfo.bucketName)
      assertEquals("test-object.txt", objectInfo.objectName)
      assertEquals("mock-etag", objectInfo.etag)
    }

    @Test
    fun `测试获取对象内容成功`() = runTest {
      val result = service.getObject("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
      val objectContent = result.getOrNull()!!
      assertEquals("test-bucket", objectContent.objectInfo.bucketName)
      assertEquals("test-object.txt", objectContent.objectInfo.objectName)
      assertEquals("test-etag", objectContent.objectInfo.etag)
    }

    @Test
    fun `测试获取对象范围内容成功`() = runTest {
      val result = service.getObject("test-bucket", "test-object.txt", 0L, 10L)

      assertTrue(result.isSuccess)
      val objectContent = result.getOrNull()!!
      assertEquals("test-bucket", objectContent.objectInfo.bucketName)
      assertEquals("test-object.txt", objectContent.objectInfo.objectName)
    }

    @Test
    fun `测试检查对象存在`() = runTest {
      val result = service.objectExists("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `测试删除对象成功`() = runTest {
      val result = service.deleteObject("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试批量删除对象成功`() = runTest {
      val objectNames = listOf("object1.txt", "object2.txt", "object3.txt")
      val result = service.deleteObjects("test-bucket", objectNames)

      assertTrue(result.isSuccess)
      val deleteResults = result.getOrNull()!!
      assertEquals(3, deleteResults.size)
      deleteResults.forEach { deleteResult ->
        assertTrue(deleteResult.success)
        assertNull(deleteResult.errorMessage)
      }
    }

    @Test
    fun `测试复制对象成功`() = runTest {
      val request =
        CopyObjectRequest(
          sourceBucketName = "source-bucket",
          sourceObjectName = "source-object.txt",
          destinationBucketName = "dest-bucket",
          destinationObjectName = "dest-object.txt",
        )

      val result = service.copyObject(request)

      assertTrue(result.isSuccess)
      val objectInfo = result.getOrNull()!!
      assertEquals("dest-bucket", objectInfo.bucketName)
      assertEquals("dest-object.txt", objectInfo.objectName)
      assertEquals("copy-etag", objectInfo.etag)
    }

    @Test
    fun `测试列出对象`() = runTest {
      val request = ListObjectsRequest(bucketName = "test-bucket", prefix = "test/", maxKeys = 100)

      val result = service.listObjects(request)

      assertTrue(result.isSuccess)
      val listing = result.getOrNull()!!
      assertEquals("test-bucket", listing.bucketName)
      assertTrue(listing.objects.isEmpty()) // 简化实现返回空列表
      assertFalse(listing.isTruncated)
      assertEquals(100, listing.maxKeys)
    }

    @Test
    fun `测试生成预签名URL`() = runTest {
      val result =
        service.generatePresignedUrl(
          bucketName = "test-bucket",
          objectName = "test-object.txt",
          expiration = java.time.Duration.ofHours(1),
          method = HttpMethod.GET,
        )

      assertTrue(result.isSuccess)
      assertEquals("https://test-bucket.tos.example.com/test-object.txt?signature=test", result.getOrNull())
    }
  }

  @Nested
  inner class MultipartUpload {

    @Test
    fun `测试初始化分片上传`() = runTest {
      val request = InitiateMultipartUploadRequest(bucketName = "test-bucket", objectName = "large-file.txt", contentType = "text/plain")

      val result = service.initiateMultipartUpload(request)

      assertTrue(result.isSuccess)
      val upload = result.getOrNull()!!
      assertTrue(upload.uploadId.startsWith("mock-upload-id-"))
      assertEquals("test-bucket", upload.bucketName)
      assertEquals("large-file.txt", upload.objectName)
    }

    @Test
    fun `测试上传分片`() = runTest {
      val request =
        UploadPartRequest(
          bucketName = "test-bucket",
          objectName = "large-file.txt",
          uploadId = "test-upload-id",
          partNumber = 1,
          inputStream = "part content".byteInputStream(),
          size = 12L,
        )

      val result = service.uploadPart(request)

      assertTrue(result.isSuccess)
      val partInfo = result.getOrNull()!!
      assertEquals(1, partInfo.partNumber)
      assertEquals("mock-etag-1", partInfo.etag)
      assertEquals(12L, partInfo.size)
    }

    @Test
    fun `测试完成分片上传`() = runTest {
      val parts = listOf(PartInfo(partNumber = 1, etag = "etag1", size = 100L), PartInfo(partNumber = 2, etag = "etag2", size = 100L))
      val request = CompleteMultipartUploadRequest(bucketName = "test-bucket", objectName = "large-file.txt", uploadId = "test-upload-id", parts = parts)

      val result = service.completeMultipartUpload(request)

      assertTrue(result.isSuccess)
      val objectInfo = result.getOrNull()!!
      assertEquals("test-bucket", objectInfo.bucketName)
      assertEquals("large-file.txt", objectInfo.objectName)
      assertTrue(objectInfo.etag.startsWith("mock-etag-"))
      assertEquals(200L, objectInfo.size) // Sum of part sizes
    }

    @Test
    fun `测试中止分片上传`() = runTest {
      val result = service.abortMultipartUpload("test-upload-id", "test-bucket", "large-file.txt")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试列出分片`() = runTest {
      val result = service.listParts("test-upload-id", "test-bucket", "large-file.txt")

      assertTrue(result.isSuccess)
      val parts = result.getOrNull()!!
      assertTrue(parts.isEmpty()) // 简化实现返回空列表
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `测试生成分享链接`() = runTest {
      val request =
        ShareLinkRequest(bucketName = "test-bucket", objectName = "test-object.txt", expiration = java.time.Duration.ofHours(1), method = HttpMethod.GET)

      val result = service.generateShareLink(request)

      assertTrue(result.isSuccess)
      val shareInfo = result.getOrNull()!!
      assertEquals("test-bucket", shareInfo.bucketName)
      assertEquals("test-object.txt", shareInfo.objectName)
      assertEquals(HttpMethod.GET, shareInfo.method)
      assertFalse(shareInfo.hasPassword)
    }

    @Test
    fun `测试上传并生成分享链接`() = runTest {
      val request =
        UploadWithLinkRequest(
          bucketName = "test-bucket",
          objectName = "test-object.txt",
          inputStream = "test content".byteInputStream(),
          size = 12L,
          contentType = "text/plain",
          shareExpiration = java.time.Duration.ofHours(1),
        )

      val result = service.uploadWithLink(request)

      assertTrue(result.isSuccess)
      val response = result.getOrNull()!!
      assertEquals("test-bucket", response.objectInfo.bucketName)
      assertEquals("test-object.txt", response.objectInfo.objectName)
      assertNotNull(response.shareLink)
      assertNotNull(response.publicUrl)
    }

    @Test
    fun `测试验证分享链接`() = runTest {
      val shareUrl = "https://test-bucket.tos.example.com/test-object.txt?signature=test"

      val result = service.validateShareLink(shareUrl)

      assertTrue(result.isSuccess)
      val shareInfo = result.getOrNull()!!
      assertEquals("test-bucket", shareInfo.bucketName)
      assertEquals("test-object.txt", shareInfo.objectName)
    }

    @Test
    fun `测试撤销分享链接`() = runTest {
      val shareUrl = "https://test-bucket.tos.example.com/test-object.txt?signature=test"

      val result = service.revokeShareLink(shareUrl)

      assertTrue(result.isSuccess)
    }
  }
}
