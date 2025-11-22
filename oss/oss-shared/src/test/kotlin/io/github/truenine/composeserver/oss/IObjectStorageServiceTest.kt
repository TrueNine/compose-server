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
    fun `test create and check bucket`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"

      try {
        // create bucket
        val createResult = service.createBucket(CreateBucketRequest(bucketName))
        assertTrue(createResult.isSuccess, "create bucket should succeed")

        val bucketInfo = createResult.getOrThrow()
        assertEquals(bucketName, bucketInfo.name)

        // check if bucket exists
        val existsResult = service.bucketExists(bucketName)
        assertTrue(existsResult.isSuccess, "check bucket existence should succeed")
        assertTrue(existsResult.getOrThrow(), "bucket should exist")

        log.info("bucket operations test passed: $bucketName")
      } finally {
        // cleanup
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test list buckets`() = runBlocking {
      val service = createObjectStorageService()

      val listResult = service.listBuckets()
      assertTrue(listResult.isSuccess, "list buckets should succeed")

      val buckets = listResult.getOrThrow()
      log.info("found ${buckets.size} buckets")
    }

    @Test
    fun `test delete non-existent bucket`() = runBlocking {
      val service = createObjectStorageService()
      val nonExistentBucket = "non-existent-bucket-${System.currentTimeMillis()}"

      val deleteResult = service.deleteBucket(nonExistentBucket)
      // deleting a non-existent bucket may or may not succeed, depending on the implementation
      log.info("delete non-existent bucket result: ${deleteResult.isSuccess}")
    }

    @Test
    fun `test set bucket access level to public`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-access-public-${System.currentTimeMillis()}"

      try {
        // create bucket
        val createResult = service.createBucket(CreateBucketRequest(bucketName))
        assertTrue(createResult.isSuccess, "create bucket should succeed")

        // set bucket access level to public
        val setAccessResult = service.setBucketAccess(bucketName, BucketAccessLevel.PUBLIC)
        assertTrue(setAccessResult.isSuccess, "set bucket to public access should succeed")

        log.info("set bucket public access test passed: $bucketName")
      } finally {
        // cleanup
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test set bucket access level to private`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-access-private-${System.currentTimeMillis()}"

      try {
        // create bucket
        val createResult = service.createBucket(CreateBucketRequest(bucketName))
        assertTrue(createResult.isSuccess, "create bucket should succeed")

        // set bucket access level to private
        val setAccessResult = service.setBucketAccess(bucketName, BucketAccessLevel.PRIVATE)
        assertTrue(setAccessResult.isSuccess, "set bucket to private access should succeed")

        log.info("set bucket private access test passed: $bucketName")
      } finally {
        // cleanup
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test set access level for non-existent bucket`() = runBlocking {
      val service = createObjectStorageService()
      val nonExistentBucket = "non-existent-bucket-${System.currentTimeMillis()}"

      // try to set access level for a non-existent bucket
      val setAccessResult = service.setBucketAccess(nonExistentBucket, BucketAccessLevel.PUBLIC)
      assertTrue(setAccessResult.isFailure, "set access level for non-existent bucket should fail")

      val exception = setAccessResult.exceptionOrNull()
      assertTrue(exception is BucketNotFoundException, "should throw BucketNotFoundException")

      log.info("error handling for setting access level of non-existent bucket passed")
    }

    @Test
    fun `test idempotency of setting bucket access level`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-access-idempotent-${System.currentTimeMillis()}"

      try {
        // create bucket
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // set the same access level multiple times
        val firstSetResult = service.setBucketAccess(bucketName, BucketAccessLevel.PUBLIC)
        assertTrue(firstSetResult.isSuccess, "first time setting access level should succeed")

        val secondSetResult = service.setBucketAccess(bucketName, BucketAccessLevel.PUBLIC)
        assertTrue(secondSetResult.isSuccess, "second time setting the same access level should succeed")

        // change access level
        val changeAccessResult = service.setBucketAccess(bucketName, BucketAccessLevel.PRIVATE)
        assertTrue(changeAccessResult.isSuccess, "changing access level should succeed")

        // set the same access level again
        val thirdSetResult = service.setBucketAccess(bucketName, BucketAccessLevel.PRIVATE)
        assertTrue(thirdSetResult.isSuccess, "setting the same access level again should succeed")

        log.info("idempotency test for setting bucket access level passed: $bucketName")
      } finally {
        // cleanup
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ObjectOperationWithBucketCreation {

    @Test
    fun `test upload object to existing bucket`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-existing-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "Hello, existing bucket!"

      try {
        // create bucket first
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // use putObjectWithBucketCreation to upload to an existing bucket
        val uploadResult = service.putObjectWithBucketCreation(bucketName, objectName, content)
        assertTrue(uploadResult.isSuccess, "upload object to existing bucket should succeed")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)
        assertEquals(content.length.toLong(), objectInfo.size)

        // verify object exists and content is correct
        val downloadResult = service.getObjectString(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "download object should succeed")
        assertEquals(content, downloadResult.getOrThrow())

        log.info("upload object to existing bucket test passed: $bucketName/$objectName")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test upload object to non-existent bucket should auto create bucket`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-autocreate-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "Hello, auto-created bucket!"

      try {
        // ensure bucket does not exist
        val existsResult = service.bucketExists(bucketName)
        assertTrue(existsResult.isSuccess, "check bucket existence should succeed")
        assertFalse(existsResult.getOrThrow(), "bucket should not exist")

        // use putObjectWithBucketCreation to upload, should auto create bucket
        val uploadResult = service.putObjectWithBucketCreation(bucketName, objectName, content)
        assertTrue(uploadResult.isSuccess, "upload object and auto create bucket should succeed")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)
        assertEquals(content.length.toLong(), objectInfo.size)

        // verify bucket has been created
        val bucketExistsAfter = service.bucketExists(bucketName)
        assertTrue(bucketExistsAfter.isSuccess, "check bucket existence should succeed")
        assertTrue(bucketExistsAfter.getOrThrow(), "bucket should have been created")

        // verify object exists and content is correct
        val downloadResult = service.getObjectString(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "download object should succeed")
        assertEquals(content, downloadResult.getOrThrow())

        log.info("upload object and auto create bucket test passed: $bucketName/$objectName")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test upload object using PutObjectRequest to non-existent bucket`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-request-autocreate-${System.currentTimeMillis()}"
      val objectName = "test-request-object.txt"
      val content = "Hello, PutObjectRequest test!"
      val contentType = "text/plain; charset=utf-8"
      val metadata = mapOf("author" to "test", "version" to "1.0")

      try {
        // ensure bucket does not exist
        val existsResult = service.bucketExists(bucketName)
        assertFalse(existsResult.getOrThrow(), "bucket should not exist")

        // create PutObjectRequest
        val request =
          PutObjectRequest(
            bucketName = bucketName,
            objectName = objectName,
            inputStream = content.byteInputStream(),
            size = content.length.toLong(),
            contentType = contentType,
            metadata = metadata,
          )

        // use putObjectWithBucketCreation to upload
        val uploadResult = service.putObjectWithBucketCreation(request)
        assertTrue(uploadResult.isSuccess, "upload with PutObjectRequest should succeed")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)
        assertEquals(content.length.toLong(), objectInfo.size)
        assertEquals(contentType, objectInfo.contentType)
        assertTrue(objectInfo.metadata.containsKey("author"))
        assertEquals("test", objectInfo.metadata["author"])

        // verify bucket has been created
        assertTrue(service.bucketExists(bucketName).getOrThrow(), "bucket should have been created")

        // verify object content
        val downloadResult = service.getObjectString(bucketName, objectName)
        assertEquals(content, downloadResult.getOrThrow())

        log.info("upload with PutObjectRequest test passed: $bucketName/$objectName")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test upload byte array object to non-existent bucket`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bytes-autocreate-${System.currentTimeMillis()}"
      val objectName = "test-bytes-object.bin"
      val bytes = "Binary content with special chars: !@#$%^&*()".toByteArray(Charsets.UTF_8)

      try {
        // ensure bucket does not exist
        assertFalse(service.bucketExists(bucketName).getOrThrow(), "bucket should not exist")

        // use extension function to upload byte array, should auto create bucket
        val uploadResult = service.putObjectWithBucketCreation(bucketName, objectName, bytes, "application/octet-stream")
        assertTrue(uploadResult.isSuccess, "upload byte array and auto create bucket should succeed")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)
        assertEquals(bytes.size.toLong(), objectInfo.size)

        // verify bucket has been created
        assertTrue(service.bucketExists(bucketName).getOrThrow(), "bucket should have been created")

        // verify object content
        val downloadResult = service.getObjectBytes(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "download byte array should succeed")
        val downloadedBytes = downloadResult.getOrThrow()
        assertTrue(bytes.contentEquals(downloadedBytes), "downloaded byte array should be the same as original")

        log.info("upload byte array and auto create bucket test passed: $bucketName/$objectName (${bytes.size} bytes)")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test upload multiple objects to the same auto-created bucket`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-multiple-objects-${System.currentTimeMillis()}"
      val objects = listOf("file1.txt" to "Content of file 1", "file2.txt" to "Content of file 2", "folder/file3.txt" to "Content of file 3 in folder")

      try {
        // ensure bucket does not exist
        assertFalse(service.bucketExists(bucketName).getOrThrow(), "bucket should not exist")

        // upload first object, should auto create bucket
        val firstUploadResult = service.putObjectWithBucketCreation(bucketName, objects[0].first, objects[0].second)
        assertTrue(firstUploadResult.isSuccess, "first object upload should succeed")

        // verify bucket has been created
        assertTrue(service.bucketExists(bucketName).getOrThrow(), "bucket should have been created")

        // upload remaining objects to the existing bucket
        for (i in 1 until objects.size) {
          val (objectName, content) = objects[i]
          val uploadResult = service.putObjectWithBucketCreation(bucketName, objectName, content)
          assertTrue(uploadResult.isSuccess, "object $objectName upload should succeed")
        }

        // verify all objects exist and their content is correct
        objects.forEach { (objectName, expectedContent) ->
          val existsResult = service.objectExists(bucketName, objectName)
          assertTrue(existsResult.getOrThrow(), "object $objectName should exist")

          val downloadResult = service.getObjectString(bucketName, objectName)
          assertEquals(expectedContent, downloadResult.getOrThrow(), "object $objectName content should be correct")
        }

        log.info("upload multiple objects to auto-created bucket test passed: $bucketName (${objects.size} objects)")
      } finally {
        // cleanup all objects
        objects.forEach { (objectName, _) -> service.deleteObject(bucketName, objectName) }
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test idempotency of putObjectWithBucketCreation`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-idempotent-${System.currentTimeMillis()}"
      val objectName = "test-idempotent-object.txt"
      val content = "Idempotent test content"

      try {
        // first upload, should create bucket and object
        val firstUploadResult = service.putObjectWithBucketCreation(bucketName, objectName, content)
        assertTrue(firstUploadResult.isSuccess, "first upload should succeed")

        // second upload of the same object, should overwrite the original
        val updatedContent = "Updated idempotent test content"
        val secondUploadResult = service.putObjectWithBucketCreation(bucketName, objectName, updatedContent)
        assertTrue(secondUploadResult.isSuccess, "second upload should succeed")

        // verify object content is updated
        val downloadResult = service.getObjectString(bucketName, objectName)
        assertEquals(updatedContent, downloadResult.getOrThrow(), "object content should be updated")

        // verify bucket still exists
        assertTrue(service.bucketExists(bucketName).getOrThrow(), "bucket should still exist")

        log.info("putObjectWithBucketCreation idempotency test passed: $bucketName/$objectName")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ObjectOperations {

    @Test
    fun `test upload and download object`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-object.txt"
      val content = "Hello, World!"

      try {
        // create bucket
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // upload object
        val uploadResult = service.putObject(bucketName = bucketName, objectName = objectName, content = content, contentType = "text/plain; charset=utf-8")
        assertTrue(uploadResult.isSuccess, "upload object should succeed")

        val objectInfo = uploadResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)

        // check if object exists
        val existsResult = service.objectExists(bucketName, objectName)
        assertTrue(existsResult.isSuccess, "check object existence should succeed")
        assertTrue(existsResult.getOrThrow(), "object should exist")

        // download object
        val downloadResult = service.getObjectString(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "download object should succeed")
        assertEquals(content, downloadResult.getOrThrow())

        log.info("object operations test passed: $bucketName/$objectName")
      } finally {
        // cleanup
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test get object info`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-info.txt"
      val content = "Test content for info"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        val infoResult = service.getObjectInfo(bucketName, objectName)
        assertTrue(infoResult.isSuccess, "get object info should succeed")

        val objectInfo = infoResult.getOrThrow()
        assertEquals(bucketName, objectInfo.bucketName)
        assertEquals(objectName, objectInfo.objectName)
        assertTrue(objectInfo.size > 0, "object size should be greater than 0")

        log.info("object info test passed: ${objectInfo.size} bytes")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test delete object`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-delete.txt"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, "content to delete").getOrThrow()

        // confirm object exists
        assertTrue(service.objectExists(bucketName, objectName).getOrThrow())

        // delete object
        val deleteResult = service.deleteObject(bucketName, objectName)
        assertTrue(deleteResult.isSuccess, "delete object should succeed")

        // confirm object does not exist
        assertFalse(service.objectExists(bucketName, objectName).getOrThrow())

        log.info("object deletion test passed")
      } finally {
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ExtensionFunctions {

    @Test
    fun `test byte array upload and download`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val objectName = "test-bytes.bin"
      val bytes = "Binary content".toByteArray(Charsets.UTF_8)

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // upload byte array
        val uploadResult = service.putObject(bucketName, objectName, bytes)
        assertTrue(uploadResult.isSuccess, "upload byte array should succeed")

        // download byte array
        val downloadResult = service.getObjectBytes(bucketName, objectName)
        assertTrue(downloadResult.isSuccess, "download byte array should succeed")

        val downloadedBytes = downloadResult.getOrThrow()
        assertTrue(bytes.contentEquals(downloadedBytes), "downloaded byte array should be the same as original")

        log.info("byte array operations test passed: ${bytes.size} bytes")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test ensure bucket exists`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-ensure-bucket-${System.currentTimeMillis()}"

      try {
        // first call should create the bucket
        val firstResult = service.ensureBucket(bucketName)
        assertTrue(firstResult.isSuccess, "first ensureBucket call should succeed")

        // second call should return the existing bucket
        val secondResult = service.ensureBucket(bucketName)
        assertTrue(secondResult.isSuccess, "second ensureBucket call should succeed")

        log.info("ensure bucket exists test passed")
      } finally {
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ShareLinkOperations {

    @Test
    fun `test generate simple share link`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-share-object.txt"
      val content = "Hello, Share Link!"

      try {
        // create bucket and object
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        // generate share link
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1))
        assertTrue(shareResult.isSuccess, "generate share link should succeed")

        val shareInfo = shareResult.getOrThrow()
        assertEquals(bucketName, shareInfo.bucketName)
        assertEquals(objectName, shareInfo.objectName)
        assertTrue(shareInfo.shareUrl.isNotEmpty())

        log.info("share link generation test passed: ${shareInfo.shareUrl}")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test upload file and generate share link`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-upload-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-share-object.txt"
      val content = "Hello, Upload with Share Link!"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // upload string and generate share link
        val uploadResult =
          service.uploadStringWithLink(bucketName = bucketName, objectName = objectName, content = content, shareExpiration = java.time.Duration.ofHours(2))
        assertTrue(uploadResult.isSuccess, "upload and generate share link should succeed")

        val response = uploadResult.getOrThrow()
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)
        assertTrue(response.shareLink.shareUrl.isNotEmpty())
        assertNotNull(response.publicUrl)

        log.info("upload and generate share link test passed")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test validate share link`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-validate-bucket-${System.currentTimeMillis()}"
      val objectName = "test-validate-object.txt"
      val content = "Hello, Validate Link!"

      try {
        // create bucket and object
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        // generate share link
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofHours(1))
        val shareUrl = shareResult.getOrThrow().shareUrl

        // validate share link
        val validateResult = service.validateShareLink(shareUrl)
        assertTrue(validateResult.isSuccess, "validate share link should succeed")

        val validatedInfo = validateResult.getOrThrow()
        assertEquals(bucketName, validatedInfo.bucketName)
        assertEquals(objectName, validatedInfo.objectName)

        log.info("validate share link test passed")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }
  }

  @Nested
  inner class ErrorHandling {

    @Test
    fun `test access non-existent object`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-bucket-${System.currentTimeMillis()}"
      val nonExistentObject = "non-existent-object.txt"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        val existsResult = service.objectExists(bucketName, nonExistentObject)
        assertTrue(existsResult.isSuccess, "check for non-existent object should succeed")
        assertFalse(existsResult.getOrThrow(), "non-existent object should return false")

        val getResult = service.getObject(bucketName, nonExistentObject)
        assertTrue(getResult.isFailure, "get non-existent object should fail")

        log.info("error handling test passed")
      } finally {
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test generate share link for non-existent object`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-error-bucket-${System.currentTimeMillis()}"
      val nonExistentObject = "non-existent-object.txt"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // try to generate a share link for a non-existent object
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = nonExistentObject, expiration = java.time.Duration.ofHours(1))

        // depending on the implementation, this might succeed (link generated but fails on access) or fail
        // MinIO allows generating presigned URLs for non-existent objects
        log.info("generate share link for non-existent object result: ${shareResult.isSuccess}")
      } finally {
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test end-to-end share link download process`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-e2e-download-bucket-${System.currentTimeMillis()}"
      val objectName = "test-e2e-download-object.txt"
      val content = "Hello, End-to-End Download Test!"

      try {
        // create bucket and object
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()
        service.putObject(bucketName, objectName, content).getOrThrow()

        // generate share link
        val shareResult = service.generateSimpleShareLink(bucketName = bucketName, objectName = objectName, expiration = java.time.Duration.ofMinutes(5))
        assertTrue(shareResult.isSuccess, "generate share link should succeed")

        val shareInfo = shareResult.getOrThrow()
        val shareUrl = shareInfo.shareUrl

        // download content using the share link
        val downloadResult = service.downloadFromShareLink(shareUrl)
        assertTrue(downloadResult.isSuccess, "download via share link should succeed")

        val downloadedContent = downloadResult.getOrThrow()
        downloadedContent.use { objectContent ->
          val downloadedText = objectContent.inputStream.bufferedReader().readText()
          assertEquals(content, downloadedText, "downloaded content should match original content")
        }

        // download as string using extension function
        val stringDownloadResult = service.downloadStringFromShareLink(shareUrl)
        assertTrue(stringDownloadResult.isSuccess, "download as string using extension function should succeed")
        assertEquals(content, stringDownloadResult.getOrThrow(), "downloaded content from extension function should match")

        log.info("end-to-end share link download process test passed")
        log.info("share link: $shareUrl")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test full verification of upload and return link`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-upload-link-verify-bucket-${System.currentTimeMillis()}"
      val objectName = "test-upload-link-verify-object.txt"
      val content = "Hello, Upload Link Verification Test!"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // upload string and generate share link using extension function
        val uploadResult =
          service.uploadStringWithLink(bucketName = bucketName, objectName = objectName, content = content, shareExpiration = java.time.Duration.ofMinutes(10))
        assertTrue(uploadResult.isSuccess, "upload string and generate share link should succeed")

        val response = uploadResult.getOrThrow()

        // verify upload result
        assertEquals(bucketName, response.objectInfo.bucketName)
        assertEquals(objectName, response.objectInfo.objectName)
        assertEquals(content.length.toLong(), response.objectInfo.size)

        // verify share link info
        assertTrue(response.shareLink.shareUrl.isNotEmpty(), "share link should not be empty")
        assertEquals(bucketName, response.shareLink.bucketName)
        assertEquals(objectName, response.shareLink.objectName)
        assertEquals(HttpMethod.GET, response.shareLink.method)

        // verify object actually exists
        val existsResult = service.objectExists(bucketName, objectName)
        assertTrue(existsResult.isSuccess && existsResult.getOrThrow(), "uploaded object should exist")

        // verify content by downloading via share link
        val downloadResult = service.downloadStringFromShareLink(response.shareLink.shareUrl)
        assertTrue(downloadResult.isSuccess, "download via share link should succeed")
        assertEquals(content, downloadResult.getOrThrow(), "downloaded content should match uploaded content")

        // validate share link
        val validateResult = service.validateShareLink(response.shareLink.shareUrl)
        assertTrue(validateResult.isSuccess, "validate share link should succeed")

        val validatedInfo = validateResult.getOrThrow()
        assertEquals(bucketName, validatedInfo.bucketName)
        assertEquals(objectName, validatedInfo.objectName)

        log.info("full verification of upload and return link test passed")
        log.info("share link: ${response.shareLink.shareUrl}")
        log.info("public URL: ${response.publicUrl}")
      } finally {
        service.deleteObject(bucketName, objectName)
        service.deleteBucket(bucketName)
      }
    }

    @Test
    fun `test file upload download share link process`() = runBlocking {
      val service = createObjectStorageService()
      val bucketName = "test-file-share-bucket-${System.currentTimeMillis()}"
      val objectName = "test-file-share-object.txt"
      val content = "Hello, File Share Test!"

      try {
        service.createBucket(CreateBucketRequest(bucketName)).getOrThrow()

        // create a temporary file
        val tempFile = java.io.File.createTempFile("test-upload", ".txt")
        tempFile.writeText(content)

        try {
          // upload file and generate share link using extension function
          val uploadResult =
            service.uploadFileWithLink(
              bucketName = bucketName,
              objectName = objectName,
              file = tempFile,
              shareExpiration = java.time.Duration.ofMinutes(15),
              contentType = "text/plain",
            )
          assertTrue(uploadResult.isSuccess, "upload file and generate share link should succeed")

          val response = uploadResult.getOrThrow()

          // verify upload result
          assertEquals(bucketName, response.objectInfo.bucketName)
          assertEquals(objectName, response.objectInfo.objectName)
          assertEquals(tempFile.length(), response.objectInfo.size)

          // create a destination file for download
          val downloadFile = java.io.File.createTempFile("test-download", ".txt")

          try {
            // download to file from share link using extension function
            val downloadResult = service.downloadFileFromShareLink(shareUrl = response.shareLink.shareUrl, targetFile = downloadFile)
            assertTrue(downloadResult.isSuccess, "download to file from share link should succeed")

            // verify downloaded file content
            val downloadedContent = downloadFile.readText()
            assertEquals(content, downloadedContent, "downloaded file content should match original content")

            log.info("file upload download share link process test passed")
            log.info("original file size: ${tempFile.length()} bytes")
            log.info("downloaded file size: ${downloadFile.length()} bytes")
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
