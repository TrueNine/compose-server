package itest.integrate.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import jakarta.annotation.Resource
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.test.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 手动配置的 Web 集成测试
 *
 * 不依赖 Spring Boot 自动配置，手动配置所有组件
 */
@SpringBootTest(classes = [TestEntrance::class])
@AutoConfigureMockMvc
@Import(ManualWebConfigurationTest.TestController::class)
class ManualWebConfigurationTest {

  @Resource @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var objectMapper: ObjectMapper

  @Resource private lateinit var testController: TestController

  private lateinit var mockMvc: MockMvc

  @BeforeEach
  fun setup() {
    val messageConverter = MappingJackson2HttpMessageConverter(objectMapper)
    mockMvc = MockMvcBuilders.standaloneSetup(testController).setMessageConverters(messageConverter).build()
  }

  @Nested
  inner class RestControllerSerializationTests {

    @Test
    fun should_serialize_instant_as_timestamp_in_rest_response() {
      val result = mockMvc.perform(get("/api/time-data")).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn()

      val responseBody = result.response.contentAsString
      val timeData = objectMapper.readValue(responseBody, TimeData::class.java)

      assertNotNull(timeData.instant)
      assertNotNull(timeData.localDateTime)
    }

    @Test
    fun should_deserialize_timestamp_to_instant_in_rest_request() {
      val instant = Instant.parse("2023-06-15T12:00:00Z")
      val localDateTime = LocalDateTime.of(2023, 6, 15, 12, 0, 0)
      val timeData = TimeData(instant, localDateTime)

      val requestBody = objectMapper.writeValueAsString(timeData)

      mockMvc
        .perform(post("/api/time-data").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk)
        .andExpect(content().string("\"OK\""))
    }
  }

  @Nested
  inner class TimezoneHandlingTests {

    @Test
    fun should_handle_different_system_timezones_consistently() {
      val originalTimeZone = System.getProperty("user.timezone")
      try {
        // 测试不同时区
        val timeZones = listOf("UTC", "Asia/Shanghai", "America/New_York", "Europe/London")

        timeZones.forEach { timeZone ->
          System.setProperty("user.timezone", timeZone)
          TimeZone.setDefault(TimeZone.getTimeZone(timeZone))

          val result = mockMvc.perform(get("/api/time-data")).andExpect(status().isOk).andReturn()

          val responseBody = result.response.contentAsString
          val timeData = objectMapper.readValue(responseBody, TimeData::class.java)

          // 验证时间戳序列化在不同时区下保持一致
          assertNotNull(timeData.instant)
          assertNotNull(timeData.localDateTime)
        }
      } finally {
        // 恢复原始时区
        if (originalTimeZone != null) {
          System.setProperty("user.timezone", originalTimeZone)
          TimeZone.setDefault(TimeZone.getTimeZone(originalTimeZone))
        }
      }
    }
  }

  /** 测试数据类 */
  data class TimeData(val instant: Instant, val localDateTime: LocalDateTime)

  /** 测试控制器 */
  @RestController
  class TestController {

    @GetMapping("/api/time-data")
    fun getTimeData(): TimeData {
      val now = Instant.now()
      return TimeData(instant = now, localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault()))
    }

    @PostMapping("/api/time-data")
    fun postTimeData(@RequestBody timeData: TimeData): String {
      return "OK"
    }
  }

  @Configuration @Import(TestController::class) class TestConfiguration
}
