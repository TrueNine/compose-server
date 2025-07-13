package io.github.truenine.composeserver.security.sensitive

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.annotations.SensitiveResponse
import io.github.truenine.composeserver.domain.ISensitivity
import io.github.truenine.composeserver.security.autoconfig.SensitiveResultResponseBodyAdvice
import io.github.truenine.composeserver.security.controller.SensitiveController
import io.github.truenine.composeserver.testtoolkit.log
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Test sensitive data processing functionality */
class SensitiveTest {

  private lateinit var mockMvc: MockMvc
  private val objectMapper = ObjectMapper()

  @BeforeEach
  fun setup() {
    val controller = SensitiveController()
    val sensitive = SensitiveResultResponseBodyAdvice()
    mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(sensitive).build()
  }

  @Test
  fun `test sensitive response processing`() {
    mockMvc.get("/test/sensitive/get").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
    }
  }

  @Test
  fun `test sensitive data is properly desensitized`() {
    log.info("测试敏感数据是否正确脱敏")

    val result = mockMvc.get("/test/sensitive/get").andReturn()
    val responseContent = result.response.contentAsString
    log.info("响应内容: {}", responseContent)

    // 解析响应为 Pr 对象
    val responseMap = objectMapper.readValue(responseContent, Map::class.java)
    val dataList = responseMap["d"] as List<*>

    // 验证数据列表不为空
    assertTrue(dataList.isNotEmpty(), "数据列表不应该为空")

    // 验证每个响应对象的 a 字段都被脱敏为 233
    dataList.forEach { item ->
      val itemMap = item as Map<*, *>
      val aValue = itemMap["a"]
      assertEquals(233, aValue, "敏感数据应该被脱敏为 233")
    }

    log.info("敏感数据脱敏测试通过")
  }

  @Test
  fun `test ISensitivity interface behavior`() {
    log.info("测试 ISensitivity 接口行为")

    // 创建测试对象
    val testObj = SensitiveController.Resp(42)
    val originalValue = testObj.a

    // 调用脱敏方法
    testObj.changeWithSensitiveData()
    val sensitizedValue = testObj.a

    // 验证数据被正确脱敏
    assertNotEquals(originalValue, sensitizedValue, "脱敏后的值应该与原值不同")
    assertEquals(233, sensitizedValue, "脱敏后的值应该为 233")

    log.info("ISensitivity 接口行为测试通过")
  }

  @Test
  fun `test SensitiveResultResponseBodyAdvice supports method`() {
    log.info("测试 SensitiveResultResponseBodyAdvice 的 supports 方法")

    val advice = SensitiveResultResponseBodyAdvice()

    // 模拟带有 @SensitiveResponse 注解的方法
    val method = SensitiveController::class.java.getDeclaredMethod("test get a")
    val methodParameter = org.springframework.core.MethodParameter(method, -1)

    // 验证 supports 方法返回 true
    val supports = advice.supports(methodParameter, org.springframework.http.converter.json.MappingJackson2HttpMessageConverter::class.java)
    assertTrue(supports, "带有 @SensitiveResponse 注解的方法应该被支持")

    log.info("SensitiveResultResponseBodyAdvice supports 方法测试通过")
  }
}
