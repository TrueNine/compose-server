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
  inner class `Basic configuration tests` {

    @Test fun `Should inject OSS service successfully`(): Unit = minio { assertNotNull(oss) }

    @Test fun `Should inject MinIO client successfully`(): Unit = minio { assertNotNull(minioClient) }

    @Test fun `Should inject MinIO properties successfully`(): Unit = minio { assertNotNull(minioProperties) }

    @Test fun `Should inject generic OSS properties successfully`(): Unit = minio { assertNotNull(ossProperties) }
  }

  @Nested
  inner class `File upload and download tests` {

    @Test
    fun `Should create bucket`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-create"
        val result = oss.createBucket(CreateBucketRequest(bucketName))
        assertTrue(result.isSuccess, "Bucket creation should succeed")

        val bucketInfo = result.getOrThrow()
        assertEquals(bucketName, bucketInfo.name)
      }
    }

    @Test
    fun `Should upload and download file`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-upload"
        val objectName = "test-file.txt"
        val content = "Hello, MinIO Integration Test!"

        // First create bucket
        oss.createBucket(CreateBucketRequest(bucketName))

        // Upload file
        val uploadResult =
          oss.putObject(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = ByteArrayInputStream(content.toByteArray()),
            size = content.toByteArray().size.toLong(),
            contentType = "text/plain",
          )

        assertTrue(uploadResult.isSuccess, "File upload should succeed")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(objectName, objectInfo.objectName)
        assertEquals(bucketName, objectInfo.bucketName)

        // Download file
        val downloadResult = oss.getObject(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "File download should succeed")

        val objectContent = downloadResult.getOrThrow()
        val downloadedContent = objectContent.inputStream.bufferedReader().use { it.readText() }
        assertEquals(content, downloadedContent, "Downloaded content should be the same as uploaded content")
      }
    }

    @Test
    fun `Should check whether object exists`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-exists"
        val existingFile = "existing-file.txt"
        val nonExistingFile = "non-existing-file.txt"

        // Create bucket and upload file
        oss.createBucket(CreateBucketRequest(bucketName))
        oss.putObject(bucketName = bucketName, objectName = existingFile, inputStream = ByteArrayInputStream("test".toByteArray()), size = 4L)

        // Check existing file
        val existsResult = oss.objectExists(bucketName, existingFile)
        assertTrue(existsResult.isSuccess, "Checking existing object should succeed")
        assertTrue(existsResult.getOrThrow(), "Object should exist")

        // Check non-existing file
        val notExistsResult = oss.objectExists(bucketName, nonExistingFile)
        assertTrue(notExistsResult.isSuccess, "Checking non-existing object should succeed")
        assertTrue(!notExistsResult.getOrThrow(), "Object should not exist")
      }
    }
  }

  @Nested
  inner class `Object deletion tests` {

    @Nested
    inner class `Normal cases` {

      @Test
      fun `Should delete a single object`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-delete"
          val objectName = "file-to-delete.txt"
          val content = "This file will be deleted"

          // Create bucket and upload object
          oss.createBucket(CreateBucketRequest(bucketName))
          oss.putObject(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = ByteArrayInputStream(content.toByteArray()),
            size = content.toByteArray().size.toLong(),
          )

          // Verify object exists
          val existsBeforeDelete = oss.objectExists(bucketName, objectName)
          assertTrue(existsBeforeDelete.isSuccess, "Checking object existence should succeed")
          assertTrue(existsBeforeDelete.getOrThrow(), "Object should exist before deletion")

          // Delete object
          val deleteResult = oss.deleteObject(bucketName, objectName)
          assertTrue(deleteResult.isSuccess, "Object deletion should succeed")

          // Verify object has been deleted
          val existsAfterDelete = oss.objectExists(bucketName, objectName)
          assertTrue(existsAfterDelete.isSuccess, "Checking object existence after deletion should succeed")
          assertTrue(!existsAfterDelete.getOrThrow(), "Object should not exist after deletion")
        }
      }

      @Test
      fun `Should delete multiple objects`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-delete-multiple"
          val objectNames = listOf("file1.txt", "file2.txt", "file3.txt")
          val content = "Test file content"

          // Create bucket
          oss.createBucket(CreateBucketRequest(bucketName))

          // Upload multiple objects
          objectNames.forEach { objectName ->
            oss.putObject(
              bucketName = bucketName,
              objectName = objectName,
              inputStream = ByteArrayInputStream(content.toByteArray()),
              size = content.toByteArray().size.toLong(),
            )
          }

          // Verify all objects exist
          objectNames.forEach { objectName ->
            val exists = oss.objectExists(bucketName, objectName)
            assertTrue(exists.isSuccess && exists.getOrThrow(), "Object $objectName should exist")
          }

          // Batch delete objects
          val deleteResult = oss.deleteObjects(bucketName, objectNames)
          assertTrue(deleteResult.isSuccess, "Batch deleting objects should succeed")

          val deleteResults = deleteResult.getOrThrow()
          // Note: some implementations may return an empty list on success instead of detailed delete results
          if (deleteResults.isNotEmpty()) {
            assertEquals(objectNames.size, deleteResults.size, "Delete result count should match")
            deleteResults.forEach { result ->
              assertTrue(result.success, "Each object deletion should succeed: ${result.objectName}")
              assertTrue(objectNames.contains(result.objectName), "Deleted object name should be in the expected list")
            }
          }

          // Verify all objects have been deleted
          objectNames.forEach { objectName ->
            val exists = oss.objectExists(bucketName, objectName)
            assertTrue(exists.isSuccess, "Checking object existence after deletion should succeed")
            assertTrue(!exists.getOrThrow(), "Object $objectName should not exist after deletion")
          }
        }
      }
    }

    @Nested
    inner class `Exceptional cases` {

      @Test
      fun `Deleting non-existent object should be handled successfully`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-delete-nonexistent"
          val objectName = "nonexistent-file.txt"

          // Create bucket
          oss.createBucket(CreateBucketRequest(bucketName))

          // Try deleting a non-existent object
          val deleteResult = oss.deleteObject(bucketName, objectName)
          assertTrue(deleteResult.isSuccess, "Deleting non-existent object should be handled successfully")
        }
      }

      @Test
      fun `Deleting empty list should be handled successfully`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-delete-empty-list"
          val emptyList = emptyList<String>()

          // Create bucket
          oss.createBucket(CreateBucketRequest(bucketName))

          // Batch delete using empty list
          val deleteResult = oss.deleteObjects(bucketName, emptyList)
          assertTrue(deleteResult.isSuccess, "Deleting empty list should be handled successfully")

          val deleteResults = deleteResult.getOrThrow()
          assertTrue(deleteResults.isEmpty(), "Delete result for empty list should be empty")
        }
      }
    }
  }

  @Nested
  inner class `Exceptional boundary tests` {

    @Nested
    inner class `Bucket operation exceptions` {

      @Test
      fun `Creating an existing bucket should be handled correctly`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-duplicate"

          // First creation
          val firstResult = oss.createBucket(CreateBucketRequest(bucketName))
          assertTrue(firstResult.isSuccess, "First bucket creation should succeed")

          // Creating the same bucket again should return failure instead of throwing an exception
          val secondResult = oss.createBucket(CreateBucketRequest(bucketName))
          assertTrue(secondResult.isFailure, "Creating the same bucket again should return failure status")
        }
      }
    }

    @Nested
    inner class `File operation boundary cases` {

      @Test
      fun `Uploading empty file should succeed`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-empty-file"
          val objectName = "empty-file.txt"
          val emptyContent = ""

          // Create bucket
          oss.createBucket(CreateBucketRequest(bucketName))

          // Upload empty file
          val uploadResult =
            oss.putObject(
              bucketName = bucketName,
              objectName = objectName,
              inputStream = ByteArrayInputStream(emptyContent.toByteArray()),
              size = 0L,
              contentType = "text/plain",
            )

          assertTrue(uploadResult.isSuccess, "Uploading empty file should succeed")

          val objectInfo = uploadResult.getOrThrow()
          assertEquals(objectName, objectInfo.objectName)
          assertEquals(bucketName, objectInfo.bucketName)

          // Download and verify empty file
          val downloadResult = oss.getObject(bucketName, objectName)
          assertTrue(downloadResult.isSuccess, "Downloading empty file should succeed")

          val objectContent = downloadResult.getOrThrow()
          val downloadedContent = objectContent.inputStream.bufferedReader().use { it.readText() }
          assertEquals(emptyContent, downloadedContent, "Downloaded empty file content should be correct")
        }
      }

      @Test
      fun `Uploading long filename object should succeed`(): Unit = minio {
        runBlocking {
          val bucketName = "test-bucket-long-filename"
          val objectName = "a".repeat(100) + ".txt"
          val content = "Test content for long filename"

          // Create bucket
          oss.createBucket(CreateBucketRequest(bucketName))

          // Upload object with long filename
          val uploadResult =
            oss.putObject(
              bucketName = bucketName,
              objectName = objectName,
              inputStream = ByteArrayInputStream(content.toByteArray()),
              size = content.toByteArray().size.toLong(),
              contentType = "text/plain",
            )

          assertTrue(uploadResult.isSuccess, "Uploading long filename object should succeed")

          val objectInfo = uploadResult.getOrThrow()
          assertEquals(objectName, objectInfo.objectName)
          assertEquals(bucketName, objectInfo.bucketName)
        }
      }

      @Test
      fun `Checking object in non-existent bucket should be handled correctly`(): Unit = minio {
        runBlocking {
          val nonExistentBucket = "non-existent-bucket-" + System.currentTimeMillis()
          val objectName = "test-file.txt"

          // Check object in non-existent bucket
          val existsResult = oss.objectExists(nonExistentBucket, objectName)
          assertTrue(existsResult.isFailure, "Checking object in non-existent bucket should return failure")
        }
      }
    }
  }

  @Nested
  inner class `Tag feature tests` {
    @Test
    fun `Should set and get bucket tags`(): Unit = minio {
      runBlocking {
        val bucketName = "test-bucket-tags"
        val tags = listOf(Tag("project", "compose-server"), Tag("env", "test"))

        oss.createBucket(CreateBucketRequest(bucketName))
        val setResult = oss.setBucketTags(bucketName, tags)
        assertTrue(setResult.isSuccess, "Setting bucket tags should succeed")

        val getResult = oss.getBucketTags(bucketName)
        assertTrue(getResult.isSuccess, "Getting bucket tags should succeed")
        assertEquals(tags.toSet(), getResult.getOrThrow().toSet(), "Retrieved tags should match the set tags")

        val deleteResult = oss.deleteBucketTags(bucketName)
        assertTrue(deleteResult.isSuccess, "Deleting bucket tags should succeed")

        val getAfterDeleteResult = oss.getBucketTags(bucketName)
        assertTrue(getAfterDeleteResult.isSuccess, "Getting bucket tags after deletion should succeed")
        assertTrue(getAfterDeleteResult.getOrThrow().isEmpty(), "Tags should be empty after deletion")
      }
    }

    @Test
    fun `Should set and get object tags`(): Unit = minio {
      runBlocking {
        val bucketName = "test-object-tags"
        val objectName = "tagged-object.txt"
        val tags = listOf(Tag("type", "test-data"), Tag("version", "1"))
        oss.createBucket(CreateBucketRequest(bucketName))
        oss.putObject(bucketName, objectName, ByteArrayInputStream("data".toByteArray()), 4)

        val setResult = oss.setObjectTags(bucketName, objectName, tags)
        assertTrue(setResult.isSuccess, "Setting object tags should succeed")

        val getResult = oss.getObjectTags(bucketName, objectName)
        assertTrue(getResult.isSuccess, "Getting object tags should succeed")
        assertEquals(tags.toSet(), getResult.getOrThrow().toSet(), "Retrieved object tags should match the set tags")

        val deleteResult = oss.deleteObjectTags(bucketName, objectName)
        assertTrue(deleteResult.isSuccess, "Deleting object tags should succeed")

        val getAfterDeleteResult = oss.getObjectTags(bucketName, objectName)
        assertTrue(getAfterDeleteResult.isSuccess, "Getting object tags after deletion should succeed")
        assertTrue(getAfterDeleteResult.getOrThrow().isEmpty(), "Object tags should be empty after deletion")
      }
    }
  }

  @Nested
  inner class `Versioning feature tests` {
    @Test
    fun `Should list object versions`(): Unit = minio {
      runBlocking {
        val bucketName = "test-versioning-bucket-" + System.currentTimeMillis()
        val objectName = "versioned-object-" + System.currentTimeMillis() + ".txt"

        oss.createBucket(CreateBucketRequest(bucketName))
        oss.setBucketVersioning(bucketName, true)

        oss.putObject(bucketName, objectName, ByteArrayInputStream("v1".toByteArray()), 2)
        oss.putObject(bucketName, objectName, ByteArrayInputStream("v2".toByteArray()), 2)

        val versionsResult = oss.listObjectVersions(ListObjectVersionsRequest(bucketName = bucketName, prefix = objectName))
        assertTrue(versionsResult.isSuccess, "Listing object versions should succeed")
        val versions = versionsResult.getOrThrow()
        assertEquals(2, versions.versions.size, "There should be two versions")
      }
    }
  }

  @Nested
  inner class `Lifecycle and CORS feature tests` {
    @Test
    fun `Should set and get bucket lifecycle rules`(): Unit = minio {
      runBlocking {
        val bucketName = "test-lifecycle-bucket"
        val rules = listOf(LifecycleRule(id = "rule-1", prefix = "logs/", status = LifecycleRuleStatus.ENABLED, expiration = LifecycleExpiration(30)))
        oss.createBucket(CreateBucketRequest(bucketName))

        val setResult = oss.setBucketLifecycle(bucketName, rules)
        assertTrue(setResult.isSuccess, "Setting lifecycle rules should succeed")

        val getResult = oss.getBucketLifecycle(bucketName)
        assertTrue(getResult.isSuccess, "Getting lifecycle rules should succeed")
        val retrievedRules = getResult.getOrThrow()
        assertTrue(retrievedRules.isNotEmpty(), "Should get at least one lifecycle rule")
        assertEquals("rule-1", retrievedRules.first().id)

        val deleteResult = oss.deleteBucketLifecycle(bucketName)
        assertTrue(deleteResult.isSuccess, "Deleting lifecycle rules should succeed")
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
    fun `Should set and get bucket CORS rules`(): Unit = minio {
      runBlocking {
        val bucketName = "test-cors-bucket"
        val rules = listOf(CorsRule(allowedOrigins = listOf("*"), allowedMethods = listOf(HttpMethod.GET, HttpMethod.PUT)))
        oss.createBucket(CreateBucketRequest(bucketName))

        val setResult = oss.setBucketCors(bucketName, rules)
        assertTrue(setResult.isSuccess, "Setting CORS rules should succeed")

        val getResult = oss.getBucketCors(bucketName)
        assertTrue(getResult.isSuccess, "Getting CORS rules should succeed")
        val retrievedRules = getResult.getOrThrow()
        assertTrue(retrievedRules.isNotEmpty(), "Should get at least one CORS rule")
        assertEquals(listOf("*"), retrievedRules.first().allowedOrigins)

        val deleteResult = oss.deleteBucketCors(bucketName)
        assertTrue(deleteResult.isSuccess, "Deleting CORS rules should succeed")
      }
    }
  }

  @Import(MinioAutoConfiguration::class) @EnableConfigurationProperties(MinioProperties::class, OssProperties::class) class TestConfiguration
}
