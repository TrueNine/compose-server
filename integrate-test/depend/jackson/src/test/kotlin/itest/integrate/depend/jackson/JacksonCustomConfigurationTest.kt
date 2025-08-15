package itest.integrate.depend.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonProperties
import io.github.truenine.composeserver.depend.jackson.autoconfig.TimestampUnit
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

/**
 * Jackson自定义配置集成测试
 *
 * 测试通过配置属性自定义Jackson行为
 */
@SpringBootTest(classes = [JacksonCustomConfigurationTest.TestConfiguration::class])
@TestPropertySource(
  properties =
    [
      "compose.depend.jackson.enable-timestamp-serialization=false",
      "compose.depend.jackson.timestamp-unit=SECONDS",
      "compose.depend.jackson.serialization-inclusion=ALWAYS",
      "compose.depend.jackson.fail-on-unknown-properties=true",
      "compose.depend.jackson.write-dates-as-timestamps=false",
    ]
)
class JacksonCustomConfigurationTest {

  @Resource private lateinit var jacksonProperties: JacksonProperties

  @Resource @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var defaultObjectMapper: ObjectMapper

  @Nested
  inner class CustomConfigurationPropertiesTests {

    @Test
    fun jackson_properties_should_be_loaded_with_custom_values() {
      // 验证自定义配置值
      assertFalse(jacksonProperties.enableTimestampSerialization, "时间戳序列化应该被禁用")
      assertEquals(TimestampUnit.SECONDS, jacksonProperties.timestampUnit, "应该使用秒时间戳")
      assertEquals(JsonInclude.Include.ALWAYS, jacksonProperties.serializationInclusion, "应该总是包含属性")
      assertTrue(jacksonProperties.failOnUnknownProperties, "应该在遇到未知属性时失败")
      assertFalse(jacksonProperties.writeDatesAsTimestamps, "不应该将日期写为时间戳")
    }
  }

  @Nested
  inner class CustomObjectMapperConfigurationTests {

    @Test
    fun default_mapper_should_respect_custom_timestamp_configuration() {
      val config = defaultObjectMapper.serializationConfig

      // 当enableTimestampSerialization=false且writeDatesAsTimestamps=false时，
      // 应该禁用时间戳序列化
      assertFalse(config.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS), "自定义配置应该禁用时间戳序列化")
    }

    @Test
    fun default_mapper_should_respect_custom_unknown_properties_configuration() {
      val config = defaultObjectMapper.deserializationConfig

      // 当failOnUnknownProperties=true时，应该在遇到未知属性时失败
      assertTrue(config.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES), "自定义配置应该在遇到未知属性时失败")
    }
  }

  @Import(JacksonAutoConfiguration::class)
  @EnableConfigurationProperties(JacksonProperties::class)
  @ComponentScan("io.github.truenine.composeserver.depend.jackson")
  class TestConfiguration
}
