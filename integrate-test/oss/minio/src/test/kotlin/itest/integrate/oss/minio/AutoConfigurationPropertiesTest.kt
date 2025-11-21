package itest.integrate.oss.minio

import io.github.truenine.composeserver.enums.HttpMethod
import io.github.truenine.composeserver.oss.CorsRule
import io.github.truenine.composeserver.oss.CreateBucketRequest
import io.github.truenine.composeserver.oss.IObjectStorageService
import io.github.truenine.composeserver.oss.LifecycleExpiration
import io.github.truenine.composeserver.oss.LifecycleRule
import io.github.truenine.composeserver.oss.LifecycleRuleStatus
import io.github.truenine.composeserver.oss.ListObjectVersionsRequest
import io.github.truenine.composeserver.oss.Tag
import io.github.truenine.composeserver.oss.minio.autoconfig.MinioAutoConfiguration
import io.github.truenine.composeserver.oss.minio.properties.MinioProperties
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.testtoolkit.testcontainers.IOssMinioContainer
import io.minio.MinioClient
import jakarta.annotation.Resource
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(classes = [AutoConfigurationPropertiesTest.TestConfiguration::class])
class AutoConfigurationPropertiesTest : IOssMinioContainer {

  @Resource lateinit var oss: IObjectStorageService

  @Resource lateinit var minioClient: MinioClient

  @Resource lateinit var minioProperties: MinioProperties

  @Resource lateinit var ossProperties: OssProperties

  @Nested
  inner class `基础配置测试` {

    @Test fun `应该成功注入 OSS 服务`(): Unit = minio { assertNotNull(oss) }

    @Test fun `应该成功注入 MinIO 客户端`(): Unit = minio { assertNotNull(minioClient) }

    @Test fun `应该成功注入 MinIO 配置属性`(): Unit = minio { assertNotNull(minioProperties) }

    @Test fun `应该成功注入 OSS 通用配置属性`(): Unit = minio { assertNotNull(ossProperties) }
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
  inner class `对象删除测试` {

    @Nested
    inner class `正常情况` {

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
          // 注意：有些实现可能返回空列表表示成功删除，而不是返回删除详情
          if (deleteResults.isNotEmpty()) {
            assertEquals(objectNames.size, deleteResults.size, "删除结果数量应该匹配")
            deleteResults.forEach { result ->
              assertTrue(result.success, "每个文件删除都应该成功: ${result.objectName}")
              assertTrue(objectNames.contains(result.objectName), "删除的文件名应该在预期列表中")
            }
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

    @Nested
    inner class `异常情况` {

      @Test
      fun `删除不存在的文件应该成功处理`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-delete-nonexistent"
          val objectName = "nonexistent-file.txt"

          // 创建存储桶
          oss.createBucket(CreateBucketRequest(bucketName))

          // 尝试删除不存在的文件
          val deleteResult = oss.deleteObject(bucketName, objectName)
          assertTrue(deleteResult.isSuccess, "删除不存在的文件应该成功处理")
        }
      }

      @Test
      fun `删除空列表应该成功处理`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-delete-empty-list"
          val emptyList = emptyList<String>()

          // 创建存储桶
          oss.createBucket(CreateBucketRequest(bucketName))

          // 批量删除空列表
          val deleteResult = oss.deleteObjects(bucketName, emptyList)
          assertTrue(deleteResult.isSuccess, "删除空列表应该成功处理")

          val deleteResults = deleteResult.getOrThrow()
          assertTrue(deleteResults.isEmpty(), "空列表删除结果应该为空")
        }
      }
    }
  }

  @Nested
  inner class `异常边界情况测试` {

    @Nested
    inner class `存储桶操作异常` {

      @Test
      fun `创建已存在的存储桶应该正确处理`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-duplicate"

          // 第一次创建
          val firstResult = oss.createBucket(CreateBucketRequest(bucketName))
          assertTrue(firstResult.isSuccess, "第一次创建存储桶应该成功")

          // 再次创建相同名称的存储桶，应该返回失败而不是抛出异常
          val secondResult = oss.createBucket(CreateBucketRequest(bucketName))
          assertTrue(secondResult.isFailure, "重复创建存储桶应该返回失败状态")
        }
      }
    }

    @Nested
    inner class `文件操作边界情况` {

      @Test
      fun `上传空文件应该成功`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-empty-file"
          val objectName = "empty-file.txt"
          val emptyContent = ""

          // 创建存储桶
          oss.createBucket(CreateBucketRequest(bucketName))

          // 上传空文件
          val uploadResult =
            oss.putObject(
              bucketName = bucketName,
              objectName = objectName,
              inputStream = ByteArrayInputStream(emptyContent.toByteArray()),
              size = 0L,
              contentType = "text/plain",
            )

          assertTrue(uploadResult.isSuccess, "上传空文件应该成功")

          val objectInfo = uploadResult.getOrThrow()
          assertEquals(objectName, objectInfo.objectName)
          assertEquals(bucketName, objectInfo.bucketName)

          // 下载并验证空文件
          val downloadResult = oss.getObject(bucketName, objectName)
          assertTrue(downloadResult.isSuccess, "下载空文件应该成功")

          val objectContent = downloadResult.getOrThrow()
          val downloadedContent = objectContent.inputStream.bufferedReader().use { it.readText() }
          assertEquals(emptyContent, downloadedContent, "下载的空文件内容应该正确")
        }
      }

      @Test
      fun `上传大文件名的文件应该成功`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-long-filename"
          val objectName = "a".repeat(100) + ".txt"
          val content = "Test content for long filename"

          // 创建存储桶
          oss.createBucket(CreateBucketRequest(bucketName))

          // 上传长文件名的文件
          val uploadResult =
            oss.putObject(
              bucketName = bucketName,
              objectName = objectName,
              inputStream = ByteArrayInputStream(content.toByteArray()),
              size = content.toByteArray().size.toLong(),
              contentType = "text/plain",
            )

          assertTrue(uploadResult.isSuccess, "上传长文件名文件应该成功")

          val objectInfo = uploadResult.getOrThrow()
          assertEquals(objectName, objectInfo.objectName)
          assertEquals(bucketName, objectInfo.bucketName)
        }
      }

      @Test
      fun `检查不存在存储桶中的文件应该正确处理`(): Unit = minio {
        runBlocking {
          val nonExistentBucket = "non-existent-bucket-" + System.currentTimeMillis()
          val objectName = "test-file.txt"

          // 检查不存在存储桶中的文件
          val existsResult = oss.objectExists(nonExistentBucket, objectName)
          assertTrue(existsResult.isFailure, "检查不存在存储桶中的文件应该返回失败")
        }
      }
    }
  }

  @Nested
  inner class `标签功能测试` {
    @Test
    fun `应该能够设置和获取存储桶标签`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-tags"
        val tags = listOf(Tag("project", "compose-server"), Tag("env", "test"))

        oss.createBucket(CreateBucketRequest(bucketName))
        val setResult = oss.setBucketTags(bucketName, tags)
        assertTrue(setResult.isSuccess, "设置存储桶标签应该成功")

        val getResult = oss.getBucketTags(bucketName)
        assertTrue(getResult.isSuccess, "获取存储桶标签应该成功")
        assertEquals(tags.toSet(), getResult.getOrThrow().toSet(), "获取的标签应该与设置的匹配")

        val deleteResult = oss.deleteBucketTags(bucketName)
        assertTrue(deleteResult.isSuccess, "删除存储桶标签应该成功")

        val getAfterDeleteResult = oss.getBucketTags(bucketName)
        assertTrue(getAfterDeleteResult.isSuccess, "删除后获取存储桶标签应该成功")
        assertTrue(getAfterDeleteResult.getOrThrow().isEmpty(), "删除后标签应该为空")
      }
    }

    @Test
    fun `应该能够设置和获取对象标签`(): Unit = minio {
      runBlocking {
        val bucketName = "test-object-tags"
        val objectName = "tagged-object.txt"
        val tags = listOf(Tag("type", "test-data"), Tag("version", "1"))
        oss.createBucket(CreateBucketRequest(bucketName))
        oss.putObject(bucketName, objectName, ByteArrayInputStream("data".toByteArray()), 4)

        val setResult = oss.setObjectTags(bucketName, objectName, tags)
        assertTrue(setResult.isSuccess, "设置对象标签应该成功")

        val getResult = oss.getObjectTags(bucketName, objectName)
        assertTrue(getResult.isSuccess, "获取对象标签应该成功")
        assertEquals(tags.toSet(), getResult.getOrThrow().toSet(), "获取的对象标签应该与设置的匹配")

        val deleteResult = oss.deleteObjectTags(bucketName, objectName)
        assertTrue(deleteResult.isSuccess, "删除对象标签应该成功")

        val getAfterDeleteResult = oss.getObjectTags(bucketName, objectName)
        assertTrue(getAfterDeleteResult.isSuccess, "删除后获取对象标签应该成功")
        assertTrue(getAfterDeleteResult.getOrThrow().isEmpty(), "删除后对象标签应该为空")
      }
    }
  }

  @Nested
  inner class `版本控制功能测试` {
    @Test
    fun `应该能够列出对象版本`(): Unit = minio {
      runBlocking {
        val bucketName = "test-versioning-bucket-" + System.currentTimeMillis()
        val objectName = "versioned-object-" + System.currentTimeMillis() + ".txt"

        oss.createBucket(CreateBucketRequest(bucketName))
        oss.setBucketVersioning(bucketName, true)

        oss.putObject(bucketName, objectName, ByteArrayInputStream("v1".toByteArray()), 2)
        oss.putObject(bucketName, objectName, ByteArrayInputStream("v2".toByteArray()), 2)

        val versionsResult = oss.listObjectVersions(ListObjectVersionsRequest(bucketName = bucketName, prefix = objectName))
        assertTrue(versionsResult.isSuccess, "列出对象版本应该成功")
        val versions = versionsResult.getOrThrow()
        assertEquals(2, versions.versions.size, "应该有两个版本")
      }
    }
  }

  @Nested
  inner class `生命周期和CORS功能测试` {
    @Test
    fun `应该能够设置和获取存储桶生命周期规则`(): Unit = minio {
      runBlocking {
        val bucketName = "test-lifecycle-bucket"
        val rules = listOf(LifecycleRule(id = "rule-1", prefix = "logs/", status = LifecycleRuleStatus.ENABLED, expiration = LifecycleExpiration(30)))
        oss.createBucket(CreateBucketRequest(bucketName))

        val setResult = oss.setBucketLifecycle(bucketName, rules)
        assertTrue(setResult.isSuccess, "设置生命周期规则应该成功")

        val getResult = oss.getBucketLifecycle(bucketName)
        assertTrue(getResult.isSuccess, "获取生命周期规则应该成功")
        val retrievedRules = getResult.getOrThrow()
        assertTrue(retrievedRules.isNotEmpty(), "应该获取到至少一个规则")
        assertEquals("rule-1", retrievedRules.first().id)

        val deleteResult = oss.deleteBucketLifecycle(bucketName)
        assertTrue(deleteResult.isSuccess, "删除生命周期规则应该成功")
      }
    }

    @Test
    @Disabled(
      """
      Bucket-level CORS configuration is only supported in MinIO AiStor (paid version).
      The community edition of MinIO does not support per-bucket CORS settings.
      
      Reference: https://github.com/minio/minio/discussions/20841
      
      For community edition, use cluster-wide CORS via MINIO_API_CORS_ALLOW_ORIGIN environment variable.
      """
    )
    fun `应该能够设置和获取存储桶CORS规则`(): Unit = minio {
      runBlocking {
        val bucketName = "test-cors-bucket"
        val rules = listOf(CorsRule(allowedOrigins = listOf("*"), allowedMethods = listOf(HttpMethod.GET, HttpMethod.PUT)))
        oss.createBucket(CreateBucketRequest(bucketName))

        val setResult = oss.setBucketCors(bucketName, rules)
        assertTrue(setResult.isSuccess, "设置CORS规则应该成功")

        val getResult = oss.getBucketCors(bucketName)
        assertTrue(getResult.isSuccess, "获取CORS规则应该成功")
        val retrievedRules = getResult.getOrThrow()
        assertTrue(retrievedRules.isNotEmpty(), "应该获取到至少一个CORS规则")
        assertEquals(listOf("*"), retrievedRules.first().allowedOrigins)

        val deleteResult = oss.deleteBucketCors(bucketName)
        assertTrue(deleteResult.isSuccess, "删除CORS规则应该成功")
      }
    }
  }

  @Import(MinioAutoConfiguration::class) @EnableConfigurationProperties(MinioProperties::class, OssProperties::class) class TestConfiguration
}
