package itest.integrate.oss.minio

import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.IObjectStorageService
import io.github.truenine.composeserver.oss.minio.autoconfig.MinioAutoConfiguration
import io.github.truenine.composeserver.oss.minio.properties.MinioProperties
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.testtoolkit.testcontainers.IOssMinioContainer
import io.minio.MinioClient
import jakarta.annotation.Resource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(classes = [AutoConfigurationPropertiesTest.TestConfiguration::class])
class AutoConfigurationPropertiesTest : IOssMinioContainer {

  @Resource
  lateinit var oss: IObjectStorageService

  @Resource
  lateinit var minioClient: MinioClient

  @Resource
  lateinit var minioProperties: MinioProperties

  @Resource
  lateinit var ossProperties: OssProperties

  @Nested
  inner class `基础配置测试` {

    @Test
    fun `应该成功注入 OSS 服务`(): Unit = minio { assertNotNull(oss) }

    @Test
    fun `应该成功注入 MinIO 客户端`(): Unit = minio { assertNotNull(minioClient) }

    @Test
    fun `应该成功注入 MinIO 配置属性`(): Unit = minio { assertNotNull(minioProperties) }

    @Test
    fun `应该成功注入 OSS 通用配置属性`(): Unit = minio { assertNotNull(ossProperties) }
  }

  @Nested
  inner class `文件上传下载测试` {

    @Test
    fun `应该能够创建存储桶`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-create"
        val result = oss.createBucket(CreateBucketRequest(bucketName))
        assertTrue(result.isSuccess, "创建存储桶应该成功")

        val bucketInfo = result.getOrThrow()
        assertEquals(bucketName, bucketInfo.name)
      }
    }

    @Test
    fun `应该能够上传和下载文件`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-upload"
        val objectName = "test-file.txt"
        val content = "Hello, MinIO Integration Test!"

        // 首先创建存储桶
        oss.createBucket(CreateBucketRequest(bucketName))

        // 上传文件
        val uploadResult =
          oss.putObject(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = ByteArrayInputStream(content.toByteArray()),
            size = content.toByteArray().size.toLong(),
            contentType = "text/plain",
          )

        assertTrue(uploadResult.isSuccess, "上传文件应该成功")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(objectName, objectInfo.objectName)
        assertEquals(bucketName, objectInfo.bucketName)

        // 下载文件
        val downloadResult = oss.getObject(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "下载文件应该成功")

        val objectContent = downloadResult.getOrThrow()
        val downloadedContent = objectContent.inputStream.bufferedReader().use { it.readText() }
        assertEquals(content, downloadedContent, "下载的内容应该与上传的内容一致")
      }
    }

    @Test
    fun `应该能够检查文件是否存在`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-exists"
        val existingFile = "existing-file.txt"
        val nonExistingFile = "non-existing-file.txt"

        // 创建存储桶并上传文件
        oss.createBucket(CreateBucketRequest(bucketName))
        oss.putObject(bucketName = bucketName, objectName = existingFile, inputStream = ByteArrayInputStream("test".toByteArray()), size = 4L)

        // 检查存在的文件
        val existsResult = oss.objectExists(bucketName, existingFile)
        assertTrue(existsResult.isSuccess, "检查文件存在应该成功")
        assertTrue(existsResult.getOrThrow(), "文件应该存在")

        // 检查不存在的文件
        val notExistsResult = oss.objectExists(bucketName, nonExistingFile)
        assertTrue(notExistsResult.isSuccess, "检查文件不存在应该成功")
        assertTrue(!notExistsResult.getOrThrow(), "文件不应该存在")
      }
    }
  }

  @Nested
  inner class ObjectDelete {

    @Test
    fun `应该能够删除单个文件`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-delete"
        val objectName = "file-to-delete.txt"
        val content = "This file will be deleted"

        // 创建存储桶并上传文件
        oss.createBucket(CreateBucketRequest(bucketName))
        oss.putObject(
          bucketName = bucketName,
          objectName = objectName,
          inputStream = ByteArrayInputStream(content.toByteArray()),
          size = content.toByteArray().size.toLong(),
        )

        // 验证文件存在
        val existsBeforeDelete = oss.objectExists(bucketName, objectName)
        assertTrue(existsBeforeDelete.isSuccess, "检查文件存在应该成功")
        assertTrue(existsBeforeDelete.getOrThrow(), "文件在删除前应该存在")

        // 删除文件
        val deleteResult = oss.deleteObject(bucketName, objectName)
        assertTrue(deleteResult.isSuccess, "删除文件应该成功")

        // 验证文件已被删除
        val existsAfterDelete = oss.objectExists(bucketName, objectName)
        assertTrue(existsAfterDelete.isSuccess, "删除后检查文件存在应该成功")
        assertTrue(!existsAfterDelete.getOrThrow(), "文件在删除后不应该存在")
      }
    }

    @Test
    fun `应该能够删除多个文件`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-delete-multiple"
        val objectNames = listOf("file1.txt", "file2.txt", "file3.txt")
        val content = "Test file content"

        // 创建存储桶
        oss.createBucket(CreateBucketRequest(bucketName))

        // 上传多个文件
        objectNames.forEach { objectName ->
          oss.putObject(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = ByteArrayInputStream(content.toByteArray()),
            size = content.toByteArray().size.toLong(),
          )
        }

        // 验证所有文件都存在
        objectNames.forEach { objectName ->
          val exists = oss.objectExists(bucketName, objectName)
          assertTrue(exists.isSuccess && exists.getOrThrow(), "文件 $objectName 应该存在")
        }

        // 批量删除文件
        val deleteResult = oss.deleteObjects(bucketName, objectNames)
        assertTrue(deleteResult.isSuccess, "批量删除文件应该成功")

        val deleteResults = deleteResult.getOrThrow()
        assertEquals(objectNames.size, deleteResults.size, "删除结果数量应该匹配")

        deleteResults.forEach { result ->
          assertTrue(result.success, "每个文件删除都应该成功: ${result.objectName}")
          assertTrue(objectNames.contains(result.objectName), "删除的文件名应该在预期列表中")
        }

        // 验证所有文件都被删除了
        objectNames.forEach { objectName ->
          val exists = oss.objectExists(bucketName, objectName)
          assertTrue(exists.isSuccess, "删除后检查文件存在应该成功")
          assertTrue(!exists.getOrThrow(), "文件 $objectName 在删除后不应该存在")
        }
      }
    }
  }

  @Import(MinioAutoConfiguration::class)
  @EnableConfigurationProperties(MinioProperties::class, OssProperties::class)
  class TestConfiguration
}
