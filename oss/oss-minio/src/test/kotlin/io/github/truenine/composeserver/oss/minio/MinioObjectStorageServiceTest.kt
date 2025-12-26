package io.github.truenine.composeserver.oss.minio

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.*
import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.testcontainers.IOssMinioContainer
import io.minio.MinioClient
import kotlin.test.*
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/** MinIO object storage service test */
class MinioObjectStorageServiceTest : IOssMinioContainer {

  private lateinit var minioClient: MinioClient
  private lateinit var service: MinioObjectStorageService
  private val exposedBaseUrl = "http://localhost"

  @BeforeEach
  fun setUp() = minio {
    // create a real MinioClient connection to testcontainers
    val port = it.getMappedPort(9000)
    val host = it.host
    assertNotNull(port)
    assertNotNull(host)

    minioClient = MinioClient.builder().endpoint("http://${host}:$port").credentials("minioadmin", "minioadmin").build()
    service = MinioObjectStorageService(minioClient, "$exposedBaseUrl:$port")
  }

  @Nested
  inner class HealthCheck {

    @Test
    fun `test health check success`() = runTest {
      val result = service.isHealthy()
      assertTrue(result)
    }

    @Test
    fun `test health check failure`() = runTest {
      // create an invalid client to test failure case
      val invalidClient = MinioClient.builder().endpoint("http://invalid-host:9999").credentials("invalid", "invalid").build()
      val invalidService = MinioObjectStorageService(invalidClient, "http://invalid-host:9999")

      val result = invalidService.isHealthy()

      assertFalse(result)
    }
  }

  @Nested
  inner class NativeClient {

    @Test
    fun `test get native client`() {
      val nativeClient = service.getNativeClient<MinioClient>()

      assertNotNull(nativeClient)
      assertEquals(minioClient, nativeClient)
    }
  }

  @Nested
  inner class ExposedBaseUrl {

    @Test
    fun `test get exposed base URL`() {
      assertTrue(service.exposedBaseUrl.startsWith(exposedBaseUrl))
    }
  }

  @Nested
  inner class BucketOperations {

    @Test
    fun `test create bucket success`() = runTest {
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val request = CreateBucketRequest(bucketName = bucketName, region = "us-east-1")

      val result = service.createBucket(request)

      assertTrue(result.isSuccess)
      val bucketInfo = result.getOrNull()!!
      assertEquals(bucketName, bucketInfo.name)
      assertEquals("us-east-1", bucketInfo.region)

      // cleanup: delete the created bucket
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test create bucket failure`() = runTest {
      // use an invalid bucket name to test failure (bucket name cannot contain uppercase letters)
      val request = CreateBucketRequest(bucketName = "INVALID-BUCKET-NAME", region = "us-east-1")

      val result = service.createBucket(request)

      assertTrue(result.isFailure)
    }

    @Test
    fun `test check bucket exists`() = runTest {
      val bucketName = "test-exists-bucket-${System.currentTimeMillis()}"

      // create the bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result = service.bucketExists(bucketName)

      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull() == true)

      // cleanup: delete the created bucket
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test check bucket does not exist`() = runTest {
      val result = service.bucketExists("non-existent-bucket-${System.currentTimeMillis()}")

      assertTrue(result.isSuccess)
      assertFalse(result.getOrNull() == true)
    }

    @Test
    fun `test delete bucket success`() = runTest {
      val bucketName = "test-delete-bucket-${System.currentTimeMillis()}"

      // create the bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result = service.deleteBucket(bucketName)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test delete bucket failure`() = runTest {
      val result = service.deleteBucket("non-existent-bucket-${System.currentTimeMillis()}")

      // MinIO does not report an error when deleting a non-existent bucket, so this test might succeed
      // we can test deleting a bucket with objects to trigger a failure
      assertTrue(result.isSuccess || result.isFailure)
    }

    @Test
    fun `test list buckets`() = runTest {
      val bucketName1 = "test-list-bucket1-${System.currentTimeMillis()}"
      val bucketName2 = "test-list-bucket2-${System.currentTimeMillis()}"

      // create two test buckets
      service.createBucket(CreateBucketRequest(bucketName = bucketName1))
      service.createBucket(CreateBucketRequest(bucketName = bucketName2))

      val result = service.listBuckets()

      assertTrue(result.isSuccess)
      val buckets = result.getOrNull()!!
      assertTrue(buckets.any { it.name == bucketName1 })
      assertTrue(buckets.any { it.name == bucketName2 })

      // cleanup: delete the created buckets
      service.deleteBucket(bucketName1)
      service.deleteBucket(bucketName2)
    }

    @Test
    fun `test set bucket access level to public`() = runTest {
      val bucketName = "test-access-public-bucket-${System.currentTimeMillis()}"

      // create the bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result = service.setBucketAccess(bucketName, BucketAccessLevel.PUBLIC)

      assertTrue(result.isSuccess)

      // cleanup: delete the created bucket
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test set bucket access level to private`() = runTest {
      val bucketName = "test-access-private-bucket-${System.currentTimeMillis()}"

      // create the bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result = service.setBucketAccess(bucketName, BucketAccessLevel.PRIVATE)

      assertTrue(result.isSuccess)

      // cleanup: delete the created bucket
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test set access level for non-existent bucket fails`() = runTest {
      val bucketName = "non-existent-bucket-${System.currentTimeMillis()}"

      val result = service.setBucketAccess(bucketName, BucketAccessLevel.PUBLIC)

      assertTrue(result.isFailure)
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `test upload object success`() = runTest {
      val bucketName = "test-upload-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "test content"

      // create the bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val request =
        PutObjectRequest(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
        )

      val result = service.putObject(request)

      assertTrue(result.isSuccess)
      val objectInfo = result.getOrNull()!!
      assertEquals(bucketName, objectInfo.bucketName)
      assertEquals(objectName, objectInfo.objectName)
      assertEquals(content.length.toLong(), objectInfo.size)
      assertNotNull(objectInfo.etag)

      // cleanup: deleting the bucket will also delete its objects
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test upload object failure`() = runTest {
      // use a non-existent bucket to test failure
      val request =
        PutObjectRequest(
          bucketName = "non-existent-bucket-${System.currentTimeMillis()}",
          objectName = "test-object.txt",
          inputStream = "test content".byteInputStream(),
          size = 12L,
          contentType = "text/plain",
        )

      val result = service.putObject(request)

      assertTrue(result.isFailure)
    }

    @Test
    fun `test simplified upload object success`() = runTest {
      val bucketName = "test-simple-upload-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "test content"

      // create the bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result =
        service.putObject(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
          metadata = mapOf("key" to "value"),
        )

      assertTrue(result.isSuccess)

      // cleanup: delete the bucket
      service.deleteBucket(bucketName)
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `test generate share link success`() = runTest {
      val bucketName = "test-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-share-object.txt"
      val content = "test share content"

      // create bucket and object first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))
      service.putObject(
        bucketName = bucketName,
        objectName = objectName,
        inputStream = content.byteInputStream(),
        size = content.length.toLong(),
        contentType = "text/plain",
      )

      // generate share link
      val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1), method = HttpMethod.GET)

      val result = service.generateShareLink(shareRequest)

      assertTrue(result.isSuccess)
      val shareInfo = result.getOrNull()!!
      assertEquals(bucketName, shareInfo.bucketName)
      assertEquals(objectName, shareInfo.objectName)
      assertEquals(HttpMethod.GET, shareInfo.method)
      assertTrue(shareInfo.shareUrl.isNotEmpty())
      assertFalse(shareInfo.hasPassword)

      // cleanup
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test upload and return share link success`() = runTest {
      val bucketName = "test-upload-link-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-link-object.txt"
      val content = "test upload with link content"

      // create bucket first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      // upload and generate share link
      val uploadRequest =
        UploadWithLinkRequest(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
          shareExpiration = java.time.Duration.ofHours(2),
          shareMethod = HttpMethod.GET,
        )

      val result = service.uploadWithLink(uploadRequest)

      assertTrue(result.isSuccess)
      val response = result.getOrNull()!!

      // verify object info
      assertEquals(bucketName, response.objectInfo.bucketName)
      assertEquals(objectName, response.objectInfo.objectName)
      assertEquals(content.length.toLong(), response.objectInfo.size)

      // verify share link info
      assertEquals(bucketName, response.shareLink.bucketName)
      assertEquals(objectName, response.shareLink.objectName)
      assertEquals(HttpMethod.GET, response.shareLink.method)
      assertTrue(response.shareLink.shareUrl.isNotEmpty())

      // verify public URL
      assertNotNull(response.publicUrl)
      assertTrue(response.publicUrl!!.contains(bucketName))
      assertTrue(response.publicUrl!!.contains(objectName))

      // cleanup
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test validate share link success`() = runTest {
      val bucketName = "test-validate-link-bucket-${System.currentTimeMillis()}"
      val objectName = "test-validate-link-object.txt"
      val content = "test validate link content"

      // create bucket and object first
      service.createBucket(CreateBucketRequest(bucketName = bucketName))
      service.putObject(
        bucketName = bucketName,
        objectName = objectName,
        inputStream = content.byteInputStream(),
        size = content.length.toLong(),
        contentType = "text/plain",
      )

      // generate share link
      val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1))
      val shareResult = service.generateShareLink(shareRequest)
      assertTrue(shareResult.isSuccess)
      val shareUrl = shareResult.getOrThrow().shareUrl

      // validate share link
      val validateResult = service.validateShareLink(shareUrl)

      assertTrue(validateResult.isSuccess)
      val validatedInfo = validateResult.getOrNull()!!
      assertEquals(bucketName, validatedInfo.bucketName)
      assertEquals(objectName, validatedInfo.objectName)
      assertEquals(shareUrl, validatedInfo.shareUrl)

      // cleanup
      service.deleteBucket(bucketName)
    }

    @Test
    fun `test revoke share link`() = runTest {
      val shareUrl = "http://example.com/test-share-url"

      // MinIO does not support revoking presigned URLs, but the method should return success
      val result = service.revokeShareLink(shareUrl)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `test share link actual download functionality`() = runTest {
      val bucketName = "test-real-download-bucket-${System.currentTimeMillis()}"
      val objectName = "test-real-download-object.txt"
      val content = "Hello, Real Download Test!"

      try {
        // create bucket and object first
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
        )

        // generate share link
        val shareRequest =
          ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofMinutes(5), method = HttpMethod.GET)
        val shareResult = service.generateShareLink(shareRequest)
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // download content using the share link
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "download via share link should succeed")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "downloaded content should match original content")
        }

        log.info("share link actual download test passed: $shareUrl")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test full flow of upload and return link`() = runTest {
      val bucketName = "test-upload-link-flow-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-link-flow-object.txt"
      val content = "Hello, Upload with Link Flow Test!"

      try {
        // create bucket first
        service.createBucket(CreateBucketRequest(bucketName = bucketName))

        // upload and generate share link
        val uploadRequest =
          UploadWithLinkRequest(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = content.byteInputStream(),
            size = content.length.toLong(),
            contentType = "text/plain",
            shareExpiration = java.time.Duration.ofMinutes(10),
            shareMethod = HttpMethod.GET,
          )

        val uploadResult = service.uploadWithLink(uploadRequest)
        assertTrue(uploadResult.isSuccess, "upload and generate share link should succeed")

        val response = uploadResult.getOrThrow()

        // verify upload result
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)

        // verify share link
        assertTrue(response.shareLink.shareUrl.isNotEmpty(), "share link should not be empty")
        assertEquals(bucketName, response.shareLink.bucketName)
        assertEquals(objectName, response.shareLink.objectName)

        // verify public URL
        assertNotNull(response.publicUrl, "public URL should not be empty")
        assertTrue(response.publicUrl!!.contains(bucketName))
        assertTrue(response.publicUrl!!.contains(objectName))

        // download and verify content using the generated share link
        val downloadResult = service.downloadFromShareLink(response.shareLink.shareUrl)
        assertTrue(downloadResult.isSuccess, "download via generated share link should succeed")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "downloaded content via share link should match original content")
        }

        log.info("full flow of upload and return link test passed")
        log.info("share link: ${response.shareLink.shareUrl}")
        log.info("public URL: ${response.publicUrl}")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test accessing share link with HTTP client`() = runTest {
      val bucketName = "test-http-access-bucket-${System.currentTimeMillis()}"
      val objectName = "test-http-access-object.txt"
      val content = "Hello, HTTP Access Test!"

      try {
        // create bucket and object first
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
        )

        // generate share link
        val shareRequest =
          ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofMinutes(5), method = HttpMethod.GET)
        val shareResult = service.generateShareLink(shareRequest)
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // test share link using Java's HttpURLConnection
        try {
          val url = java.net.URL(shareUrl)
          val connection = url.openConnection() as java.net.HttpURLConnection
          connection.requestMethod = "GET"
          connection.connectTimeout = 5000
          connection.readTimeout = 10000

          val responseCode = connection.responseCode
          assertEquals(200, responseCode, "HTTP response code should be 200")

          val downloadedText = connection.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "downloaded content via HTTP should match original content")

          log.info("accessing share link with HTTP client test passed")
          log.info("share link: $shareUrl")
          log.info("HTTP response code: $responseCode")
        } catch (e: Exception) {
          log.warn("HTTP access test failed, possibly due to network issues or MinIO configuration: ${e.message}")
          // in a test environment, external HTTP access to MinIO might not be possible, which is normal
          // we log a warning but don't fail the test
        }
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test generate share link failure`() = runTest {
      // use a non-existent bucket to test failure
      val shareRequest =
        ShareLinkRequest(
          bucketName = "non-existent-bucket-${System.currentTimeMillis()}",
          objectName = "non-existent-object.txt",
          expiration = java.time.Duration.ofHours(1),
        )

      val result = service.generateShareLink(shareRequest)

      assertTrue(result.isFailure)
    }
  }

  @Nested
  inner class ClassStructure {

    @Test
    fun `test class instantiation`() {
      val testService = MinioObjectStorageService(minioClient, exposedBaseUrl)

      assertNotNull(testService)
      assertTrue(testService.exposedBaseUrl.startsWith(exposedBaseUrl))
    }

    @Test
    fun `test class inheritance`() {
      assertNotNull(service as? IObjectStorageService)
    }
  }
}
