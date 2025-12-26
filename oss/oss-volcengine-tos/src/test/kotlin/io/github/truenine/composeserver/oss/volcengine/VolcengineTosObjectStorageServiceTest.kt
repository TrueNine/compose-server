package io.github.truenine.composeserver.oss.volcengine

import com.volcengine.tos.TOSV2
import com.volcengine.tos.model.bucket.*
import com.volcengine.tos.model.`object`.*
import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.*
import io.mockk.*
import kotlin.test.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Simplified test for Volcengine TOS object storage service
 *
 * As the main code is a simplified implementation, these tests mainly verify basic functionality and interfaces
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
    fun `test health check success`() = runTest {
      val result = service.isHealthy()

      assertTrue(result)
    }
  }

  @Nested
  inner class NativeClient {

    @Test
    fun `test get native client`() {
      val nativeClient = service.getNativeClient<TOSV2>()

      assertNotNull(nativeClient)
      assertEquals(tosClient, nativeClient)
    }
  }

  @Nested
  inner class ExposedBaseUrl {

    @Test
    fun `test get exposed base URL`() {
      assertEquals(exposedBaseUrl, service.exposedBaseUrl)
    }
  }

  @Nested
  inner class BucketOperations {

    @Test
    fun `test create bucket success`() = runTest {
      val request = CreateBucketRequest(bucketName = "test-bucket", region = "cn-beijing")

      val result = service.createBucket(request)

      assertTrue(result.isSuccess)
      val bucketInfo = result.getOrNull()!!
      assertEquals("test-bucket", bucketInfo.name)
      assertEquals("cn-beijing", bucketInfo.region)
    }

    @Test
    fun `test check bucket exists`() = runTest {
      val result = service.bucketExists("test-bucket")

      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `test delete bucket success`() = runTest {
      val result = service.deleteBucket("test-bucket")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test list buckets`() = runTest {
      val result = service.listBuckets()

      assertTrue(result.isSuccess)
      val buckets = result.getOrNull()!!
      assertTrue(buckets.isEmpty()) // simplified implementation returns an empty list
    }

    @Test
    fun `test set bucket public read`() = runTest {
      val result = service.setBucketPublicRead("test-bucket")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test get bucket policy`() = runTest {
      val result = service.getBucketPolicy("test-bucket")

      assertTrue(result.isSuccess)
      assertEquals("", result.getOrNull()) // simplified implementation returns an empty string
    }

    @Test
    fun `test set bucket policy`() = runTest {
      val policyJson = """{"Version":"2012-10-17","Statement":[]}"""
      val result = service.setBucketPolicy("test-bucket", policyJson)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test set bucket access level to public`() = runTest {
      val result = service.setBucketAccess("test-bucket", BucketAccessLevel.PUBLIC)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test set bucket access level to private`() = runTest {
      val result = service.setBucketAccess("test-bucket", BucketAccessLevel.PRIVATE)

      assertTrue(result.isSuccess)
    }

    @Nested
    inner class `Exception Handling Tests` {

      @Test
      fun `test bucket not found exception`() = runTest {
        // use a generic exception to test the exception handling logic
        every { tosClient.headBucket(any<HeadBucketV2Input>()) } throws RuntimeException("Bucket not found")

        val result = service.bucketExists("non-existent-bucket")

        // for bucketExists, any exception should be caught and converted to false
        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!)
      }

      @Test
      fun `test network exception handling`() = runTest {
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
    fun `test upload object success`() = runTest {
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
    fun `test simplified upload object success`() = runTest {
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
    fun `test get object info`() = runTest {
      val result = service.getObjectInfo("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
      val objectInfo = result.getOrNull()!!
      assertEquals("test-bucket", objectInfo.bucketName)
      assertEquals("test-object.txt", objectInfo.objectName)
      assertEquals("mock-etag", objectInfo.etag)
    }

    @Test
    fun `test get object content success`() = runTest {
      val result = service.getObject("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
      val objectContent = result.getOrNull()!!
      assertEquals("test-bucket", objectContent.objectInfo.bucketName)
      assertEquals("test-object.txt", objectContent.objectInfo.objectName)
      assertEquals("test-etag", objectContent.objectInfo.etag)
    }

    @Test
    fun `test get object range content success`() = runTest {
      val result = service.getObject("test-bucket", "test-object.txt", 0L, 10L)

      assertTrue(result.isSuccess)
      val objectContent = result.getOrNull()!!
      assertEquals("test-bucket", objectContent.objectInfo.bucketName)
      assertEquals("test-object.txt", objectContent.objectInfo.objectName)
    }

    @Test
    fun `test check object exists`() = runTest {
      val result = service.objectExists("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `test delete object success`() = runTest {
      val result = service.deleteObject("test-bucket", "test-object.txt")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test batch delete objects success`() = runTest {
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
    fun `test copy object success`() = runTest {
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
    fun `test list objects`() = runTest {
      val request = ListObjectsRequest(bucketName = "test-bucket", prefix = "test/", maxKeys = 100)

      val result = service.listObjects(request)

      assertTrue(result.isSuccess)
      val listing = result.getOrNull()!!
      assertEquals("test-bucket", listing.bucketName)
      assertTrue(listing.objects.isEmpty()) // simplified implementation returns an empty list
      assertFalse(listing.isTruncated)
      assertEquals(100, listing.maxKeys)
    }

    @Test
    fun `test generate presigned URL`() = runTest {
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
    fun `test initiate multipart upload`() = runTest {
      val request = InitiateMultipartUploadRequest(bucketName = "test-bucket", objectName = "large-file.txt", contentType = "text/plain")

      val result = service.initiateMultipartUpload(request)

      assertTrue(result.isSuccess)
      val upload = result.getOrNull()!!
      assertTrue(upload.uploadId.startsWith("mock-upload-id-"))
      assertEquals("test-bucket", upload.bucketName)
      assertEquals("large-file.txt", upload.objectName)
    }

    @Test
    fun `test upload part`() = runTest {
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
    fun `test complete multipart upload`() = runTest {
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
    fun `test abort multipart upload`() = runTest {
      val result = service.abortMultipartUpload("test-upload-id", "test-bucket", "large-file.txt")

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test list parts`() = runTest {
      val result = service.listParts("test-upload-id", "test-bucket", "large-file.txt")

      assertTrue(result.isSuccess)
      val parts = result.getOrNull()!!
      assertTrue(parts.isEmpty()) // simplified implementation returns an empty list
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `test generate share link`() = runTest {
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
    fun `test upload and generate share link`() = runTest {
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
    fun `test validate share link`() = runTest {
      val shareUrl = "https://test-bucket.tos.example.com/test-object.txt?signature=test"

      val result = service.validateShareLink(shareUrl)

      assertTrue(result.isSuccess)
      val shareInfo = result.getOrNull()!!
      assertEquals("test-bucket", shareInfo.bucketName)
      assertEquals("test-object.txt", shareInfo.objectName)
    }

    @Test
    fun `test revoke share link`() = runTest {
      val shareUrl = "https://test-bucket.tos.example.com/test-object.txt?signature=test"

      val result = service.revokeShareLink(shareUrl)

      assertTrue(result.isSuccess)
    }
  }
}
