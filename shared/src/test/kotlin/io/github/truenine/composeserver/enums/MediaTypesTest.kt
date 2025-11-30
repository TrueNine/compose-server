package io.github.truenine.composeserver.enums

import kotlin.test.*

class MediaTypesTest {

  @Test
  fun `test matching media`() {
    val jpeg = MediaTypes.JPEG
    val name = "image/jpeg"
    val found = MediaTypes.findVal(name)
    assertEquals(jpeg, found)
    assertEquals("jpeg", jpeg.ext) // Should return the first (longest) extension
  }

  @Test
  fun `test extension arrays contain multiple variants`() {
    // Verify that JPEG exposes multiple extension variants
    val jpeg = MediaTypes.JPEG
    val extensions = jpeg.exts
    assertTrue(extensions.contains("jpeg"))
    assertTrue(extensions.contains("jpg"))
    assertTrue(extensions.contains("jpe"))
    assertTrue(extensions.contains("jfif"))
    assertEquals("jpeg", jpeg.ext) // The first entry should be the longest value
  }

  @Test
  fun `test HTML extensions`() {
    val html = MediaTypes.HTML
    val extensions = html.exts
    assertTrue(extensions.contains("html"))
    assertTrue(extensions.contains("htm"))
    assertEquals("html", html.ext) // The first entry should be the longest value
  }

  @Test
  fun `test GZIP extensions`() {
    val gzip = MediaTypes.GZIP
    val extensions = gzip.exts
    assertTrue(extensions.contains("gzip"))
    assertTrue(extensions.contains("gz"))
    assertEquals("gzip", gzip.ext) // The first entry should be the longest value
  }

  @Test
  fun `test findByExtension method`() {
    // Verify lookup by extension
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jpeg"))
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jpg"))
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jpe"))
    assertEquals(MediaTypes.JPEG, MediaTypes.findByExtension("jfif"))

    assertEquals(MediaTypes.HTML, MediaTypes.findByExtension("html"))
    assertEquals(MediaTypes.HTML, MediaTypes.findByExtension("htm"))

    assertEquals(MediaTypes.GZIP, MediaTypes.findByExtension("gzip"))
    assertEquals(MediaTypes.GZIP, MediaTypes.findByExtension("gz"))

    // Verify behaviour for unknown extensions
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
    // Verify special media types without extensions
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
    // Verify media type canonical values
    assertEquals("image/jpeg", MediaTypes.JPEG.value)
    assertEquals("text/html", MediaTypes.HTML.value)
    assertEquals("application/json", MediaTypes.JSON.value)
    assertEquals("video/mp4", MediaTypes.MP4.value)
  }

  @Test
  fun `test backward compatibility with existing API`() {
    // Verify backward compatibility with existing API
    val jpeg = MediaTypes.JPEG
    assertNotNull(jpeg.medias)
    assertTrue(jpeg.medias.contains("image/jpeg"))
    assertTrue(jpeg.medias.contains("image/jpg"))

    @Suppress("DEPRECATION") assertEquals(jpeg.value, jpeg.media())
  }

  @Test
  fun `test media type arrays contain multiple variants`() {
    // Verify that media type arrays include multiple variants
    val xml = MediaTypes.XML
    val mediaTypes = xml.medias
    assertTrue(mediaTypes.contains("text/xml"))
    assertTrue(mediaTypes.contains("application/xml"))
    assertEquals("text/xml", xml.value) // The first entry should be the primary media type
  }

  @Test
  fun `test WAV media types`() {
    val wav = MediaTypes.WAV
    val mediaTypes = wav.medias
    assertTrue(mediaTypes.contains("audio/x-wav"))
    assertTrue(mediaTypes.contains("audio/wav"))
    assertEquals("audio/x-wav", wav.value) // The first entry should be the primary media type
  }

  @Test
  fun `test JAVASCRIPT media types`() {
    val js = MediaTypes.JAVASCRIPT
    val mediaTypes = js.medias
    assertTrue(mediaTypes.contains("text/javascript"))
    assertTrue(mediaTypes.contains("application/javascript"))
    assertEquals("text/javascript", js.value) // The first entry should be the primary media type
  }

  @Test
  fun `test GZIP media types`() {
    val gzip = MediaTypes.GZIP
    val mediaTypes = gzip.medias
    assertTrue(mediaTypes.contains("application/x-gzip"))
    assertTrue(mediaTypes.contains("application/gzip"))
    assertEquals("application/x-gzip", gzip.value) // The first entry should be the primary media type
  }

  @Test
  fun `test EXE media types`() {
    val exe = MediaTypes.EXE
    val mediaTypes = exe.medias
    assertTrue(mediaTypes.contains("application/ms-download"))
    assertTrue(mediaTypes.contains("application/octet-stream"))
    assertEquals("application/ms-download", exe.value) // The first entry should be the primary media type
  }

  @Test
  fun `test findVal method with multiple media types`() {
    // Verify that findVal resolves every media type variant
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
    // Verify entries that expose a single media type
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
