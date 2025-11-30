package io.github.truenine.composeserver.oss.minio

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.*
import io.github.truenine.composeserver.testtoolkit.log
import io.minio.MinioClient
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Share link integration test, specifically for testing the actual availability and end-to-end functionality of share links */
@Testcontainers
class ShareLinkIntegrationTest {

  companion object {
    @Container
    @JvmStatic
    val minioContainer: MinIOContainer = MinIOContainer("minio/minio:RELEASE.2024-11-07T00-52-20Z").withUserName("minioadmin").withPassword("minioadmin")
  }

  private lateinit var minioClient: MinioClient
  private lateinit var service: MinioObjectStorageService
  private val exposedBaseUrl = "http://localhost:9000"

  @BeforeEach
  fun setUp() {
    val endpoint = minioContainer.s3URL
    minioClient = MinioClient.builder().endpoint(endpoint).credentials(minioContainer.userName, minioContainer.password).build()

    service = MinioObjectStorageService(minioClient, exposedBaseUrl)
  }

  @Nested
  inner class ShareLinkGeneration {

    @Test
    fun `test generated share link format is correct`() = runTest {
      val bucketName = "test-link-format-bucket-${System.currentTimeMillis()}"
      val objectName = "test-link-format-object.txt"
      val content = "Hello, Link Format Test!"

      try {
        // Create bucket and object
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, content)

        // Generate share link
        val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = Duration.ofHours(1), method = HttpMethod.GET)

        val result = service.generateShareLink(shareRequest)
        assertTrue(result.isSuccess)

        val shareInfo = result.getOrThrow()
        val shareUrl = shareInfo.shareUrl

        // Verify URL format
        assertTrue(shareUrl.startsWith("http"), "Share link should be an HTTP URL")
        assertTrue(shareUrl.contains(bucketName), "Share link should contain the bucket name")
        assertTrue(shareUrl.contains(objectName), "Share link should contain the object name")
        assertTrue(shareUrl.contains("X-Amz-"), "Share link should contain AWS signature parameters")

        log.info("Generated share link format is correct: $shareUrl")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test share links with different expiration times`() = runTest {
      val bucketName = "test-expiry-bucket-${System.currentTimeMillis()}"
      val objectName = "test-expiry-object.txt"
      val content = "Hello, Expiry Test!"

      try {
        // Create bucket and object
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, content)

        // Test different expiration times
        val expirations = listOf(Duration.ofMinutes(5), Duration.ofHours(1), Duration.ofDays(1))

        for (expiration in expirations) {
          val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = expiration, method = HttpMethod.GET)

          val result = service.generateShareLink(shareRequest)
          assertTrue(result.isSuccess, "Generating share link with expiration ${expiration} should succeed")

          val shareInfo = result.getOrThrow()
          assertTrue(shareInfo.expiration.isAfter(java.time.Instant.now()), "Expiration time should be in the future")

          log.info("Successfully generated share link with expiration ${expiration}")
        }
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ShareLinkDownload {

    @Test
    fun `test content integrity when downloading via share link`() = runTest {
      val bucketName = "test-download-integrity-bucket-${System.currentTimeMillis()}"
      val objectName = "test-download-integrity-object.txt"
      val content = "Hello, Download Integrity Test!\nThis is a test content with multiple lines."

      try {
        // Create bucket and object
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, content)

        // Generate share link
        val shareResult = service.generateSimpleShareLink(bucketName, objectName, Duration.ofMinutes(10))
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // Download via share link
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "Downloading via share link should succeed")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader(Charsets.UTF_8).readText()
          assertEquals(content, downloadedText, "Downloaded content should be identical to the original")
          assertEquals(content.length, downloadedText.length, "Downloaded content size should be the same")
        }

        log.info("Content integrity test for download via share link passed")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test large file download via share link`() = runTest {
      val bucketName = "test-large-file-bucket-${System.currentTimeMillis()}"
      val objectName = "test-large-file-object.txt"

      // Generate large test content (approx. 1MB)
      val largeContent = "Hello, Large File Test!\n".repeat(50000)

      try {
        // Create bucket and object
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, largeContent)

        // Generate share link
        val shareResult = service.generateSimpleShareLink(bucketName, objectName, Duration.ofMinutes(10))
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // Download via share link
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "Downloading large file via share link should succeed")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(largeContent.length, downloadedText.length, "Downloaded large file content length should be the same")
          assertEquals(largeContent, downloadedText, "Downloaded large file content should be identical")
        }

        log.info("Large file download test via share link passed, file size: ${largeContent.length} chars")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class UploadWithLinkFlow {

    @Test
    fun `test end-to-end flow of upload and return link`() = runTest {
      val bucketName = "test-upload-e2e-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-e2e-object.txt"
      val content = "Hello, Upload End-to-End Test!"

      try {
        // Create bucket
        service.createBucket(CreateBucketRequest(bucketName = bucketName))

        // Upload and generate share link
        val uploadRequest =
          UploadWithLinkRequest(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = content.byteInputStream(),
            size = content.length.toLong(),
            contentType = "text/plain",
            shareExpiration = Duration.ofMinutes(15),
            shareMethod = HttpMethod.GET,
          )

        val uploadResult = service.uploadWithLink(uploadRequest)
        assertTrue(uploadResult.isSuccess, "Uploading and generating share link should succeed")

        val response = uploadResult.getOrThrow()

        // Verify upload result
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)

        // Verify that the object actually exists
        val existsResult = service.objectExists(bucketName, objectName)
        assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "The uploaded object should exist")

        // Verify that the share link is usable
        val downloadResult = service.downloadFromShareLink(response.shareLink.shareUrl)
        assertTrue(downloadResult.isSuccess, "Downloading via the generated share link should succeed")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "Content downloaded via share link should match the uploaded content")
        }

        log.info("End-to-end flow of upload and return link test passed")
        log.info("Share link: ${response.shareLink.shareUrl}")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test batch upload and generate share links`() = runTest {
      val bucketName = "test-batch-upload-bucket-${System.currentTimeMillis()}"
      val fileCount = 5

      try {
        // Create bucket
        service.createBucket(CreateBucketRequest(bucketName = bucketName))

        val uploadResults = mutableListOf<UploadWithLinkResponse>()

        // Batch upload files and generate share links
        for (i in 1..fileCount) {
          val objectName = "test-batch-object-$i.txt"
          val content = "Hello, Batch Upload Test $i!"

          val uploadRequest =
            UploadWithLinkRequest(
              bucketName = bucketName,
              objectName = objectName,
              inputStream = content.byteInputStream(),
              size = content.length.toLong(),
              contentType = "text/plain",
              shareExpiration = Duration.ofMinutes(20),
              shareMethod = HttpMethod.GET,
            )

          val uploadResult = service.uploadWithLink(uploadRequest)
          assertTrue(uploadResult.isSuccess, "Batch uploading file #$i should succeed")
          uploadResults.add(uploadResult.getOrThrow())
        }

        // Verify that all share links are usable
        uploadResults.forEachIndexed { index, response ->
          val expectedContent = "Hello, Batch Upload Test ${index + 1}!"

          val downloadResult = service.downloadFromShareLink(response.shareLink.shareUrl)
          assertTrue(downloadResult.isSuccess, "Downloading via share link #${index + 1} should succeed")

          val downloadedContent = downloadResult.getOrThrow()
          downloadedContent.use { objectContent ->
            val downloadedText = objectContent.inputStream.bufferedReader().readText()
            assertEquals(expectedContent, downloadedText, "Content of file #${index + 1} should be consistent")
          }
        }

        log.info("Batch upload and generate share links test passed, processed $fileCount files")
      } finally {
        // Clean up all files
        for (i in 1..fileCount) {
          service.deleteObject(bucketName, "test-batch-object-$i.txt")
        }
        service.deleteBucket(bucketName)
      }
    }
  }
}
