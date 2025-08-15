package itest.integrate.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import jakarta.annotation.Resource
import org.hamcrest.Matchers
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Spring Boot Web集成测试
 *
 * 测试REST Controller中的JSON序列化，验证Web环境下的时间戳序列化行为
 */
@WebMvcTest(SpringBootWebIntegrationTest.TestController::class)
@ContextConfiguration(classes = [SpringBootWebIntegrationTest.WebTestConfiguration::class])
class SpringBootWebIntegrationTest {

  @Resource private lateinit var mockMvc: MockMvc

  @Resource @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var objectMapper: ObjectMapper

  @Nested
  inner class RestControllerSerializationTests {

    @Test
    fun get_endpoint_should_serialize_time_objects_as_timestamps() {
      mockMvc
        .perform(get("/api/time-data"))
        .andExpect(status().isOk)
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.instant").isNumber)
        .andExpect(jsonPath("$.localDateTime").isNumber)
        .andExpect(jsonPath("$.zonedDateTime").isNumber)
    }

    @Test
    fun post_endpoint_should_deserialize_timestamps_correctly() {
      val now = Instant.now()
      val localDateTime = LocalDateTime.now()
      val zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)

      val requestBody =
        mapOf(
          "instant" to now.toEpochMilli(),
          "localDateTime" to localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli(),
          "zonedDateTime" to zonedDateTime.toInstant().toEpochMilli(),
        )

      val jsonRequest = objectMapper.writeValueAsString(requestBody)

      mockMvc
        .perform(post("/api/time-data").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andExpect(status().isOk)
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.received").value(true))
    }
  }

  @Nested
  inner class RequestResponseBodyTests {

    @Test
    fun request_body_with_timestamps_should_be_deserialized_correctly() {
      val timestampData = TimestampData(instant = Instant.now(), localDateTime = LocalDateTime.now(), zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC))

      // 手动序列化为时间戳格式
      val requestJson =
        """
        {
          "instant": ${timestampData.instant.toEpochMilli()},
          "localDateTime": ${timestampData.localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()},
          "zonedDateTime": ${timestampData.zonedDateTime.toInstant().toEpochMilli()}
        }
      """
          .trimIndent()

      mockMvc
        .perform(post("/api/timestamp-data").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isOk)
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.processed").value(true))
    }

    @Test
    fun response_body_should_serialize_timestamps_correctly() {
      mockMvc
        .perform(get("/api/timestamp-data"))
        .andExpect(status().isOk)
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.instant").isNumber)
        .andExpect(jsonPath("$.localDateTime").isNumber)
        .andExpect(jsonPath("$.zonedDateTime").isNumber)
        // 验证时间戳是合理的（大于某个基准时间）
        .andExpect(jsonPath("$.instant").value(Matchers.greaterThan(1600000000000L)))
        .andExpect(jsonPath("$.localDateTime").value(Matchers.greaterThan(1600000000000L)))
        .andExpect(jsonPath("$.zonedDateTime").value(Matchers.greaterThan(1600000000000L)))
    }
  }

  @Nested
  inner class TimezoneHandlingTests {

    @Test
    fun different_timezone_inputs_should_produce_consistent_utc_timestamps() {
      // 测试不同时区的相同时间点应该产生相同的UTC时间戳
      val utcTime = ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC)
      val beijingTime = utcTime.withZoneSameInstant(ZoneOffset.ofHours(8))
      val newYorkTime = utcTime.withZoneSameInstant(ZoneOffset.ofHours(-5))

      val utcRequest = """{"zonedDateTime": ${utcTime.toInstant().toEpochMilli()}}"""
      val beijingRequest = """{"zonedDateTime": ${beijingTime.toInstant().toEpochMilli()}}"""
      val newYorkRequest = """{"zonedDateTime": ${newYorkTime.toInstant().toEpochMilli()}}"""

      // 所有请求应该返回相同的时间戳
      val expectedTimestamp = utcTime.toInstant().toEpochMilli()

      mockMvc
        .perform(post("/api/timezone-test").contentType(MediaType.APPLICATION_JSON).content(utcRequest))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.receivedTimestamp").value(expectedTimestamp))

      mockMvc
        .perform(post("/api/timezone-test").contentType(MediaType.APPLICATION_JSON).content(beijingRequest))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.receivedTimestamp").value(expectedTimestamp))

      mockMvc
        .perform(post("/api/timezone-test").contentType(MediaType.APPLICATION_JSON).content(newYorkRequest))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.receivedTimestamp").value(expectedTimestamp))
    }
  }

  /** 测试数据类 */
  data class TimestampData(val instant: Instant, val localDateTime: LocalDateTime, val zonedDateTime: ZonedDateTime)

  data class TimezoneTestRequest(val zonedDateTime: ZonedDateTime)

  data class TimezoneTestResponse(val receivedTimestamp: Long)

  /** 测试控制器 */
  @RestController
  class TestController {

    @GetMapping("/api/time-data")
    fun getTimeData(): Map<String, Any> {
      val now = Instant.now()
      return mapOf("instant" to now, "localDateTime" to LocalDateTime.now(), "zonedDateTime" to ZonedDateTime.now(ZoneOffset.UTC))
    }

    @PostMapping("/api/time-data")
    fun postTimeData(@RequestBody data: Map<String, Any>): Map<String, Boolean> {
      return mapOf("received" to true)
    }

    @GetMapping("/api/timestamp-data")
    fun getTimestampData(): TimestampData {
      return TimestampData(instant = Instant.now(), localDateTime = LocalDateTime.now(), zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC))
    }

    @PostMapping("/api/timestamp-data")
    fun postTimestampData(@RequestBody data: TimestampData): Map<String, Boolean> {
      return mapOf("processed" to true)
    }

    @PostMapping("/api/timezone-test")
    fun timezoneTest(@RequestBody request: TimezoneTestRequest): TimezoneTestResponse {
      return TimezoneTestResponse(receivedTimestamp = request.zonedDateTime.toInstant().toEpochMilli())
    }
  }

  @TestConfiguration
  @SpringBootApplication
  @Import(JacksonAutoConfiguration::class)
  class WebTestConfiguration {

    @Bean
    fun testController(): TestController {
      return TestController()
    }
  }
}
