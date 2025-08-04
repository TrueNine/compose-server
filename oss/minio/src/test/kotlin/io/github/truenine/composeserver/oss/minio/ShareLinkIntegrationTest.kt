package io.github.truenine.composeserver.oss.minio

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.*
import io.github.truenine.composeserver.testtoolkit.log
import io.minio.MinioClient
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/** 分享链接集成测试 专门测试分享链接的实际可用性和端到端功能 */
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
    fun `测试生成的分享链接格式正确`() = runTest {
      val bucketName = "test-link-format-bucket-${System.currentTimeMillis()}"
      val objectName = "test-link-format-object.txt"
      val content = "Hello, Link Format Test!"

      try {
        // 创建桶和对象
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, content)

        // 生成分享链接
        val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = Duration.ofHours(1), method = HttpMethod.GET)

        val result = service.generateShareLink(shareRequest)
        assertTrue(result.isSuccess)

        val shareInfo = result.getOrThrow()
        val shareUrl = shareInfo.shareUrl

        // 验证URL格式
        assertTrue(shareUrl.startsWith("http"), "分享链接应该是HTTP URL")
        assertTrue(shareUrl.contains(bucketName), "分享链接应该包含桶名")
        assertTrue(shareUrl.contains(objectName), "分享链接应该包含对象名")
        assertTrue(shareUrl.contains("X-Amz-"), "分享链接应该包含AWS签名参数")

        log.info("生成的分享链接格式正确: $shareUrl")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试不同过期时间的分享链接`() = runTest {
      val bucketName = "test-expiry-bucket-${System.currentTimeMillis()}"
      val objectName = "test-expiry-object.txt"
      val content = "Hello, Expiry Test!"

      try {
        // 创建桶和对象
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, content)

        // 测试不同的过期时间
        val expirations = listOf(Duration.ofMinutes(5), Duration.ofHours(1), Duration.ofDays(1))

        for (expiration in expirations) {
          val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = expiration, method = HttpMethod.GET)

          val result = service.generateShareLink(shareRequest)
          assertTrue(result.isSuccess, "生成${expiration}过期时间的分享链接应该成功")

          val shareInfo = result.getOrThrow()
          assertTrue(shareInfo.expiration.isAfter(java.time.Instant.now()), "过期时间应该在未来")

          log.info("生成${expiration}过期时间的分享链接成功")
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
    fun `测试通过分享链接下载内容完整性`() = runTest {
      val bucketName = "test-download-integrity-bucket-${System.currentTimeMillis()}"
      val objectName = "test-download-integrity-object.txt"
      val content = "Hello, Download Integrity Test!\n这是一个包含中文的测试内容。\n包含多行文本。"

      try {
        // 创建桶和对象
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, content)

        // 生成分享链接
        val shareResult = service.generateSimpleShareLink(bucketName, objectName, Duration.ofMinutes(10))
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // 通过分享链接下载
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "通过分享链接下载应该成功")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader(Charsets.UTF_8).readText()
          assertEquals(content, downloadedText, "下载的内容应该与原始内容完全一致")
          assertEquals(content.length, downloadedText.length, "下载的内容大小应该一致")
        }

        log.info("通过分享链接下载内容完整性测试通过")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试大文件分享链接下载`() = runTest {
      val bucketName = "test-large-file-bucket-${System.currentTimeMillis()}"
      val objectName = "test-large-file-object.txt"

      // 生成较大的测试内容（约1MB）
      val largeContent = "Hello, Large File Test!\n".repeat(50000)

      try {
        // 创建桶和对象
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(bucketName, objectName, largeContent)

        // 生成分享链接
        val shareResult = service.generateSimpleShareLink(bucketName, objectName, Duration.ofMinutes(10))
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // 通过分享链接下载
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "通过分享链接下载大文件应该成功")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(largeContent.length, downloadedText.length, "下载的大文件内容长度应该一致")
          assertEquals(largeContent, downloadedText, "下载的大文件内容应该完全一致")
        }

        log.info("大文件分享链接下载测试通过，文件大小: ${largeContent.length} 字符")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class UploadWithLinkFlow {

    @Test
    fun `测试上传并返回链接的端到端流程`() = runTest {
      val bucketName = "test-upload-e2e-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-e2e-object.txt"
      val content = "Hello, Upload End-to-End Test!"

      try {
        // 创建桶
        service.createBucket(CreateBucketRequest(bucketName = bucketName))

        // 上传并生成分享链接
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
        assertTrue(uploadResult.isSuccess, "上传并生成分享链接应该成功")

        val response = uploadResult.getOrThrow()

        // 验证上传结果
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)

        // 验证对象确实存在
        val existsResult = service.objectExists(bucketName, objectName)
        assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "上传的对象应该存在")

        // 验证分享链接可用
        val downloadResult = service.downloadFromShareLink(response.shareLink.shareUrl)
        assertTrue(downloadResult.isSuccess, "通过生成的分享链接下载应该成功")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "通过分享链接下载的内容应该与上传的内容一致")
        }

        log.info("上传并返回链接的端到端流程测试通过")
        log.info("分享链接: ${response.shareLink.shareUrl}")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试批量上传并生成分享链接`() = runTest {
      val bucketName = "test-batch-upload-bucket-${System.currentTimeMillis()}"
      val fileCount = 5

      try {
        // 创建桶
        service.createBucket(CreateBucketRequest(bucketName = bucketName))

        val uploadResults = mutableListOf<UploadWithLinkResponse>()

        // 批量上传文件并生成分享链接
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
          assertTrue(uploadResult.isSuccess, "批量上传第${i}个文件应该成功")
          uploadResults.add(uploadResult.getOrThrow())
        }

        // 验证所有分享链接都可用
        uploadResults.forEachIndexed { index, response ->
          val expectedContent = "Hello, Batch Upload Test ${index + 1}!"

          val downloadResult = service.downloadFromShareLink(response.shareLink.shareUrl)
          assertTrue(downloadResult.isSuccess, "第${index + 1}个分享链接下载应该成功")

          val downloadedContent = downloadResult.getOrThrow()
          downloadedContent.use { objectContent ->
            val downloadedText = objectContent.inputStream.bufferedReader().readText()
            assertEquals(expectedContent, downloadedText, "第${index + 1}个文件内容应该一致")
          }
        }

        log.info("批量上传并生成分享链接测试通过，共处理 $fileCount 个文件")
      } finally {
        // 清理所有文件
        for (i in 1..fileCount) {
          service.deleteObject(bucketName, "test-batch-object-$i.txt")
        }
        service.deleteBucket(bucketName)
      }
    }
  }
}
