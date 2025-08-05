package io.github.truenine.composeserver.enums

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MediaTypesTest {

  @Test
  fun `test matching media`() {
    val jpeg = MediaTypes.JPEG
    val name = "image/jpeg"
    val found = MediaTypes.findVal(name)
    assertEquals(jpeg, found)
    assertEquals("jpeg", jpeg.ext) // 现在应该返回第一个（最长的）扩展名
  }

  @Test
  fun `test extension arrays contain multiple variants`() {
    // 测试 JPEG 包含多个扩展名变体
    val jpeg = MediaTypes.JPEG
    val extensions = jpeg.exts
    assertTrue(extensions.contains("jpeg"))
    assertTrue(extensions.contains("jpg"))
    assertTrue(extensions.contains("jpe"))
    assertTrue(extensions.contains("jfif"))
    assertEquals("jpeg", jpeg.ext) // 第一个应该是最长的
  }

  @Test
  fun `test HTML extensions`() {
    val html = MediaTypes.HTML
    val extensions = html.exts
    assertTrue(extensions.contains("html"))
    assertTrue(extensions.contains("htm"))
    assertEquals("html", html.ext) // 第一个应该是最长的
  }

  @Test
  fun `test GZIP extensions`() {
    val gzip = MediaTypes.GZIP
    val extensions = gzip.exts
    assertTrue(extensions.contains("gzip"))
    assertTrue(extensions.contains("gz"))
    assertEquals("gzip", gzip.ext) // 第一个应该是最长的
  }

  @Test
  fun `test findByExtension method`() {
    // 测试根据扩展名查找
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jpeg"))
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jpg"))
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jpe"))
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jfif"))

    assertEquals(MediaTypes.HTML, MediaTypes.findByExtension("html"))
    assertEquals(MediaTypes.HTML, MediaTypes.findByExtension("htm"))

    assertEquals(MediaTypes.GZIP, MediaTypes.findByExtension("gzip"))
    assertEquals(MediaTypes.GZIP, MediaTypes.findByExtension("gz"))

    // 测试不存在的扩展名
    assertNull(MediaTypes.findByExtension("unknown"))
    assertNull(MediaTypes.findByExtension(""))
    assertNull(MediaTypes.findByExtension(null))
  }

  @Test
  fun `test TEXT extensions`() {
    val text = MediaTypes.TEXT
    val extensions = text.exts
    assertTrue(extensions.contains("txt"))
    assertTrue(extensions.contains("text"))
    assertEquals("txt", text.ext)
  }

  @Test
  fun `test M3U extensions`() {
    val m3u = MediaTypes.M3U
    val extensions = m3u.exts
    assertTrue(extensions.contains("m3u"))
    assertTrue(extensions.contains("m3u8"))
    assertEquals("m3u", m3u.ext)
  }

  @Test
  fun `test OGG extensions`() {
    val ogg = MediaTypes.OGG
    val extensions = ogg.exts
    assertTrue(extensions.contains("ogg"))
    assertTrue(extensions.contains("oga"))
    assertEquals("ogg", ogg.ext)
  }

  @Test
  fun `test JAVASCRIPT extensions`() {
    val js = MediaTypes.JAVASCRIPT
    val extensions = js.exts
    assertTrue(extensions.contains("js"))
    assertTrue(extensions.contains("mjs"))
    assertEquals("js", js.ext)
  }

  @Test
  fun `test empty extensions for special types`() {
    // 测试没有扩展名的特殊类型
    val url = MediaTypes.URL
    assertEquals(null, url.ext)
    assertTrue(url.exts.isEmpty())

    val multipart = MediaTypes.MULTIPART_FORM_DATA
    assertEquals(null, multipart.ext)
    assertTrue(multipart.exts.isEmpty())

    val sse = MediaTypes.SSE
    assertEquals(null, sse.ext)
    assertTrue(sse.exts.isEmpty())
  }

  @Test
  fun `test media type values`() {
    // 测试媒体类型值
    assertEquals("image/jpeg", MediaTypes.JPEG.value)
    assertEquals("text/html", MediaTypes.HTML.value)
    assertEquals("application/json", MediaTypes.JSON.value)
    assertEquals("video/mp4", MediaTypes.MP4.value)
  }

  @Test
  fun `test backward compatibility with existing API`() {
    // 测试向后兼容性
    val jpeg = MediaTypes.JPEG
    assertNotNull(jpeg.medias)
    assertTrue(jpeg.medias.contains("image/jpeg"))
    assertTrue(jpeg.medias.contains("image/jpg"))

    @Suppress("DEPRECATION") assertEquals(jpeg.value, jpeg.media())
  }

  @Test
  fun `test media type arrays contain multiple variants`() {
    // 测试媒体类型数组包含多个变体
    val xml = MediaTypes.XML
    val mediaTypes = xml.medias
    assertTrue(mediaTypes.contains("text/xml"))
    assertTrue(mediaTypes.contains("application/xml"))
    assertEquals("text/xml", xml.value) // 第一个应该是主要的媒体类型
  }

  @Test
  fun `test WAV media types`() {
    val wav = MediaTypes.WAV
    val mediaTypes = wav.medias
    assertTrue(mediaTypes.contains("audio/x-wav"))
    assertTrue(mediaTypes.contains("audio/wav"))
    assertEquals("audio/x-wav", wav.value) // 第一个应该是主要的媒体类型
  }

  @Test
  fun `test JAVASCRIPT media types`() {
    val js = MediaTypes.JAVASCRIPT
    val mediaTypes = js.medias
    assertTrue(mediaTypes.contains("text/javascript"))
    assertTrue(mediaTypes.contains("application/javascript"))
    assertEquals("text/javascript", js.value) // 第一个应该是主要的媒体类型
  }

  @Test
  fun `test GZIP media types`() {
    val gzip = MediaTypes.GZIP
    val mediaTypes = gzip.medias
    assertTrue(mediaTypes.contains("application/x-gzip"))
    assertTrue(mediaTypes.contains("application/gzip"))
    assertEquals("application/x-gzip", gzip.value) // 第一个应该是主要的媒体类型
  }

  @Test
  fun `test EXE media types`() {
    val exe = MediaTypes.EXE
    val mediaTypes = exe.medias
    assertTrue(mediaTypes.contains("application/ms-download"))
    assertTrue(mediaTypes.contains("application/octet-stream"))
    assertEquals("application/ms-download", exe.value) // 第一个应该是主要的媒体类型
  }

  @Test
  fun `test findVal method with multiple media types`() {
    // 测试 findVal 方法能够找到所有媒体类型变体
    assertEquals(MediaTypes.XML, MediaTypes.findVal("text/xml"))
    assertEquals(MediaTypes.XML, MediaTypes.findVal("application/xml"))

    assertEquals(MediaTypes.WAV, MediaTypes.findVal("audio/x-wav"))
    assertEquals(MediaTypes.WAV, MediaTypes.findVal("audio/wav"))

    assertEquals(MediaTypes.JAVASCRIPT, MediaTypes.findVal("text/javascript"))
    assertEquals(MediaTypes.JAVASCRIPT, MediaTypes.findVal("application/javascript"))

    assertEquals(MediaTypes.GZIP, MediaTypes.findVal("application/x-gzip"))
    assertEquals(MediaTypes.GZIP, MediaTypes.findVal("application/gzip"))

    assertEquals(MediaTypes.EXE, MediaTypes.findVal("application/ms-download"))
    assertEquals(MediaTypes.EXE, MediaTypes.findVal("application/octet-stream"))
  }

  @Test
  fun `test single media type entries`() {
    // 测试只有单个媒体类型的条目
    val png = MediaTypes.PNG
    assertEquals(1, png.medias.size)
    assertEquals("image/png", png.medias[0])
    assertEquals("image/png", png.value)

    val json = MediaTypes.JSON
    assertEquals(1, json.medias.size)
    assertEquals("application/json", json.medias[0])
    assertEquals("application/json", json.value)
  }
}
