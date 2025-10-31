package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UrlExtensionsTest {

  @Nested
  inner class UrlPathBuilderTests {

    @Test
    fun buildsPathWithNoSegments() {
      val path = buildUrlPath()
      assertEquals("/", path)
    }

    @Test
    fun buildsPathWithSingleSegment() {
      val path = buildUrlPath("segment")
      assertEquals("/segment", path)
    }

    @Test
    fun buildsPathWithMultipleSegments() {
      val path = buildUrlPath("bucket", "folder", "file.txt")
      assertEquals("/bucket/folder/file.txt", path)
    }

    @Test
    fun trimsSlashesFromSegments() {
      val path = buildUrlPath("/bucket/", "/folder/", "/file.txt/")
      assertEquals("/bucket/folder/file.txt", path)
    }

    @Test
    fun filtersBlankSegments() {
      val path = buildUrlPath("bucket", "", "  ", "file.txt")
      assertEquals("/bucket/file.txt", path)
    }

    @Test
    fun returnsRootPathWhenSegmentsAreBlank() {
      val path = buildUrlPath("", "  ", "   ")
      assertEquals("/", path)
    }
  }

  @Nested
  inner class FullUrlBuilderTests {

    @Test
    fun buildsFullUrlWithSegments() {
      val url = buildUrl("https://example.com", "bucket", "file.txt")
      assertEquals("https://example.com/bucket/file.txt", url)
    }

    @Test
    fun trimsTrailingSlashInBaseUrl() {
      val url = buildUrl("https://example.com/", "bucket", "file.txt")
      assertEquals("https://example.com/bucket/file.txt", url)
    }

    @Test
    fun returnsBaseUrlWhenNoSegmentsProvided() {
      val url = buildUrl("https://example.com")
      assertEquals("https://example.com", url)
    }

    @Test
    fun ignoresBlankSegments() {
      val url = buildUrl("https://example.com", "", "  ")
      assertEquals("https://example.com", url)
    }
  }

  @Nested
  inner class ObjectUrlBuilderTests {

    @Test
    fun buildsObjectUrl() {
      val url = buildObjectUrl("https://minio.example.com", "my-bucket", "path/to/file.jpg")
      assertEquals("https://minio.example.com/my-bucket/path/to/file.jpg", url)
    }

    @Test
    fun buildsObjectUrlWithSpecialCharacters() {
      val url = buildObjectUrl("https://minio.example.com", "my-bucket", "folder with spaces/file-name.jpg")
      assertEquals("https://minio.example.com/my-bucket/folder with spaces/file-name.jpg", url)
    }
  }

  @Nested
  inner class UrlEncodingTests {

    @Test
    fun encodesBasicString() {
      val encoded = "hello world".urlEncode()
      assertEquals("hello+world", encoded)
    }

    @Test
    fun encodesSpecialCharacters() {
      val encoded = "hello@world#test".urlEncode()
      assertEquals("hello%40world%23test", encoded)
    }

    @Test
    fun encodesNonLatinCharacters() {
      val original = "fichier d'essai"
      val encoded = original.urlEncode()
      log.info("Encoded non-Latin string: {}", encoded)
      assertEquals(URLEncoder.encode(original, StandardCharsets.UTF_8.name()), encoded)
    }

    @Test
    fun encodesPathWhilePreservingSeparators() {
      val encoded = "folder/sub folder/file name.txt".urlEncodePath()
      assertEquals("folder/sub+folder/file+name.txt", encoded)
    }

    @Test
    fun encodesPathSpecialCharacters() {
      val encoded = "folder@test/file#name.txt".urlEncodePath()
      assertEquals("folder%40test/file%23name.txt", encoded)
    }
  }

  @Nested
  inner class QueryStringBuilderTests {

    @Test
    fun buildsQueryStringWithNoParameters() {
      val query = buildQueryString(emptyMap())
      assertEquals("", query)
    }

    @Test
    fun buildsQueryStringWithSingleParameter() {
      val query = buildQueryString(mapOf("key" to "value"))
      assertEquals("key=value", query)
    }

    @Test
    fun buildsQueryStringWithMultipleParameters() {
      val query = buildQueryString(mapOf("key1" to "value1", "key2" to "value2"))
      // Map iteration order is undefined, so we perform containment checks
      log.info("Query string: {}", query)
      assertEquals(true, query.contains("key1=value1"))
      assertEquals(true, query.contains("key2=value2"))
      assertEquals(true, query.contains("&"))
    }

    @Test
    fun encodesParameterValues() {
      val query = buildQueryString(mapOf("key" to "value with spaces"))
      assertEquals("key=value+with+spaces", query)
    }

    @Test
    fun filtersEmptyValues() {
      val query = buildQueryString(mapOf("key1" to "value1", "key2" to "", "key3" to "value3"))
      log.info("Query string with empty values: {}", query)
      assertEquals(true, query.contains("key1=value1"))
      assertEquals(false, query.contains("key2="))
      assertEquals(true, query.contains("key3=value3"))
    }

    @Test
    fun includesEmptyValuesWhenRequested() {
      val query = buildQueryString(mapOf("key1" to "value1", "key2" to "", "key3" to "value3"), includeEmpty = true)
      log.info("Query string including empty values: {}", query)
      assertEquals(true, query.contains("key1=value1"))
      assertEquals(true, query.contains("key2="))
      assertEquals(true, query.contains("key3=value3"))
    }
  }

  @Nested
  inner class UrlWithQueryBuilderTests {

    @Test
    fun buildsUrlWithQueryParameters() {
      val url = buildUrlWithQuery("https://example.com", arrayOf("api", "v1", "users"), mapOf("page" to "1", "size" to "10"))
      log.info("URL with query: {}", url)
      assertEquals(true, url.startsWith("https://example.com/api/v1/users?"))
      assertEquals(true, url.contains("page=1"))
      assertEquals(true, url.contains("size=10"))
    }

    @Test
    fun buildsUrlWithoutQueryParameters() {
      val url = buildUrlWithQuery("https://example.com", arrayOf("api", "v1", "users"), emptyMap())
      assertEquals("https://example.com/api/v1/users", url)
    }

    @Test
    fun buildsUrlWithoutPathSegments() {
      val url = buildUrlWithQuery("https://example.com", emptyArray(), mapOf("query" to "test"))
      assertEquals("https://example.com?query=test", url)
    }
  }

  @Nested
  inner class FileExtensionExtractionTests {

    @Test
    fun extractsCommonFileExtensions() {
      assertEquals("jpg", extractFileExtension("file.jpg"))
      assertEquals("txt", extractFileExtension("document.txt"))
      assertEquals("pdf", extractFileExtension("report.pdf"))
    }

    @Test
    fun extractsExtensionsFromUrl() {
      assertEquals("jpg", extractFileExtension("https://example.com/images/photo.jpg"))
      assertEquals("png", extractFileExtension("https://example.com/path/to/image.PNG"))
    }

    @Test
    fun extractsExtensionsFromUrlWithQuery() {
      assertEquals("jpg", extractFileExtension("https://example.com/image.jpg?version=1&size=large"))
    }

    @Test
    fun returnsEmptyExtensionWhenAbsent() {
      assertEquals("", extractFileExtension("filename"))
      assertEquals("", extractFileExtension("https://example.com/path/filename"))
    }

    @Test
    fun extractsExtensionFromMultiDotFileNames() {
      assertEquals("gz", extractFileExtension("archive.tar.gz"))
      assertEquals("txt", extractFileExtension("file.backup.txt"))
    }

    @Test
    fun returnsEmptyExtensionForDirectories() {
      assertEquals("", extractFileExtension("https://example.com/path/to/directory/"))
      assertEquals("", extractFileExtension("/path/to/directory"))
    }
  }

  @Nested
  inner class UrlNormalizationTests {

    @Test
    fun normalizesSimpleUrl() {
      val normalized = normalizeUrl("https://example.com/path/to/file")
      assertEquals("https://example.com/path/to/file", normalized)
    }

    @Test
    fun normalizesUrlWithExtraSlashes() {
      val normalized = normalizeUrl("https://example.com//path///to//file")
      assertEquals("https://example.com/path/to/file", normalized)
    }

    @Test
    fun normalizesUrlWithDotSegments() {
      val normalized = normalizeUrl("https://example.com/path/./to/file")
      assertEquals("https://example.com/path/to/file", normalized)
    }

    @Test
    fun normalizesUrlWithParentDirectorySegments() {
      val normalized = normalizeUrl("https://example.com/path/to/../file")
      assertEquals("https://example.com/path/file", normalized)
    }

    @Test
    fun normalizesComplexPath() {
      val normalized = normalizeUrl("https://example.com/path/./to/../from/./file")
      assertEquals("https://example.com/path/from/file", normalized)
    }

    @Test
    fun normalizesPathWithoutScheme() {
      val normalized = normalizeUrl("/path/./to/../file")
      assertEquals("/path/file", normalized)
    }

    @Test
    fun normalizesBlankUrl() {
      val normalized = normalizeUrl("")
      assertEquals("", normalized)
    }

    @Test
    fun normalizesUrlWithOnlySchemeAndHost() {
      val normalized = normalizeUrl("https://example.com")
      assertEquals("https://example.com", normalized)
    }
  }
}
