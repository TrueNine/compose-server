package io.github.truenine.composeserver.depend.servlet

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.http.MediaType

class SpringResponseEntityFnsTest {

  @Test
  fun `test exists response entity with various types`() {
    headMethodResponse {
      // Boolean tests
      exists { true }
      assertEquals(200, this@headMethodResponse.status)

      exists { false }
      assertEquals(404, this@headMethodResponse.status)

      // Null tests
      exists { null }
      assertEquals(404, this@headMethodResponse.status)

      // Number tests
      exists { 0 }
      assertEquals(200, this@headMethodResponse.status)

      exists { -1 }
      assertEquals(404, this@headMethodResponse.status)

      exists { 42 }
      assertEquals(200, this@headMethodResponse.status)

      // String tests
      exists { "non-empty" }
      assertEquals(200, this@headMethodResponse.status)

      exists { "" }
      assertEquals(404, this@headMethodResponse.status)

      // Array tests
      exists { arrayOf(1, 2, 3) }
      assertEquals(200, this@headMethodResponse.status)

      exists { emptyArray<Any>() }
      assertEquals(404, this@headMethodResponse.status)

      // List tests
      exists { listOf(1, 2, 3) }
      assertEquals(200, this@headMethodResponse.status)

      exists { emptyList<Any>() }
      assertEquals(404, this@headMethodResponse.status)

      // Map tests
      exists { mapOf("key" to "value") }
      assertEquals(200, this@headMethodResponse.status)

      exists { emptyMap<String, Any>() }
      assertEquals(404, this@headMethodResponse.status)

      // Unit test
      exists { Unit }
      assertEquals(404, this@headMethodResponse.status)

      // Any object test
      exists { Any() }
      assertEquals(200, this@headMethodResponse.status)
    }
  }

  @Test
  fun `test ResponseEntityScope type function`() {
    val response = headMethodResponse {
      type("application/json")
      exists { true }
    }

    assertEquals(200, response.statusCode.value())
    assertEquals(MediaType.APPLICATION_JSON, response.headers.contentType)
  }

  @Test
  fun `test ResponseEntityScope type with invalid media type`() {
    val response = headMethodResponse {
      type("invalid/media/type/format")
      exists { true }
    }

    assertEquals(200, response.statusCode.value())
    assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.headers.contentType)
  }

  @Test
  fun `test ResponseEntityScope size function`() {
    val response = headMethodResponse {
      size { 1024L }
      exists { true }
    }

    assertEquals(200, response.statusCode.value())
    assertEquals(1024L, response.headers.contentLength)
  }

  @Test
  fun `test ResponseEntityScope size with null`() {
    val response = headMethodResponse {
      size { null }
      exists { true }
    }

    assertEquals(200, response.statusCode.value())
    assertEquals(-1L, response.headers.contentLength) // Spring default when not set
  }

  @Test
  fun `test ResponseEntityScope lastModifyBy function`() {
    val testDateTime = LocalDateTime.of(2025, 7, 13, 12, 0, 0)
    val response = headMethodResponse {
      lastModifyBy { testDateTime }
      exists { true }
    }

    assertEquals(200, response.statusCode.value())
    assertNotNull(response.headers.lastModified)
    assertTrue(response.headers.lastModified > 0)
  }

  @Test
  fun `test ResponseEntityScope lastModifyBy with null`() {
    val response = headMethodResponse {
      lastModifyBy { null }
      exists { true }
    }

    assertEquals(200, response.statusCode.value())
    assertEquals(-1L, response.headers.lastModified) // Spring default when not set
  }

  @Test
  fun `test complete response with all headers`() {
    val testDateTime = LocalDateTime.of(2025, 7, 13, 12, 0, 0)
    val response = headMethodResponse {
      type("text/plain")
      size { 256L }
      lastModifyBy { testDateTime }
      exists { "content exists" }
    }

    assertEquals(200, response.statusCode.value())
    assertEquals(MediaType.TEXT_PLAIN, response.headers.contentType)
    assertEquals(256L, response.headers.contentLength)
    assertNotNull(response.headers.lastModified)
    assertTrue(response.headers.lastModified > 0)
  }

  @Test
  fun `test response with 404 status should not affect other headers`() {
    val testDateTime = LocalDateTime.of(2025, 7, 13, 12, 0, 0)
    val response = headMethodResponse {
      type("application/xml")
      size { 512L }
      lastModifyBy { testDateTime }
      exists { false } // This should set status to 404
    }

    assertEquals(404, response.statusCode.value())
    assertEquals(MediaType.APPLICATION_XML, response.headers.contentType)
    assertEquals(512L, response.headers.contentLength)
    assertNotNull(response.headers.lastModified)
  }

  @Test
  fun `test ResponseEntityScope default values`() {
    val response = headMethodResponse {
      // No configuration, should use defaults
    }

    assertEquals(200, response.statusCode.value()) // Default status
    assertEquals(-1L, response.headers.contentLength) // No content length set
    assertEquals(-1L, response.headers.lastModified) // No last modified set
    // No content type set by default
  }

  @Test
  fun `test exists with complex objects`() {
    data class TestObject(val value: String)

    headMethodResponse {
      exists { TestObject("test") }
      assertEquals(200, this@headMethodResponse.status)
    }
  }

  @Test
  fun `test exists with nested collections`() {
    headMethodResponse {
      exists { listOf(listOf(1, 2), listOf(3, 4)) }
      assertEquals(200, this@headMethodResponse.status)

      exists { listOf(emptyList<Int>()) } // List with empty nested list
      assertEquals(200, this@headMethodResponse.status)
    }
  }

  @Test
  fun `test multiple exists calls should use last result`() {
    headMethodResponse {
      exists { true }
      assertEquals(200, this@headMethodResponse.status)

      exists { false } // Should override previous
      assertEquals(404, this@headMethodResponse.status)

      exists { "final result" } // Should override again
      assertEquals(200, this@headMethodResponse.status)
    }
  }
}
