package io.github.truenine.composeserver.oss.volcengine

import com.volcengine.tos.TOSV2
import com.volcengine.tos.TOSV2ClientBuilder
import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.CompleteMultipartUploadRequest
import io.github.truenine.composeserver.oss.CopyObjectRequest
import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.InitiateMultipartUploadRequest
import io.github.truenine.composeserver.oss.ListObjectsRequest
import io.github.truenine.composeserver.oss.ShareLinkRequest
import io.github.truenine.composeserver.oss.UploadPartRequest
import java.io.ByteArrayInputStream
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

/**
 * Volcengine TOS 对象存储服务集成测试
 *
 * 该测试需要真实的 TOS 服务凭证，通过环境变量提供：
 * - VOLCENGINE_TOS_ACCESS_KEY: TOS 访问密钥
 * - VOLCENGINE_TOS_SECRET_KEY: TOS 秘密密钥
 *
 * 如果环境变量不存在，测试将被跳过
 *
 * @author TrueNine
 * @since 2025-08-05
 */
@EnabledIf("hasRequiredEnvironmentVariables")
class VolcengineTosObjectStorageServiceIntegrationTest {

  companion object {
    @JvmStatic private val log = logger<VolcengineTosObjectStorageServiceIntegrationTest>()

    /** 检查是否存在必需的环境变量 用于 JUnit5 的条件测试 */
    @JvmStatic
    fun hasRequiredEnvironmentVariables(): Boolean {
      val accessKey = System.getenv("VOLCENGINE_TOS_ACCESS_KEY")
      val secretKey = System.getenv("VOLCENGINE_TOS_SECRET_KEY")

      val hasCredentials = !accessKey.isNullOrBlank() && !secretKey.isNullOrBlank()

      if (!hasCredentials) {
        log.warn("跳过 Volcengine TOS 集成测试：缺少必需的环境变量 VOLCENGINE_TOS_ACCESS_KEY 或 VOLCENGINE_TOS_SECRET_KEY")
      } else {
        log.info("检测到 Volcengine TOS 凭证，将执行集成测试")
      }

      return hasCredentials
    }
  }

  private lateinit var service: VolcengineTosObjectStorageService
  private lateinit var tosClient: TOSV2
  private lateinit var accessKey: String
  private lateinit var secretKey: String
  private val testBuckets = mutableSetOf<String>()

  @BeforeEach
  fun setUp() {
    // 读取环境变量
    accessKey = System.getenv("VOLCENGINE_TOS_ACCESS_KEY") ?: throw IllegalStateException("VOLCENGINE_TOS_ACCESS_KEY 环境变量未设置")
    secretKey = System.getenv("VOLCENGINE_TOS_SECRET_KEY") ?: throw IllegalStateException("VOLCENGINE_TOS_SECRET_KEY 环境变量未设置")

    log.info("使用 Access Key: ${accessKey.take(8)}... 进行集成测试")

    // 创建真实的 TOS 客户端
    tosClient = TOSV2ClientBuilder().build("cn-beijing", "https://tos-cn-beijing.volces.com", accessKey, secretKey)

    // 创建服务实例
    service = VolcengineTosObjectStorageService(tosClient = tosClient, exposedBaseUrl = "https://tos-cn-beijing.volces.com")

    log.info("TOS 客户端和服务实例创建完成")
  }

  @AfterEach
  fun tearDown() {
    // 清理所有测试创建的存储桶
    testBuckets.forEach { bucketName ->
      try {
        runBlocking {
          // 先删除桶中的所有对象
          val listResult = service.listObjects(ListObjectsRequest(bucketName = bucketName))
          if (listResult.isSuccess) {
            val objects = listResult.getOrThrow().objects
            objects.forEach { obj -> service.deleteObject(bucketName, obj.objectName) }
          }
          // 然后删除存储桶
          service.deleteBucket(bucketName)
        }
        log.info("清理测试存储桶: {}", bucketName)
      } catch (e: Exception) {
        log.warn("清理存储桶失败: {} - {}", bucketName, e.message)
      }
    }
    testBuckets.clear()
  }

  @Nested
  inner class HealthCheck {

    @Test
    fun `测试健康检查`() = runBlocking {
      log.info("开始健康检查测试")

      val result = service.isHealthy()
      assertTrue(result, "TOS 服务健康检查应该成功")

      log.info("健康检查测试完成")
    }
  }

  @Nested
  inner class BucketOperations {

    @Test
    fun `测试存储桶基本操作`() = runBlocking {
      val testBucketName = "test-integration-bucket-${System.currentTimeMillis()}"
      testBuckets.add(testBucketName)
      log.info("开始存储桶操作测试，桶名: {}", testBucketName)

      // 1. 创建存储桶
      val createResult = service.createBucket(CreateBucketRequest(testBucketName))
      assertTrue(createResult.isSuccess, "创建存储桶应该成功")
      val bucketInfo = createResult.getOrThrow()
      assertEquals(testBucketName, bucketInfo.name)

      // 2. 检查存储桶是否存在
      val existsResult = service.bucketExists(testBucketName)
      assertTrue(existsResult.isSuccess, "检查存储桶存在应该成功")
      assertTrue(existsResult.getOrThrow(), "存储桶应该存在")

      // 3. 列出存储桶
      val listResult = service.listBuckets()
      assertTrue(listResult.isSuccess, "列出存储桶应该成功")
      val buckets = listResult.getOrThrow()
      assertTrue(buckets.any { it.name == testBucketName }, "存储桶列表应该包含新创建的桶")

      log.info("存储桶操作测试完成")
    }

    @Test
    fun `测试存储桶权限设置`() = runBlocking {
      val testBucketName = "test-integration-acl-bucket-${System.currentTimeMillis()}"
      testBuckets.add(testBucketName)
      log.info("开始存储桶权限测试，桶名: {}", testBucketName)

      // 创建存储桶
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()

      // 设置公共读权限
      val aclResult = service.setBucketPublicRead(testBucketName)
      assertTrue(aclResult.isSuccess, "设置存储桶公共读权限应该成功")

      log.info("存储桶权限测试完成")
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `测试对象基本操作`() = runBlocking {
      val testBucketName = "test-integration-object-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-object.txt"
      val testContent = "Hello, Volcengine TOS Integration Test!"
      testBuckets.add(testBucketName)

      log.info("开始对象操作测试，桶名: {}, 对象名: {}", testBucketName, testObjectName)

      // 1. 创建存储桶
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()

      // 2. 上传对象
      val inputStream = ByteArrayInputStream(testContent.toByteArray())
      val uploadResult =
        service.putObject(
          bucketName = testBucketName,
          objectName = testObjectName,
          inputStream = inputStream,
          size = testContent.length.toLong(),
          contentType = "text/plain",
        )
      assertTrue(uploadResult.isSuccess, "上传对象应该成功")
      val objectInfo = uploadResult.getOrThrow()
      assertEquals(testBucketName, objectInfo.bucketName)
      assertEquals(testObjectName, objectInfo.objectName)

      // 3. 检查对象是否存在
      val existsResult = service.objectExists(testBucketName, testObjectName)
      assertTrue(existsResult.isSuccess, "检查对象存在应该成功")
      assertTrue(existsResult.getOrThrow(), "对象应该存在")

      // 4. 获取对象信息
      val infoResult = service.getObjectInfo(testBucketName, testObjectName)
      assertTrue(infoResult.isSuccess, "获取对象信息应该成功")
      val retrievedInfo = infoResult.getOrThrow()
      assertEquals(testBucketName, retrievedInfo.bucketName)
      assertEquals(testObjectName, retrievedInfo.objectName)

      // 5. 下载对象
      val downloadResult = service.getObject(testBucketName, testObjectName)
      assertTrue(downloadResult.isSuccess, "下载对象应该成功")
      val objectContent = downloadResult.getOrThrow()
      val downloadedContent = objectContent.inputStream.readBytes().toString(Charsets.UTF_8)
      assertEquals(testContent, downloadedContent, "下载的内容应该与上传的内容一致")

      // 6. 列出对象
      val listResult = service.listObjects(ListObjectsRequest(bucketName = testBucketName))
      assertTrue(listResult.isSuccess, "列出对象应该成功")
      val objects = listResult.getOrThrow().objects
      assertTrue(objects.any { it.objectName == testObjectName }, "对象列表应该包含上传的对象")

      log.info("对象操作测试完成")
    }

    @Test
    fun `测试对象复制操作`() = runBlocking {
      val testBucketName = "test-integration-copy-bucket-${System.currentTimeMillis()}"
      val sourceObjectName = "source-object.txt"
      val targetObjectName = "target-object.txt"
      val testContent = "Content for copy test"
      testBuckets.add(testBucketName)

      log.info("开始对象复制测试")

      // 创建存储桶并上传源对象
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()
      val inputStream = ByteArrayInputStream(testContent.toByteArray())
      service.putObject(testBucketName, sourceObjectName, inputStream, testContent.length.toLong()).getOrThrow()

      // 复制对象
      val copyRequest =
        CopyObjectRequest(
          sourceBucketName = testBucketName,
          sourceObjectName = sourceObjectName,
          destinationBucketName = testBucketName,
          destinationObjectName = targetObjectName,
        )
      val copyResult = service.copyObject(copyRequest)
      assertTrue(copyResult.isSuccess, "复制对象应该成功")

      // 验证目标对象存在
      val existsResult = service.objectExists(testBucketName, targetObjectName)
      assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "复制的对象应该存在")

      log.info("对象复制测试完成")
    }
  }

  @Nested
  inner class PresignedUrlOperations {

    @Test
    fun `测试预签名URL生成`() = runBlocking {
      val testBucketName = "test-integration-presigned-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-presigned-object.txt"
      testBuckets.add(testBucketName)

      log.info("开始预签名URL测试，桶名: {}, 对象名: {}", testBucketName, testObjectName)

      // 1. 创建存储桶和对象
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()
      val inputStream = ByteArrayInputStream("test content".toByteArray())
      service.putObject(testBucketName, testObjectName, inputStream, 12L).getOrThrow()

      // 2. 生成GET预签名URL
      val getUrlResult =
        service.generatePresignedUrl(bucketName = testBucketName, objectName = testObjectName, expiration = Duration.ofHours(1), method = HttpMethod.GET)
      assertTrue(getUrlResult.isSuccess, "生成GET预签名URL应该成功")
      val getPresignedUrl = getUrlResult.getOrThrow()
      assertTrue(getPresignedUrl.isNotEmpty(), "GET预签名URL不应该为空")
      assertTrue(getPresignedUrl.startsWith("https://"), "GET预签名URL应该是HTTPS协议")

      // 3. 生成PUT预签名URL
      val putUrlResult =
        service.generatePresignedUrl(
          bucketName = testBucketName,
          objectName = "test-put-object.txt",
          expiration = Duration.ofMinutes(30),
          method = HttpMethod.PUT,
        )
      assertTrue(putUrlResult.isSuccess, "生成PUT预签名URL应该成功")
      val putPresignedUrl = putUrlResult.getOrThrow()
      assertTrue(putPresignedUrl.isNotEmpty(), "PUT预签名URL不应该为空")
      assertTrue(putPresignedUrl.startsWith("https://"), "PUT预签名URL应该是HTTPS协议")

      log.info("预签名URL测试完成")
    }
  }

  @Nested
  inner class MultipartUploadOperations {

    @Test
    fun `测试多部分上传`() = runBlocking {
      val testBucketName = "test-integration-multipart-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-multipart-object.txt"
      // TOS requires each part (except the last one) to be at least 5MB
      val partContent1 = "A".repeat(5 * 1024 * 1024) // 5MB
      val partContent2 = "B".repeat(5 * 1024 * 1024) // 5MB
      testBuckets.add(testBucketName)

      log.info("开始多部分上传测试")

      // 创建存储桶
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()

      // 1. 初始化多部分上传
      val initiateRequest = InitiateMultipartUploadRequest(bucketName = testBucketName, objectName = testObjectName)
      val initiateResult = service.initiateMultipartUpload(initiateRequest)
      assertTrue(initiateResult.isSuccess, "初始化多部分上传应该成功")
      val multipartUpload = initiateResult.getOrThrow()
      assertNotNull(multipartUpload.uploadId, "上传ID不应该为空")

      try {
        // 2. 上传第一部分
        val part1Request =
          UploadPartRequest(
            bucketName = testBucketName,
            objectName = testObjectName,
            uploadId = multipartUpload.uploadId,
            partNumber = 1,
            inputStream = ByteArrayInputStream(partContent1.toByteArray()),
            size = partContent1.length.toLong(),
          )
        val part1Result = service.uploadPart(part1Request)
        assertTrue(part1Result.isSuccess, "上传第一部分应该成功")
        val part1Info = part1Result.getOrThrow()

        // 3. 上传第二部分
        val part2Request =
          UploadPartRequest(
            bucketName = testBucketName,
            objectName = testObjectName,
            uploadId = multipartUpload.uploadId,
            partNumber = 2,
            inputStream = ByteArrayInputStream(partContent2.toByteArray()),
            size = partContent2.length.toLong(),
          )
        val part2Result = service.uploadPart(part2Request)
        assertTrue(part2Result.isSuccess, "上传第二部分应该成功")
        val part2Info = part2Result.getOrThrow()

        // 4. 列出已上传的部分
        val listPartsResult = service.listParts(multipartUpload.uploadId, testBucketName, testObjectName)
        assertTrue(listPartsResult.isSuccess, "列出部分应该成功")
        val parts = listPartsResult.getOrThrow()
        assertEquals(2, parts.size, "应该有两个部分")

        // 5. 完成多部分上传
        val completeRequest =
          CompleteMultipartUploadRequest(
            bucketName = testBucketName,
            objectName = testObjectName,
            uploadId = multipartUpload.uploadId,
            parts = listOf(part1Info, part2Info),
          )
        val completeResult = service.completeMultipartUpload(completeRequest)
        assertTrue(completeResult.isSuccess, "完成多部分上传应该成功")

        // 6. 验证对象存在
        val existsResult = service.objectExists(testBucketName, testObjectName)
        assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "多部分上传的对象应该存在")

        log.info("多部分上传测试完成")
      } catch (e: Exception) {
        // 如果出现异常，尝试中止上传
        service.abortMultipartUpload(multipartUpload.uploadId, testBucketName, testObjectName)
        throw e
      }
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `测试分享链接生成和验证`() = runBlocking {
      val testBucketName = "test-integration-share-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-share-object.txt"
      val testContent = "Content for share link test"
      testBuckets.add(testBucketName)

      log.info("开始分享链接测试")

      // 创建存储桶并上传对象
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()
      val inputStream = ByteArrayInputStream(testContent.toByteArray())
      service.putObject(testBucketName, testObjectName, inputStream, testContent.length.toLong()).getOrThrow()

      // 生成分享链接
      val shareRequest = ShareLinkRequest(bucketName = testBucketName, objectName = testObjectName, expiration = Duration.ofHours(2), method = HttpMethod.GET)
      val shareResult = service.generateShareLink(shareRequest)
      assertTrue(shareResult.isSuccess, "生成分享链接应该成功")
      val shareInfo = shareResult.getOrThrow()
      assertTrue(shareInfo.shareUrl.isNotEmpty(), "分享链接不应该为空")

      // 验证分享链接
      val validateResult = service.validateShareLink(shareInfo.shareUrl)
      assertTrue(validateResult.isSuccess, "验证分享链接应该成功")

      log.info("分享链接测试完成")
    }
  }

  @Nested
  inner class EnvironmentVariables {

    @Test
    fun `测试环境变量读取`() {
      log.info("验证环境变量读取")

      assertNotNull(accessKey, "VOLCENGINE_TOS_ACCESS_KEY 应该不为空")
      assertNotNull(secretKey, "VOLCENGINE_TOS_SECRET_KEY 应该不为空")
      assertTrue(accessKey.isNotBlank(), "VOLCENGINE_TOS_ACCESS_KEY 应该不为空白")
      assertTrue(secretKey.isNotBlank(), "VOLCENGINE_TOS_SECRET_KEY 应该不为空白")

      log.info("环境变量验证完成")
    }
  }
}
