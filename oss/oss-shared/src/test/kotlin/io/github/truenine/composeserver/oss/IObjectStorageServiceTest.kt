package io.github.truenine.composeserver.oss

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Base test class for IObjectStorageService implementations
 *
 * Subclasses should provide a concrete implementation for testing
 */
abstract class IObjectStorageServiceTest {

  abstract fun createObjectStorageService(): IObjectStorageService

  @Nested
  inner class BucketOperations {

    @Test
    fun `测试创建和检查存储桶`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"

      try {
        // 创建存储桶
        val createResult = service.createBucket(CreateBucketRequest(bucketName))
        assertTrue(createResult.isSuccess, "创建存储桶应该成功")

        val bucketInfo = createResult.getOrThrow()
        assertEquals(bucketName, bucketInfo.name)

        // 检查存储桶是否存在
        val existsResult = service.bucketExists(bucketName)
        assertTrue(existsResult.isSuccess, "检查存储桶存在应该成功")
        assertTrue(existsResult.getOrThrow(), "存储桶应该存在")

        log.info("存储桶操作测试通过: $bucketName")
      } finally {
        // 清理
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试列出存储桶`() = runBlocking {
      val service = createObjectStorageService()

      val listResult = service.listBuckets()
      assertTrue(listResult.isSuccess, "列出存储桶应该成功")

      val buckets = listResult.getOrThrow()
      log.info("找到 ${buckets.size} 个存储桶")
    }

    @Test
    fun `测试删除不存在的存储桶`() = runBlocking {
      val service = createObjectStorageService()
      val nonExistentBucket = "non-existent-bucket-${System.currentTimeMillis()}"

      val deleteResult = service.deleteBucket(nonExistentBucket)
      // 删除不存在的存储桶可能成功也可能失败，取决于实现
      log.info("删除不存在存储桶的结果: ${deleteResult.isSuccess}")
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `测试上传和下载对象`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "Hello, World! 你好世界！"

      try {
        // 创建存储桶
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // 上传对象
        val uploadResult = service.putObject(bucketName = bucketName, objectName = objectName, content = content, contentType = "text/plain; charset=utf-8")
        assertTrue(uploadResult.isSuccess, "上传对象应该成功")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)

        // 检查对象是否存在
        val existsResult = service.objectExists(bucketName, objectName)
        assertTrue(existsResult.isSuccess, "检查对象存在应该成功")
        assertTrue(existsResult.getOrThrow(), "对象应该存在")

        // 下载对象
        val downloadResult = service.getObjectString(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "下载对象应该成功")
        assertEquals(content, downloadResult.getOrThrow())

        log.info("对象操作测试通过: $bucketName/$objectName")
      } finally {
        // 清理
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试获取对象信息`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-info.txt"
      val content = "Test content for info"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        val infoResult = service.getObjectInfo(bucketName, objectName)
        assertTrue(infoResult.isSuccess, "获取对象信息应该成功")

        val objectInfo = infoResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)
        assertTrue(objectInfo.size > 0, "对象大小应该大于0")

        log.info("对象信息测试通过: ${objectInfo.size} bytes")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试删除对象`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-delete.txt"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, "content to delete").getOrThrow()

        // 确认对象存在
        assertTrue(service.objectExists(bucketName, objectName).getOrThrow())

        // 删除对象
        val deleteResult = service.deleteObject(bucketName, objectName)
        assertTrue(deleteResult.isSuccess, "删除对象应该成功")

        // 确认对象不存在
        assertFalse(service.objectExists(bucketName, objectName).getOrThrow())

        log.info("对象删除测试通过")
      } finally {
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ExtensionFunctions {

    @Test
    fun `测试字节数组上传下载`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-bytes.bin"
      val bytes = "Binary content 二进制内容".toByteArray(Charsets.UTF_8)

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // 上传字节数组
        val uploadResult = service.putObject(bucketName, objectName, bytes)
        assertTrue(uploadResult.isSuccess, "上传字节数组应该成功")

        // 下载字节数组
        val downloadResult = service.getObjectBytes(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "下载字节数组应该成功")

        val downloadedBytes = downloadResult.getOrThrow()
        assertTrue(bytes.contentEquals(downloadedBytes), "下载的字节数组应该与原始数据相同")

        log.info("字节数组操作测试通过: ${bytes.size} bytes")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试确保存储桶存在`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-ensure-bucket-${System.currentTimeMillis()}"

      try {
        // 第一次调用应该创建存储桶
        val firstResult = service.ensureBucket(bucketName)
        assertTrue(firstResult.isSuccess, "第一次确保存储桶应该成功")

        // 第二次调用应该返回现有存储桶
        val secondResult = service.ensureBucket(bucketName)
        assertTrue(secondResult.isSuccess, "第二次确保存储桶应该成功")

        log.info("确保存储桶存在测试通过")
      } finally {
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `测试生成简单分享链接`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-share-object.txt"
      val content = "Hello, Share Link!"

      try {
        // 创建存储桶和对象
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        // 生成分享链接
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1))
        assertTrue(shareResult.isSuccess, "生成分享链接应该成功")

        val shareInfo = shareResult.getOrThrow()
        assertEquals(bucketName, shareInfo.bucketName)
        assertEquals(objectName, shareInfo.objectName)
        assertTrue(shareInfo.shareUrl.isNotEmpty())

        log.info("分享链接生成测试通过: ${shareInfo.shareUrl}")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试上传文件并生成分享链接`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-upload-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-share-object.txt"
      val content = "Hello, Upload with Share Link!"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // 上传字符串并生成分享链接
        val uploadResult =
          service.uploadStringWithLink(bucketName = bucketName, objectName = objectName, content = content, shareExpiration = java.time.Duration.ofHours(2))
        assertTrue(uploadResult.isSuccess, "上传并生成分享链接应该成功")

        val response = uploadResult.getOrThrow()
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)
        assertTrue(response.shareLink.shareUrl.isNotEmpty())
        assertNotNull(response.publicUrl)

        log.info("上传并生成分享链接测试通过")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试验证分享链接`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-validate-bucket-${System.currentTimeMillis()}"
      val objectName = "test-validate-object.txt"
      val content = "Hello, Validate Link!"

      try {
        // 创建存储桶和对象
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        // 生成分享链接
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1))
        val shareUrl = shareResult.getOrThrow().shareUrl

        // 验证分享链接
        val validateResult = service.validateShareLink(shareUrl)
        assertTrue(validateResult.isSuccess, "验证分享链接应该成功")

        val validatedInfo = validateResult.getOrThrow()
        assertEquals(bucketName, validatedInfo.bucketName)
        assertEquals(objectName, validatedInfo.objectName)

        log.info("验证分享链接测试通过")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ErrorHandling {

    @Test
    fun `测试访问不存在的对象`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val nonExistentObject = "non-existent-object.txt"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        val existsResult = service.objectExists(bucketName, nonExistentObject)
        assertTrue(existsResult.isSuccess, "检查不存在对象应该成功")
        assertFalse(existsResult.getOrThrow(), "不存在的对象应该返回false")

        val getResult = service.getObject(bucketName, nonExistentObject)
        assertTrue(getResult.isFailure, "获取不存在的对象应该失败")

        log.info("错误处理测试通过")
      } finally {
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试生成不存在对象的分享链接`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-error-bucket-${System.currentTimeMillis()}"
      val nonExistentObject = "non-existent-object.txt"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // 尝试为不存在的对象生成分享链接
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = nonExistentObject, expiration = java.time.Duration.ofHours(1))

        // 根据实现，这可能成功（生成链接但访问时失败）或失败
        // MinIO 允许为不存在的对象生成预签名URL
        log.info("为不存在对象生成分享链接结果: ${shareResult.isSuccess}")
      } finally {
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试分享链接端到端下载流程`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-e2e-download-bucket-${System.currentTimeMillis()}"
      val objectName = "test-e2e-download-object.txt"
      val content = "Hello, End-to-End Download Test!"

      try {
        // 创建存储桶和对象
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        // 生成分享链接
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofMinutes(5))
        assertTrue(shareResult.isSuccess, "生成分享链接应该成功")

        val shareInfo = shareResult.getOrThrow()
        val shareUrl = shareInfo.shareUrl

        // 使用分享链接下载内容
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "通过分享链接下载应该成功")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "下载的内容应该与原始内容一致")
        }

        // 使用扩展函数下载为字符串
        val stringDownloadResult = service.downloadStringFromShareLink(shareUrl)
        assertTrue(stringDownloadResult.isSuccess, "使用扩展函数下载字符串应该成功")
        assertEquals(content, stringDownloadResult.getOrThrow(), "扩展函数下载的内容应该一致")

        log.info("分享链接端到端下载流程测试通过")
        log.info("分享链接: $shareUrl")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试上传并返回链接的完整验证`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-upload-link-verify-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-link-verify-object.txt"
      val content = "Hello, Upload Link Verification Test!"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // 使用扩展函数上传字符串并生成分享链接
        val uploadResult =
          service.uploadStringWithLink(bucketName = bucketName, objectName = objectName, content = content, shareExpiration = java.time.Duration.ofMinutes(10))
        assertTrue(uploadResult.isSuccess, "上传字符串并生成分享链接应该成功")

        val response = uploadResult.getOrThrow()

        // 验证上传结果
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)

        // 验证分享链接信息
        assertTrue(response.shareLink.shareUrl.isNotEmpty(), "分享链接不应为空")
        assertEquals(bucketName, response.shareLink.bucketName)
        assertEquals(objectName, response.shareLink.objectName)
        assertEquals(HttpMethod.GET, response.shareLink.method)

        // 验证对象确实存在
        val existsResult = service.objectExists(bucketName, objectName)
        assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "上传的对象应该存在")

        // 通过分享链接下载验证内容
        val downloadResult = service.downloadStringFromShareLink(response.shareLink.shareUrl)
        assertTrue(downloadResult.isSuccess, "通过分享链接下载应该成功")
        assertEquals(content, downloadResult.getOrThrow(), "下载的内容应该与上传的内容一致")

        // 验证分享链接
        val validateResult = service.validateShareLink(response.shareLink.shareUrl)
        assertTrue(validateResult.isSuccess, "验证分享链接应该成功")

        val validatedInfo = validateResult.getOrThrow()
        assertEquals(bucketName, validatedInfo.bucketName)
        assertEquals(objectName, validatedInfo.objectName)

        log.info("上传并返回链接的完整验证测试通过")
        log.info("分享链接: ${response.shareLink.shareUrl}")
        log.info("公共URL: ${response.publicUrl}")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `测试文件上传下载分享链接流程`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-file-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-file-share-object.txt"
      val content = "Hello, File Share Test!"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // 创建临时文件
        val tempFile = java.io.File.createTempFile("test-upload", ".txt")
        tempFile.writeText(content)

        try {
          // 使用扩展函数上传文件并生成分享链接
          val uploadResult =
            service.uploadFileWithLink(
              bucketName = bucketName,
              objectName = objectName,
              file = tempFile,
              shareExpiration = java.time.Duration.ofMinutes(15),
              contentType = "text/plain",
            )
          assertTrue(uploadResult.isSuccess, "上传文件并生成分享链接应该成功")

          val response = uploadResult.getOrThrow()

          // 验证上传结果
          assertEquals(bucketName, response.objectInfo.bucketName)
          assertEquals(objectName, response.objectInfo.objectName)
          assertEquals(tempFile.length(), response.objectInfo.size)

          // 创建下载目标文件
          val downloadFile = java.io.File.createTempFile("test-download", ".txt")

          try {
            // 使用扩展函数通过分享链接下载到文件
            val downloadResult = service.downloadFileFromShareLink(shareUrl = response.shareLink.shareUrl, targetFile = downloadFile)
            assertTrue(downloadResult.isSuccess, "通过分享链接下载到文件应该成功")

            // 验证下载的文件内容
            val downloadedContent = downloadFile.readText()
            assertEquals(content, downloadedContent, "下载的文件内容应该与原始内容一致")

            log.info("文件上传下载分享链接流程测试通过")
            log.info("原始文件大小: ${tempFile.length()} bytes")
            log.info("下载文件大小: ${downloadFile.length()} bytes")
          } finally {
            downloadFile.delete()
          }
        } finally {
          tempFile.delete()
        }
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }
}
