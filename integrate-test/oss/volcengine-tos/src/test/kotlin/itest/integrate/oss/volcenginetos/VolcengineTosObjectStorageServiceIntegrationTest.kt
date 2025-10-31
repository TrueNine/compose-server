package itest.integrate.oss.volcenginetos

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
import io.github.truenine.composeserver.oss.volcengine.VolcengineTosObjectStorageService
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

import io.github.truenine.composeserver.oss.CorsRule
import io.github.truenine.composeserver.oss.ListObjectVersionsRequest
import io.github.truenine.composeserver.oss.Tag
import io.github.truenine.composeserver.oss.LifecycleRule
import io.github.truenine.composeserver.oss.LifecycleRuleStatus
import io.github.truenine.composeserver.oss.LifecycleExpiration

/**
 * Volcengine TOS Object Storage Service Integration Tests
 *
 * These tests require real TOS service credentials provided through environment variables:
 * - VOLCENGINE_TOS_ACCESS_KEY: TOS access key
 * - VOLCENGINE_TOS_SECRET_KEY: TOS secret key
 *
 * If environment variables are not present, tests will be skipped
 *
 * @author TrueNine
 * @since 2025-08-05
 */
@EnabledIf("hasRequiredEnvironmentVariables")
class VolcengineTosObjectStorageServiceIntegrationTest {

  companion object {
    @JvmStatic private val log = logger<VolcengineTosObjectStorageServiceIntegrationTest>()

    /** Check if required environment variables exist for JUnit5 conditional testing */
    @JvmStatic fun hasRequiredEnvironmentVariables() = hasTosRequiredEnvironmentVariables()
  }

  private lateinit var service: VolcengineTosObjectStorageService
  private lateinit var tosClient: TOSV2
  private lateinit var accessKey: String
  private lateinit var secretKey: String
  private val testBuckets = mutableSetOf<String>()

  @BeforeEach
  fun setUp() {
    getTosAkSk()?.also {
      accessKey = it.ak
      secretKey = it.sk
      log.info("Using Access Key: ${accessKey.take(8)}... for integration testing")

      // Create real TOS client
      tosClient = TOSV2ClientBuilder().build("cn-beijing", it.endpoint, accessKey, secretKey)

      // Create service instance
      service = VolcengineTosObjectStorageService(tosClient = tosClient, exposedBaseUrl = it.endpoint)

      log.info("TOS client and service instance created successfully")
    }
  }

  @AfterEach
  fun tearDown() {
    // Clean up all test-created buckets
    testBuckets.forEach { bucketName ->
      try {
        runBlocking {
          // First delete all objects in the bucket
          val listResult = service.listObjects(ListObjectsRequest(bucketName = bucketName))
          if (listResult.isSuccess) {
            val objects = listResult.getOrThrow().objects
            objects.forEach { obj -> service.deleteObject(bucketName, obj.objectName) }
          }
          // Then delete the bucket
          service.deleteBucket(bucketName)
        }
        log.info("Cleaned up test bucket: {}", bucketName)
      } catch (e: Exception) {
        log.warn("Failed to clean up bucket: {} - {}", bucketName, e.message)
      }
    }
    testBuckets.clear()
  }

  @Nested
  inner class HealthCheck {

    @Test
    fun `test health check`() = runBlocking {
      log.info("Starting health check test")

      val result = service.isHealthy()
      assertTrue(result, "TOS service health check should succeed")

      log.info("Health check test completed")
    }
  }

  @Nested
  inner class BucketOperations {

    @Test
    fun `test bucket basic operations`() = runBlocking {
      val testBucketName = "test-integration-bucket-${System.currentTimeMillis()}"
      testBuckets.add(testBucketName)
      log.info("Starting bucket operations test, bucket name: {}", testBucketName)

      // 1. Create bucket
      val createResult = service.createBucket(CreateBucketRequest(testBucketName))
      assertTrue(createResult.isSuccess, "Creating bucket should succeed")
      val bucketInfo = createResult.getOrThrow()
      assertEquals(testBucketName, bucketInfo.name)

      // 2. Check if bucket exists
      val existsResult = service.bucketExists(testBucketName)
      assertTrue(existsResult.isSuccess, "Checking bucket existence should succeed")
      assertTrue(existsResult.getOrThrow(), "Bucket should exist")

      // 3. List buckets
      val listResult = service.listBuckets()
      assertTrue(listResult.isSuccess, "Listing buckets should succeed")
      val buckets = listResult.getOrThrow()
      assertTrue(buckets.any { it.name == testBucketName }, "Bucket list should contain the newly created bucket")

      log.info("Bucket operations test completed")
    }

    @Test
    fun `test bucket permission settings`() = runBlocking {
      val testBucketName = "test-integration-acl-bucket-${System.currentTimeMillis()}"
      testBuckets.add(testBucketName)
      log.info("Starting bucket permission test, bucket name: {}", testBucketName)

      // Create bucket
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()

      // Set public read permission
      val aclResult = service.setBucketPublicRead(testBucketName)
      assertTrue(aclResult.isSuccess, "Setting bucket public read permission should succeed")

      log.info("Bucket permission test completed")
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `test object basic operations`() = runBlocking {
      val testBucketName = "test-integration-object-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-object.txt"
      val testContent = "Hello, Volcengine TOS Integration Test!"
      testBuckets.add(testBucketName)

      log.info("Starting object operations test, bucket name: {}, object name: {}", testBucketName, testObjectName)

      // 1. Create bucket
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()

      // 2. Upload object
      val inputStream = ByteArrayInputStream(testContent.toByteArray())
      val uploadResult =
        service.putObject(
          bucketName = testBucketName,
          objectName = testObjectName,
          inputStream = inputStream,
          size = testContent.length.toLong(),
          contentType = "text/plain",
        )
      assertTrue(uploadResult.isSuccess, "Upload object should succeed")
      val objectInfo = uploadResult.getOrThrow()
      assertEquals(testBucketName, objectInfo.bucketName)
      assertEquals(testObjectName, objectInfo.objectName)

      // 3. Check if object exists
      val existsResult = service.objectExists(testBucketName, testObjectName)
      assertTrue(existsResult.isSuccess, "Checking object existence should succeed")
      assertTrue(existsResult.getOrThrow(), "Object should exist")

      // 4. Get object information
      val infoResult = service.getObjectInfo(testBucketName, testObjectName)
      assertTrue(infoResult.isSuccess, "Getting object information should succeed")
      val retrievedInfo = infoResult.getOrThrow()
      assertEquals(testBucketName, retrievedInfo.bucketName)
      assertEquals(testObjectName, retrievedInfo.objectName)

      // 5. Download object
      val downloadResult = service.getObject(testBucketName, testObjectName)
      assertTrue(downloadResult.isSuccess, "Download object should succeed")
      val objectContent = downloadResult.getOrThrow()
      val downloadedContent = objectContent.inputStream.readBytes().toString(Charsets.UTF_8)
      assertEquals(testContent, downloadedContent, "Downloaded content should match uploaded content")

      // 6. List objects
      val listResult = service.listObjects(ListObjectsRequest(bucketName = testBucketName))
      assertTrue(listResult.isSuccess, "List objects should succeed")
      val objects = listResult.getOrThrow().objects
      assertTrue(objects.any { it.objectName == testObjectName }, "Object list should contain the uploaded object")

      log.info("Object operations test completed")
    }

    @Test
    fun `test object copy operations`() = runBlocking {
      val testBucketName = "test-integration-copy-bucket-${System.currentTimeMillis()}"
      val sourceObjectName = "source-object.txt"
      val targetObjectName = "target-object.txt"
      val testContent = "Content for copy test"
      testBuckets.add(testBucketName)

      log.info("Starting object copy test")

      // Create bucket and upload source object
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()
      val inputStream = ByteArrayInputStream(testContent.toByteArray())
      service.putObject(testBucketName, sourceObjectName, inputStream, testContent.length.toLong()).getOrThrow()

      // Copy object
      val copyRequest =
        CopyObjectRequest(
          sourceBucketName = testBucketName,
          sourceObjectName = sourceObjectName,
          destinationBucketName = testBucketName,
          destinationObjectName = targetObjectName,
        )
      val copyResult = service.copyObject(copyRequest)
      assertTrue(copyResult.isSuccess, "Copy object should succeed")

      // Verify target object exists
      val existsResult = service.objectExists(testBucketName, targetObjectName)
      assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "Copied object should exist")

      log.info("Object copy test completed")
    }
  }

  @Nested
  inner class PresignedUrlOperations {

    @Test
    fun `test presigned URL generation`() = runBlocking {
      val testBucketName = "test-integration-presigned-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-presigned-object.txt"
      testBuckets.add(testBucketName)

      log.info("Starting presigned URL test, bucket name: {}, object name: {}", testBucketName, testObjectName)

      // 1. Create bucket and object
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()
      val inputStream = ByteArrayInputStream("test content".toByteArray())
      service.putObject(testBucketName, testObjectName, inputStream, 12L).getOrThrow()

      // 2. Generate GET presigned URL
      val getUrlResult =
        service.generatePresignedUrl(bucketName = testBucketName, objectName = testObjectName, expiration = Duration.ofHours(1), method = HttpMethod.GET)
      assertTrue(getUrlResult.isSuccess, "Generating GET presigned URL should succeed")
      val getPresignedUrl = getUrlResult.getOrThrow()
      assertTrue(getPresignedUrl.isNotEmpty(), "GET presigned URL should not be empty")
      assertTrue(getPresignedUrl.startsWith("https://"), "GET presigned URL should use HTTPS protocol")

      // 3. Generate PUT presigned URL
      val putUrlResult =
        service.generatePresignedUrl(
          bucketName = testBucketName,
          objectName = "test-put-object.txt",
          expiration = Duration.ofMinutes(30),
          method = HttpMethod.PUT,
        )
      assertTrue(putUrlResult.isSuccess, "Generating PUT presigned URL should succeed")
      val putPresignedUrl = putUrlResult.getOrThrow()
      assertTrue(putPresignedUrl.isNotEmpty(), "PUT presigned URL should not be empty")
      assertTrue(putPresignedUrl.startsWith("https://"), "PUT presigned URL should use HTTPS protocol")

      log.info("Presigned URL test completed")
    }
  }

  @Nested
  inner class MultipartUploadOperations {

    @Test
    fun `test multipart upload`() = runBlocking {
      val testBucketName = "test-integration-multipart-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-multipart-object.txt"
      // TOS requires each part (except the last one) to be at least 5MB
      val partContent1 = "A".repeat(5 * 1024 * 1024) // 5MB
      val partContent2 = "B".repeat(5 * 1024 * 1024) // 5MB
      testBuckets.add(testBucketName)

      log.info("Starting multipart upload test")

      // Create bucket
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()

      // 1. Initiate multipart upload
      val initiateRequest = InitiateMultipartUploadRequest(bucketName = testBucketName, objectName = testObjectName)
      val initiateResult = service.initiateMultipartUpload(initiateRequest)
      assertTrue(initiateResult.isSuccess, "Initiating multipart upload should succeed")
      val multipartUpload = initiateResult.getOrThrow()
      assertNotNull(multipartUpload.uploadId, "Upload ID should not be null")

      try {
        // 2. Upload first part
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
        assertTrue(part1Result.isSuccess, "Uploading first part should succeed")
        val part1Info = part1Result.getOrThrow()

        // 3. Upload second part
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
        assertTrue(part2Result.isSuccess, "Uploading second part should succeed")
        val part2Info = part2Result.getOrThrow()

        // 4. List uploaded parts
        val listPartsResult = service.listParts(multipartUpload.uploadId, testBucketName, testObjectName)
        assertTrue(listPartsResult.isSuccess, "Listing parts should succeed")
        val parts = listPartsResult.getOrThrow()
        assertEquals(2, parts.size, "Should have two parts")

        // 5. Complete multipart upload
        val completeRequest =
          CompleteMultipartUploadRequest(
            bucketName = testBucketName,
            objectName = testObjectName,
            uploadId = multipartUpload.uploadId,
            parts = listOf(part1Info, part2Info),
          )
        val completeResult = service.completeMultipartUpload(completeRequest)
        assertTrue(completeResult.isSuccess, "Completing multipart upload should succeed")

        // 6. Verify object exists
        val existsResult = service.objectExists(testBucketName, testObjectName)
        assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "Multipart uploaded object should exist")

        log.info("Multipart upload test completed")
      } catch (e: Exception) {
        // If an exception occurs, try to abort the upload
        service.abortMultipartUpload(multipartUpload.uploadId, testBucketName, testObjectName)
        throw e
      }
    }
  }

  @Nested
  inner class `版本控制功能测试` {
    @Test
    fun `应该能够列出对象版本`() = runBlocking {
      val bucketName = "test-versioning-bucket-${System.currentTimeMillis()}"
      val objectName = "versioned-object.txt"
      testBuckets.add(bucketName)

      service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
      service.setBucketVersioning(bucketName, true).getOrThrow()

      service.putObject(bucketName, objectName, ByteArrayInputStream("v1".toByteArray()), 2).getOrThrow()
      service.putObject(bucketName, objectName, ByteArrayInputStream("v2".toByteArray()), 2).getOrThrow()

      val versionsResult = service.listObjectVersions(ListObjectVersionsRequest(bucketName = bucketName, prefix = objectName))
      assertTrue(versionsResult.isSuccess, "列出对象版本应该成功")
      val versions = versionsResult.getOrThrow()
      assertEquals(2, versions.versions.size, "应该有两个版本")
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `test share link generation and validation`() = runBlocking {
      val testBucketName = "test-integration-share-bucket-${System.currentTimeMillis()}"
      val testObjectName = "test-share-object.txt"
      val testContent = "Content for share link test"
      testBuckets.add(testBucketName)

      log.info("Starting share link test")

      // Create bucket and upload object
      service.createBucket(CreateBucketRequest(testBucketName)).getOrThrow()
      val inputStream = ByteArrayInputStream(testContent.toByteArray())
      service.putObject(testBucketName, testObjectName, inputStream, testContent.length.toLong()).getOrThrow()

      // Generate share link
      val shareRequest = ShareLinkRequest(bucketName = testBucketName, objectName = testObjectName, expiration = Duration.ofHours(2), method = HttpMethod.GET)
      val shareResult = service.generateShareLink(shareRequest)
      assertTrue(shareResult.isSuccess, "Generating share link should succeed")
      val shareInfo = shareResult.getOrThrow()
      assertTrue(shareInfo.shareUrl.isNotEmpty(), "Share link should not be empty")

      // Validate share link
      val validateResult = service.validateShareLink(shareInfo.shareUrl)
      assertTrue(validateResult.isSuccess, "Validating share link should succeed")

      log.info("Share link test completed")
    }
  }

  @Nested
  inner class `标签功能测试` {

    @Test
    fun `应该能够设置和获取存储桶标签`() = runBlocking {
      val bucketName = "test-tags-bucket-${System.currentTimeMillis()}"
      testBuckets.add(bucketName)
      service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

      val tags = listOf(Tag("project", "compose-server"), Tag("env", "test"))
      val setResult = service.setBucketTags(bucketName, tags)
      assertTrue(setResult.isSuccess, "设置存储桶标签应该成功")

      val getResult = service.getBucketTags(bucketName)
      assertTrue(getResult.isSuccess, "获取存储桶标签应该成功")
      assertEquals(tags.toSet(), getResult.getOrThrow().toSet(), "获取的标签应该与设置的匹配")

      val deleteResult = service.deleteBucketTags(bucketName)
      assertTrue(deleteResult.isSuccess, "删除存储桶标签应该成功")

      val getAfterDeleteResult = service.getBucketTags(bucketName)
      assertTrue(getAfterDeleteResult.isSuccess, "删除后获取存储桶标签应该成功")
      assertTrue(getAfterDeleteResult.getOrThrow().isEmpty(), "删除后标签应该为空")
    }
  }

  @Nested
  inner class `生命周期和CORS功能测试` {

    @Test
    fun `应该能够设置和获取存储桶生命周期规则`() = runBlocking {
      val bucketName = "test-lifecycle-bucket-${System.currentTimeMillis()}"
      testBuckets.add(bucketName)
      service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

      val rules = listOf(
        LifecycleRule(
          id = "rule-1",
          prefix = "logs/",
          status = LifecycleRuleStatus.ENABLED,
          expiration = LifecycleExpiration(30)
        )
      )

      val setResult = service.setBucketLifecycle(bucketName, rules)
      assertTrue(setResult.isSuccess, "设置生命周期规则应该成功")

      val getResult = service.getBucketLifecycle(bucketName)
      assertTrue(getResult.isSuccess, "获取生命周期规则应该成功")
      val retrievedRules = getResult.getOrThrow()
      assertTrue(retrievedRules.isNotEmpty(), "应该获取到至少一个规则")
      assertEquals("rule-1", retrievedRules.first().id)

      val deleteResult = service.deleteBucketLifecycle(bucketName)
      assertTrue(deleteResult.isSuccess, "删除生命周期规则应该成功")
    }

    @Test
    fun `应该能够设置和获取存储桶CORS规则`() = runBlocking {
      val bucketName = "test-cors-bucket-${System.currentTimeMillis()}"
      testBuckets.add(bucketName)
      service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

      val rules = listOf(
        CorsRule(
          allowedOrigins = listOf("*"),
          allowedMethods = listOf(HttpMethod.GET, HttpMethod.PUT)
        )
      )

      val setResult = service.setBucketCors(bucketName, rules)
      assertTrue(setResult.isSuccess, "设置CORS规则应该成功")

      val getResult = service.getBucketCors(bucketName)
      assertTrue(getResult.isSuccess, "获取CORS规则应该成功")
      val retrievedRules = getResult.getOrThrow()
      assertTrue(retrievedRules.isNotEmpty(), "应该获取到至少一个CORS规则")
      assertEquals(listOf("*"), retrievedRules.first().allowedOrigins)

      val deleteResult = service.deleteBucketCors(bucketName)
      assertTrue(deleteResult.isSuccess, "删除CORS规则应该成功")
    }
  }

  @Nested
  inner class EnvironmentVariables {

    @Test
    fun `test environment variable reading`() {
      log.info("Verifying environment variable reading")

      assertNotNull(accessKey, "VOLCENGINE_TOS_ACCESS_KEY should not be null")
      assertNotNull(secretKey, "VOLCENGINE_TOS_SECRET_KEY should not be null")
      assertTrue(accessKey.isNotBlank(), "VOLCENGINE_TOS_ACCESS_KEY should not be blank")
      assertTrue(secretKey.isNotBlank(), "VOLCENGINE_TOS_SECRET_KEY should not be blank")

      log.info("Environment variable verification completed")
    }
  }
}
