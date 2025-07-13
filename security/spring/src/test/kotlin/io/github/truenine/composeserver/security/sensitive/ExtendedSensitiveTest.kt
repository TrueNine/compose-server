package io.github.truenine.composeserver.security.sensitive

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.security.autoconfig.SensitiveResultResponseBodyAdvice
import io.github.truenine.composeserver.security.controller.ExtendedSensitiveController
import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/** 扩展的敏感数据处理功能测试 */
class ExtendedSensitiveTest {

  private lateinit var mockMvc: MockMvc
  private val objectMapper = ObjectMapper()

  @BeforeEach
  fun setup() {
    val controller = ExtendedSensitiveController()
    val sensitive = SensitiveResultResponseBodyAdvice()
    mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(sensitive).build()
  }

  @Test
  fun `test user info desensitization`() {
    log.info("测试用户信息脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/user")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("用户信息响应: {}", responseContent)

    val responseMap = objectMapper.readValue(responseContent, Map::class.java)
    val dataList = responseMap["d"] as List<*>

    assertTrue(dataList.isNotEmpty(), "用户数据列表不应该为空")

    dataList.forEach { item ->
      val userMap = item as Map<*, *>
      val phone = userMap["phone"] as String
      val email = userMap["email"] as String
      val idCard = userMap["idCard"] as String
      val address = userMap["address"] as String

      // 验证敏感数据被正确脱敏
      assertTrue(phone.contains("****"), "手机号应该被脱敏")
      assertTrue(email.contains("****"), "邮箱应该被脱敏")
      assertTrue(idCard.contains("****"), "身份证号应该被脱敏")
      assertTrue(address.contains("****"), "地址应该被脱敏")
    }

    log.info("用户信息脱敏测试通过")
  }

  @Test
  fun `test simple data desensitization`() {
    log.info("测试简单数据脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/simple")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("简单数据响应: {}", responseContent)

    val responseMap = objectMapper.readValue(responseContent, Map::class.java)
    val value = responseMap["value"] as String

    assertEquals("****", value, "简单敏感数据应该被脱敏为 ****")

    log.info("简单数据脱敏测试通过")
  }

  @Test
  fun `test nested data desensitization`() {
    log.info("测试嵌套数据脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/nested")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("嵌套数据响应: {}", responseContent)

    val responseMap = objectMapper.readValue(responseContent, Map::class.java)
    val publicInfo = responseMap["publicInfo"] as String
    val sensitiveInfo = responseMap["sensitiveInfo"] as Map<*, *>
    val sensitiveValue = sensitiveInfo["value"] as String

    assertEquals("public info", publicInfo, "公开信息不应该被脱敏")
    assertEquals("****", sensitiveValue, "嵌套的敏感信息应该被脱敏")

    log.info("嵌套数据脱敏测试通过")
  }

  @Test
  fun `test collection desensitization`() {
    log.info("测试集合数据脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/collection")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("集合数据响应: {}", responseContent)

    val dataList = objectMapper.readValue(responseContent, List::class.java)

    assertTrue(dataList.isNotEmpty(), "集合数据不应该为空")

    dataList.forEach { item ->
      val itemMap = item as Map<*, *>
      val value = itemMap["value"] as String
      assertEquals("****", value, "集合中的敏感数据应该被脱敏")
    }

    log.info("集合数据脱敏测试通过")
  }

  @Test
  fun `test array desensitization`() {
    log.info("测试数组数据脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/array")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("数组数据响应: {}", responseContent)

    val dataList = objectMapper.readValue(responseContent, List::class.java)

    assertTrue(dataList.isNotEmpty(), "数组数据不应该为空")

    dataList.forEach { item ->
      val itemMap = item as Map<*, *>
      val value = itemMap["value"] as String
      assertEquals("****", value, "数组中的敏感数据应该被脱敏")
    }

    log.info("数组数据脱敏测试通过")
  }

  @Test
  fun `test map desensitization`() {
    log.info("测试Map数据脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/map")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("Map数据响应: {}", responseContent)

    val responseMap = objectMapper.readValue(responseContent, Map::class.java)

    responseMap.values.forEach { item ->
      val itemMap = item as Map<*, *>
      val value = itemMap["value"] as String
      assertEquals("****", value, "Map中的敏感数据应该被脱敏")
    }

    log.info("Map数据脱敏测试通过")
  }

  @Test
  fun `test no annotation should not desensitize`() {
    log.info("测试没有注解的方法不应该脱敏")

    val result =
      mockMvc
        .get("/test/sensitive/extended/no-annotation")
        .andExpect {
          status { isOk() }
          content { contentType(MediaType.APPLICATION_JSON) }
        }
        .andReturn()

    val responseContent = result.response.contentAsString
    log.info("无注解方法响应: {}", responseContent)

    val responseMap = objectMapper.readValue(responseContent, Map::class.java)
    val value = responseMap["value"] as String

    assertEquals("should not be desensitized", value, "没有注解的方法不应该脱敏数据")

    log.info("无注解方法测试通过")
  }

  @Test
  fun `test ISensitivity interface direct usage`() {
    log.info("测试 ISensitivity 接口直接使用")

    // 测试 UserInfo
    val userInfo = ExtendedSensitiveController.UserInfo()
    val originalPhone = userInfo.phone
    userInfo.changeWithSensitiveData()
    assertNotEquals(originalPhone, userInfo.phone, "用户信息脱敏后应该不同")

    // 测试 SimpleData
    val simpleData = ExtendedSensitiveController.SimpleData("test")
    val originalValue = simpleData.value
    simpleData.changeWithSensitiveData()
    assertNotEquals(originalValue, simpleData.value, "简单数据脱敏后应该不同")
    assertEquals("****", simpleData.value, "简单数据应该被脱敏为 ****")

    log.info("ISensitivity 接口直接使用测试通过")
  }
}
