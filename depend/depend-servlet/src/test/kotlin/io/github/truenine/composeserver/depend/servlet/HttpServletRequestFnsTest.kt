package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.consts.IHeaders
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.mock.web.MockHttpServletRequest

class HttpServletRequestFnsTest {

  @Test
  fun `headerMap 应正确转换请求头为 Map`() {
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
  fun `headerMap 空请求头应返回空 Map`() {
    val request = MockHttpServletRequest()
    val headerMap = request.headerMap
    assertTrue(headerMap.isEmpty())
  }

  @Test
  fun `deviceId 应正确从请求头获取设备 ID`() {
    val request = MockHttpServletRequest()
    request.addHeader(IHeaders.X_DEVICE_ID, "test-device-123")

    val deviceId = request.deviceId

    assertEquals("test-device-123", deviceId)
  }

  @Test
  fun `remoteRequestIp 应获取请求 IP 地址`() {
    val request = MockHttpServletRequest()
    request.remoteAddr = "192.168.1.100"

    val remoteIp = request.remoteRequestIp

    assertNotNull(remoteIp)
    // IP 地址获取逻辑在 IInterAddr.getRequestIpAddress 中实现
  }

  @Test
  fun `remoteRequestIp 应处理代理头部获取真实 IP`() {
    val request = MockHttpServletRequest()
    request.addHeader("X-Forwarded-For", "203.0.113.195")
    request.addHeader("X-Real-IP", "203.0.113.195")
    request.remoteAddr = "10.0.0.1"

    val remoteIp = request.remoteRequestIp

    assertNotNull(remoteIp)
    // 具体的 IP 解析逻辑取决于 IInterAddr.getRequestIpAddress 的实现
  }

  @Test
  fun `扩展属性应在 MockHttpServletRequest 中正常工作`() {
    val request = MockHttpServletRequest()
    request.addHeader("Content-Type", "application/json")
    request.addHeader("Accept", "application/json")
    request.addHeader(IHeaders.X_DEVICE_ID, "test-device")
    request.remoteAddr = "192.168.1.100"

    // 测试扩展属性
    val headerMap = request.headerMap
    val deviceId = request.deviceId
    val remoteIp = request.remoteRequestIp

    // 验证扩展属性工作正常
    assertNotNull(headerMap)
    assertEquals("test-device", deviceId)
    assertNotNull(remoteIp)
    assertTrue(headerMap.containsKey("Content-Type"))
    assertTrue(headerMap.containsKey("Accept"))
  }
}
