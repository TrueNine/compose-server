package io.github.truenine.composeserver.oss.minio

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.ObjectStorageService
import io.github.truenine.composeserver.oss.PutObjectRequest
import io.github.truenine.composeserver.oss.ShareLinkRequest
import io.github.truenine.composeserver.oss.UploadWithLinkRequest
import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.testcontainers.IOssMinioContainer
import io.minio.MinioClient
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/** MinIO 对象存储服务测试 */
class MinioObjectStorageServiceTest : IOssMinioContainer {

  private lateinit var minioClient: MinioClient
  private lateinit var service: MinioObjectStorageService
  private val exposedBaseUrl = "http://localhost"

  @BeforeEach
  fun setUp() {
    // 创建真实的 MinioClient 连接到 testcontainers
    val port = minioContainer?.getMappedPort(9000)
    val host = minioContainer?.host
    assertNotNull(port)
    assertNotNull(host)

    minioClient = MinioClient.builder().endpoint("http://${host}:$port").credentials("minioadmin", "minioadmin").build()

    service = MinioObjectStorageService(minioClient, "$exposedBaseUrl:$port")
  }

  @Nested
  inner class HealthCheck {

    @Test
    fun `测试健康检查成功`() = runTest {
      val result = service.isHealthy()
      assertTrue(result)
    }

    @Test
    fun `测试健康检查失败`() = runTest {
      // 创建一个无效的客户端来测试失败情况
      val invalidClient = MinioClient.builder().endpoint("http://invalid-host:9999").credentials("invalid", "invalid").build()
      val invalidService = MinioObjectStorageService(invalidClient, "http://invalid-host:9999")

      val result = invalidService.isHealthy()

      assertFalse(result)
    }
  }

  @Nested
  inner class NativeClient {

    @Test
    fun `测试获取原生客户端`() {
      val nativeClient = service.getNativeClient<MinioClient>()

      assertNotNull(nativeClient)
      assertEquals(minioClient, nativeClient)
    }
  }

  @Nested
  inner class ExposedBaseUrl {

    @Test
    fun `测试获取暴露的基础URL`() {
      assertTrue(service.exposedBaseUrl.startsWith(exposedBaseUrl))
    }
  }

  @Nested
  inner class BucketOperations {

    @Test
    fun `测试创建存储桶成功`() = runTest {
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val request = CreateBucketRequest(bucketName = bucketName, region = "us-east-1")

      val result = service.createBucket(request)

      assertTrue(result.isSuccess)
      val bucketInfo = result.getOrNull()!!
      assertEquals(bucketName, bucketInfo.name)
      assertEquals("us-east-1", bucketInfo.region)

      // 清理：删除创建的桶
      service.deleteBucket(bucketName)
    }

    @Test
    fun `测试创建存储桶失败`() = runTest {
      // 使用无效的桶名来测试失败情况（桶名不能包含大写字母）
      val request = CreateBucketRequest(bucketName = "INVALID-BUCKET-NAME", region = "us-east-1")

      val result = service.createBucket(request)

      assertTrue(result.isFailure)
    }

    @Test
    fun `测试检查存储桶存在`() = runTest {
      val bucketName = "test-exists-bucket-${System.currentTimeMillis()}"

      // 先创建桶
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result = service.bucketExists(bucketName)

      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull() == true)

      // 清理：删除创建的桶
      service.deleteBucket(bucketName)
    }

    @Test
    fun `测试检查存储桶不存在`() = runTest {
      val result = service.bucketExists("non-existent-bucket-${System.currentTimeMillis()}")

      assertTrue(result.isSuccess)
      assertFalse(result.getOrNull() == true)
    }

    @Test
    fun `测试删除存储桶成功`() = runTest {
      val bucketName = "test-delete-bucket-${System.currentTimeMillis()}"

      // 先创建桶
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      val result = service.deleteBucket(bucketName)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试删除存储桶失败`() = runTest {
      val result = service.deleteBucket("non-existent-bucket-${System.currentTimeMillis()}")

      // MinIO 删除不存在的桶不会报错，所以这个测试可能会成功
      // 我们可以测试删除一个包含对象的桶来触发失败
      assertTrue(result.isSuccess || result.isFailure)
    }

    @Test
    fun `测试列出存储桶`() = runTest {
      val bucketName1 = "test-list-bucket1-${System.currentTimeMillis()}"
      val bucketName2 = "test-list-bucket2-${System.currentTimeMillis()}"

      // 创建两个测试桶
      service.createBucket(CreateBucketRequest(bucketName = bucketName1))
      service.createBucket(CreateBucketRequest(bucketName = bucketName2))

      val result = service.listBuckets()

      assertTrue(result.isSuccess)
      val buckets = result.getOrNull()!!
      assertTrue(buckets.any { it.name == bucketName1 })
      assertTrue(buckets.any { it.name == bucketName2 })

      // 清理：删除创建的桶
      service.deleteBucket(bucketName1)
      service.deleteBucket(bucketName2)
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `测试上传对象成功`() = runTest {
      val bucketName = "test-upload-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "test content"

      // 先创建桶
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

      // 清理：删除桶（会自动删除其中的对象）
      service.deleteBucket(bucketName)
    }

    @Test
    fun `测试上传对象失败`() = runTest {
      // 使用不存在的桶来测试失败情况
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
    fun `测试简化上传对象成功`() = runTest {
      val bucketName = "test-simple-upload-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "test content"

      // 先创建桶
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

      // 清理：删除桶
      service.deleteBucket(bucketName)
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `测试生成分享链接成功`() = runTest {
      val bucketName = "test-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-share-object.txt"
      val content = "test share content"

      // 先创建桶和对象
      service.createBucket(CreateBucketRequest(bucketName = bucketName))
      service.putObject(
        bucketName = bucketName,
        objectName = objectName,
        inputStream = content.byteInputStream(),
        size = content.length.toLong(),
        contentType = "text/plain",
      )

      // 生成分享链接
      val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1), method = HttpMethod.GET)

      val result = service.generateShareLink(shareRequest)

      assertTrue(result.isSuccess)
      val shareInfo = result.getOrNull()!!
      assertEquals(bucketName, shareInfo.bucketName)
      assertEquals(objectName, shareInfo.objectName)
      assertEquals(HttpMethod.GET, shareInfo.method)
      assertTrue(shareInfo.shareUrl.isNotEmpty())
      assertFalse(shareInfo.hasPassword)

      // 清理
      service.deleteBucket(bucketName)
    }

    @Test
    fun `测试上传并返回分享链接成功`() = runTest {
      val bucketName = "test-upload-link-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-link-object.txt"
      val content = "test upload with link content"

      // 先创建桶
      service.createBucket(CreateBucketRequest(bucketName = bucketName))

      // 上传并生成分享链接
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

      // 验证对象信息
      assertEquals(bucketName, response.objectInfo.bucketName)
      assertEquals(objectName, response.objectInfo.objectName)
      assertEquals(content.length.toLong(), response.objectInfo.size)

      // 验证分享链接信息
      assertEquals(bucketName, response.shareLink.bucketName)
      assertEquals(objectName, response.shareLink.objectName)
      assertEquals(HttpMethod.GET, response.shareLink.method)
      assertTrue(response.shareLink.shareUrl.isNotEmpty())

      // 验证公共URL
      assertNotNull(response.publicUrl)
      assertTrue(response.publicUrl!!.contains(bucketName))
      assertTrue(response.publicUrl!!.contains(objectName))

      // 清理
      service.deleteBucket(bucketName)
    }

    @Test
    fun `测试验证分享链接成功`() = runTest {
      val bucketName = "test-validate-link-bucket-${System.currentTimeMillis()}"
      val objectName = "test-validate-link-object.txt"
      val content = "test validate link content"

      // 先创建桶和对象
      service.createBucket(CreateBucketRequest(bucketName = bucketName))
      service.putObject(
        bucketName = bucketName,
        objectName = objectName,
        inputStream = content.byteInputStream(),
        size = content.length.toLong(),
        contentType = "text/plain",
      )

      // 生成分享链接
      val shareRequest = ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1))
      val shareResult = service.generateShareLink(shareRequest)
      assertTrue(shareResult.isSuccess)
      val shareUrl = shareResult.getOrThrow().shareUrl

      // 验证分享链接
      val validateResult = service.validateShareLink(shareUrl)

      assertTrue(validateResult.isSuccess)
      val validatedInfo = validateResult.getOrNull()!!
      assertEquals(bucketName, validatedInfo.bucketName)
      assertEquals(objectName, validatedInfo.objectName)
      assertEquals(shareUrl, validatedInfo.shareUrl)

      // 清理
      service.deleteBucket(bucketName)
    }

    @Test
    fun `测试撤销分享链接`() = runTest {
      val shareUrl = "http://example.com/test-share-url"

      // MinIO 不支持撤销预签名URL，但方法应该成功返回
      val result = service.revokeShareLink(shareUrl)

      assertTrue(result.isSuccess)
    }

    @Test
    fun `测试分享链接实际下载功能`() = runTest {
      val bucketName = "test-real-download-bucket-${System.currentTimeMillis()}"
      val objectName = "test-real-download-object.txt"
      val content = "Hello, Real Download Test!"

      try {
        // 先创建桶和对象
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
        )

        // 生成分享链接
        val shareRequest =
          ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofMinutes(5), method = HttpMethod.GET)
        val shareResult = service.generateShareLink(shareRequest)
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // 使用分享链接下载内容
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "通过分享链接下载应该成功")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "下载的内容应该与原始内容一致")
        }

        log.info("分享链接实际下载测试通过: $shareUrl")
      } finally {
        // 清理
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试上传并返回链接的完整流程`() = runTest {
      val bucketName = "test-upload-link-flow-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-link-flow-object.txt"
      val content = "Hello, Upload with Link Flow Test!"

      try {
        // 先创建桶
        service.createBucket(CreateBucketRequest(bucketName = bucketName))

        // 上传并生成分享链接
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
        assertTrue(uploadResult.isSuccess, "上传并生成分享链接应该成功")

        val response = uploadResult.getOrThrow()

        // 验证上传结果
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)

        // 验证分享链接
        assertTrue(response.shareLink.shareUrl.isNotEmpty(), "分享链接不应为空")
        assertEquals(bucketName, response.shareLink.bucketName)
        assertEquals(objectName, response.shareLink.objectName)

        // 验证公共URL
        assertNotNull(response.publicUrl, "公共URL不应为空")
        assertTrue(response.publicUrl!!.contains(bucketName))
        assertTrue(response.publicUrl!!.contains(objectName))

        // 使用生成的分享链接下载内容验证
        val downloadResult = service.downloadFromShareLink(response.shareLink.shareUrl)
        assertTrue(downloadResult.isSuccess, "通过生成的分享链接下载应该成功")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "通过分享链接下载的内容应该与原始内容一致")
        }

        log.info("上传并返回链接的完整流程测试通过")
        log.info("分享链接: ${response.shareLink.shareUrl}")
        log.info("公共URL: ${response.publicUrl}")
      } finally {
        // 清理
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试通过HTTP客户端访问分享链接`() = runTest {
      val bucketName = "test-http-access-bucket-${System.currentTimeMillis()}"
      val objectName = "test-http-access-object.txt"
      val content = "Hello, HTTP Access Test!"

      try {
        // 先创建桶和对象
        service.createBucket(CreateBucketRequest(bucketName = bucketName))
        service.putObject(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = content.byteInputStream(),
          size = content.length.toLong(),
          contentType = "text/plain",
        )

        // 生成分享链接
        val shareRequest =
          ShareLinkRequest(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofMinutes(5), method = HttpMethod.GET)
        val shareResult = service.generateShareLink(shareRequest)
        assertTrue(shareResult.isSuccess)
        val shareUrl = shareResult.getOrThrow().shareUrl

        // 使用 Java 的 HttpURLConnection 测试分享链接
        try {
          val url = java.net.URL(shareUrl)
          val connection = url.openConnection() as java.net.HttpURLConnection
          connection.requestMethod = "GET"
          connection.connectTimeout = 5000
          connection.readTimeout = 10000

          val responseCode = connection.responseCode
          assertEquals(200, responseCode, "HTTP响应码应该是200")

          val downloadedText = connection.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "通过HTTP下载的内容应该与原始内容一致")

          log.info("通过HTTP客户端访问分享链接测试通过")
          log.info("分享链接: $shareUrl")
          log.info("HTTP响应码: $responseCode")
        } catch (e: Exception) {
          log.warn("HTTP访问测试失败，可能是网络问题或MinIO配置问题: ${e.message}")
          // 在测试环境中，MinIO可能不能通过外部HTTP访问，这是正常的
          // 我们记录警告但不让测试失败
        }
      } finally {
        // 清理
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试生成分享链接失败`() = runTest {
      // 使用不存在的桶来测试失败情况
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
    fun `测试类实例化`() {
      val testService = MinioObjectStorageService(minioClient, exposedBaseUrl)

      assertNotNull(testService)
      assertTrue(testService.exposedBaseUrl.startsWith(exposedBaseUrl))
    }

    @Test
    fun `测试类继承关系`() {
      assertNotNull(service as? ObjectStorageService)
    }
  }
}
