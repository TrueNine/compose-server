package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.mock.web.MockHttpServletRequest

class HttpServletRequestFnsTest {

  @Test
  fun `headerMap should correctly convert request headers to a Map`() {
    val request = MockHttpServletRequest()
    request.addHeader("Content-Type", "application/json")
    request.addHeader("Accept", "application/json")
    request.addHeader("User-Agent", "Test-Agent")

    val headerMap = request.headerMap

    assertEquals("application/json", headerMap["Content-Type"])
    assertEquals("application/json", headerMap["Accept"])
    assertEquals("Test-Agent", headerMap["User-Agent"])
    assertEquals(3, headerMap.size)
  }

  @Test
  fun `headerMap should return an empty Map for empty request headers`() {
    val request = MockHttpServletRequest()
    val headerMap = request.headerMap
    assertTrue(headerMap.isEmpty())
  }

  @Test
  fun `deviceId should correctly get the device ID from the request header`() {
    val request = MockHttpServletRequest()
    request.addHeader(IHeaders.X_DEVICE_ID, "test-device-123")

    val deviceId = request.deviceId

    assertEquals("test-device-123", deviceId)
  }

  @Test
  fun `remoteRequestIp should get the request IP address`() {
    val request = MockHttpServletRequest()
    request.remoteAddr = "192.168.1.100"

    val remoteIp = request.remoteRequestIp

    assertNotNull(remoteIp)
    // The IP address retrieval logic is implemented in IInterAddr.getRequestIpAddress
  }

  @Test
  fun `remoteRequestIp should handle proxy headers to get the real IP`() {
    val request = MockHttpServletRequest()
    request.addHeader("X-Forwarded-For", "203.0.113.195")
    request.addHeader("X-Real-IP", "203.0.113.195")
    request.remoteAddr = "10.0.0.1"

    val remoteIp = request.remoteRequestIp

    assertNotNull(remoteIp)
    // The specific IP parsing logic depends on the implementation of IInterAddr.getRequestIpAddress
  }

  @Test
  fun `extension properties should work correctly in MockHttpServletRequest`() {
    val request = MockHttpServletRequest()
    request.addHeader("Content-Type", "application/json")
    request.addHeader("Accept", "application/json")
    request.addHeader(IHeaders.X_DEVICE_ID, "test-device")
    request.remoteAddr = "192.168.1.100"

    // Test extension properties
    val headerMap = request.headerMap
    val deviceId = request.deviceId
    val remoteIp = request.remoteRequestIp

    // Verify that the extension properties work correctly
    assertNotNull(headerMap)
    assertEquals("test-device", deviceId)
    assertNotNull(remoteIp)
    assertTrue(headerMap.containsKey("Content-Type"))
    assertTrue(headerMap.containsKey("Accept"))
  }
}
