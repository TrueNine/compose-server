package net.yan100.compose.core.http

import kotlin.test.Test
import kotlin.test.assertEquals

class MediaTypesTest {

  @Test
  fun `test matching media`() {
    val jpg = MediaTypes.JPEG
    val name = "image/jpeg"
    val found = MediaTypes.findVal(name)
    assertEquals(jpg, found)
    assertEquals(jpg.ext, "jpg")
  }
}
