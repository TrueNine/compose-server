package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UrlExtensionsTest {

  @Nested
  inner class `URL路径构建测试` {

    @Test
    fun `测试空参数构建路径`() {
      val path = buildUrlPath()
      assertEquals("/", path)
    }

    @Test
    fun `测试单个段构建路径`() {
      val path = buildUrlPath("segment")
      assertEquals("/segment", path)
    }

    @Test
    fun `测试多个段构建路径`() {
      val path = buildUrlPath("bucket", "folder", "file.txt")
      assertEquals("/bucket/folder/file.txt", path)
    }

    @Test
    fun `测试包含斜杠的段`() {
      val path = buildUrlPath("/bucket/", "/folder/", "/file.txt/")
      assertEquals("/bucket/folder/file.txt", path)
    }

    @Test
    fun `测试空白段被过滤`() {
      val path = buildUrlPath("bucket", "", "  ", "file.txt")
      assertEquals("/bucket/file.txt", path)
    }

    @Test
    fun `测试所有段都为空`() {
      val path = buildUrlPath("", "  ", "   ")
      assertEquals("/", path)
    }
  }

  @Nested
  inner class `完整URL构建测试` {

    @Test
    fun `测试基础URL和路径段`() {
      val url = buildUrl("https://example.com", "bucket", "file.txt")
      assertEquals("https://example.com/bucket/file.txt", url)
    }

    @Test
    fun `测试基础URL末尾有斜杠`() {
      val url = buildUrl("https://example.com/", "bucket", "file.txt")
      assertEquals("https://example.com/bucket/file.txt", url)
    }

    @Test
    fun `测试无路径段`() {
      val url = buildUrl("https://example.com")
      assertEquals("https://example.com", url)
    }

    @Test
    fun `测试空路径段`() {
      val url = buildUrl("https://example.com", "", "  ")
      assertEquals("https://example.com", url)
    }
  }

  @Nested
  inner class `对象URL构建测试` {

    @Test
    fun `测试构建对象URL`() {
      val url = buildObjectUrl("https://minio.example.com", "my-bucket", "path/to/file.jpg")
      assertEquals("https://minio.example.com/my-bucket/path/to/file.jpg", url)
    }

    @Test
    fun `测试构建对象URL包含特殊字符`() {
      val url = buildObjectUrl("https://minio.example.com", "my-bucket", "folder with spaces/file-name.jpg")
      assertEquals("https://minio.example.com/my-bucket/folder with spaces/file-name.jpg", url)
    }
  }

  @Nested
  inner class `URL编码测试` {

    @Test
    fun `测试字符串URL编码`() {
      val encoded = "hello world".urlEncode()
      assertEquals("hello+world", encoded)
    }

    @Test
    fun `测试特殊字符URL编码`() {
      val encoded = "hello@world#test".urlEncode()
      assertEquals("hello%40world%23test", encoded)
    }

    @Test
    fun `测试中文字符URL编码`() {
      val encoded = "测试文件".urlEncode()
      log.info("Encoded Chinese: {}", encoded)
      // 中文字符应该被正确编码
      assertEquals("%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6", encoded)
    }

    @Test
    fun `测试路径URL编码保留分隔符`() {
      val encoded = "folder/sub folder/file name.txt".urlEncodePath()
      assertEquals("folder/sub+folder/file+name.txt", encoded)
    }

    @Test
    fun `测试路径URL编码处理特殊字符`() {
      val encoded = "folder@test/file#name.txt".urlEncodePath()
      assertEquals("folder%40test/file%23name.txt", encoded)
    }
  }

  @Nested
  inner class `查询字符串构建测试` {

    @Test
    fun `测试空参数构建查询字符串`() {
      val query = buildQueryString(emptyMap())
      assertEquals("", query)
    }

    @Test
    fun `测试单个参数构建查询字符串`() {
      val query = buildQueryString(mapOf("key" to "value"))
      assertEquals("key=value", query)
    }

    @Test
    fun `测试多个参数构建查询字符串`() {
      val query = buildQueryString(mapOf("key1" to "value1", "key2" to "value2"))
      // 注意：Map的顺序可能不确定，所以我们检查包含关系
      log.info("Query string: {}", query)
      assertEquals(true, query.contains("key1=value1"))
      assertEquals(true, query.contains("key2=value2"))
      assertEquals(true, query.contains("&"))
    }

    @Test
    fun `测试参数值需要编码`() {
      val query = buildQueryString(mapOf("key" to "value with spaces"))
      assertEquals("key=value+with+spaces", query)
    }

    @Test
    fun `测试过滤空值参数`() {
      val query = buildQueryString(mapOf("key1" to "value1", "key2" to "", "key3" to "value3"))
      log.info("Query string with empty values: {}", query)
      assertEquals(true, query.contains("key1=value1"))
      assertEquals(false, query.contains("key2="))
      assertEquals(true, query.contains("key3=value3"))
    }

    @Test
    fun `测试包含空值参数`() {
      val query = buildQueryString(mapOf("key1" to "value1", "key2" to "", "key3" to "value3"), includeEmpty = true)
      log.info("Query string including empty values: {}", query)
      assertEquals(true, query.contains("key1=value1"))
      assertEquals(true, query.contains("key2="))
      assertEquals(true, query.contains("key3=value3"))
    }
  }

  @Nested
  inner class `带查询参数的URL构建测试` {

    @Test
    fun `测试构建带查询参数的URL`() {
      val url = buildUrlWithQuery("https://example.com", arrayOf("api", "v1", "users"), mapOf("page" to "1", "size" to "10"))
      log.info("URL with query: {}", url)
      assertEquals(true, url.startsWith("https://example.com/api/v1/users?"))
      assertEquals(true, url.contains("page=1"))
      assertEquals(true, url.contains("size=10"))
    }

    @Test
    fun `测试构建无查询参数的URL`() {
      val url = buildUrlWithQuery("https://example.com", arrayOf("api", "v1", "users"), emptyMap())
      assertEquals("https://example.com/api/v1/users", url)
    }

    @Test
    fun `测试构建无路径段的URL`() {
      val url = buildUrlWithQuery("https://example.com", emptyArray(), mapOf("query" to "test"))
      assertEquals("https://example.com?query=test", url)
    }
  }

  @Nested
  inner class `文件扩展名提取测试` {

    @Test
    fun `测试提取常见文件扩展名`() {
      assertEquals("jpg", extractFileExtension("file.jpg"))
      assertEquals("txt", extractFileExtension("document.txt"))
      assertEquals("pdf", extractFileExtension("report.pdf"))
    }

    @Test
    fun `测试提取URL中的文件扩展名`() {
      assertEquals("jpg", extractFileExtension("https://example.com/images/photo.jpg"))
      assertEquals("png", extractFileExtension("https://example.com/path/to/image.PNG"))
    }

    @Test
    fun `测试带查询参数的URL`() {
      assertEquals("jpg", extractFileExtension("https://example.com/image.jpg?version=1&size=large"))
    }

    @Test
    fun `测试无扩展名的文件`() {
      assertEquals("", extractFileExtension("filename"))
      assertEquals("", extractFileExtension("https://example.com/path/filename"))
    }

    @Test
    fun `测试多个点的文件名`() {
      assertEquals("gz", extractFileExtension("archive.tar.gz"))
      assertEquals("txt", extractFileExtension("file.backup.txt"))
    }

    @Test
    fun `测试目录路径`() {
      assertEquals("", extractFileExtension("https://example.com/path/to/directory/"))
      assertEquals("", extractFileExtension("/path/to/directory"))
    }
  }

  @Nested
  inner class `URL规范化测试` {

    @Test
    fun `测试规范化简单URL`() {
      val normalized = normalizeUrl("https://example.com/path/to/file")
      assertEquals("https://example.com/path/to/file", normalized)
    }

    @Test
    fun `测试规范化包含多余斜杠的URL`() {
      val normalized = normalizeUrl("https://example.com//path///to//file")
      assertEquals("https://example.com/path/to/file", normalized)
    }

    @Test
    fun `测试规范化包含当前目录的URL`() {
      val normalized = normalizeUrl("https://example.com/path/./to/file")
      assertEquals("https://example.com/path/to/file", normalized)
    }

    @Test
    fun `测试规范化包含父目录的URL`() {
      val normalized = normalizeUrl("https://example.com/path/to/../file")
      assertEquals("https://example.com/path/file", normalized)
    }

    @Test
    fun `测试规范化复杂路径`() {
      val normalized = normalizeUrl("https://example.com/path/./to/../from/./file")
      assertEquals("https://example.com/path/from/file", normalized)
    }

    @Test
    fun `测试规范化无协议的路径`() {
      val normalized = normalizeUrl("/path/./to/../file")
      assertEquals("/path/file", normalized)
    }

    @Test
    fun `测试规范化空白URL`() {
      val normalized = normalizeUrl("")
      assertEquals("", normalized)
    }

    @Test
    fun `测试规范化只有协议和主机的URL`() {
      val normalized = normalizeUrl("https://example.com")
      assertEquals("https://example.com", normalized)
    }
  }
}
