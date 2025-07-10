package io.github.truenine.composeserver.typing

import kotlin.test.Test
import kotlin.test.assertEquals

class MimeTypesTest {

  @Test
  fun `test matching media`() {
    val jpg = MimeTypes.JPEG
    val name = "image/jpeg"
    val found = MimeTypes.findVal(name)
    assertEquals(jpg, found)
    assertEquals(jpg.ext, "jpg")
  }
}
