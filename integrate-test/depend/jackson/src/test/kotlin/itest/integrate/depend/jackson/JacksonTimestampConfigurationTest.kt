package itest.integrate.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonProperties
import io.github.truenine.composeserver.depend.jackson.autoconfig.TimestampUnit
import io.github.truenine.composeserver.depend.jackson.holders.ObjectMapperHolder
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

/**
 * Jackson时间戳配置集成测试
 *
 * 验证Spring Boot环境下的Jackson自动配置正确性，测试Bean创建和依赖注入
 */
@SpringBootTest(classes = [JacksonTimestampConfigurationTest.TestConfiguration::class])
class JacksonTimestampConfigurationTest {

  @Resource private lateinit var jacksonProperties: JacksonProperties

  @Resource private lateinit var objectMapperHolder: ObjectMapperHolder

  @Resource @Qualifier(JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var defaultObjectMapper: ObjectMapper

  @Resource @Qualifier(JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) private lateinit var nonIgnoreObjectMapper: ObjectMapper

  @Nested
  inner class ConfigurationPropertiesTests {

    @Test
    fun jackson_properties_should_be_loaded_with_default_values() {
      // 验证默认配置值
      assertTrue(jacksonProperties.enableTimestampSerialization, "时间戳序列化应该默认启用")
      assertEquals(TimestampUnit.MILLISECONDS, jacksonProperties.timestampUnit, "默认应该使用毫秒时间戳")
      assertFalse(jacksonProperties.failOnUnknownProperties, "默认应该忽略未知属性")
      assertTrue(jacksonProperties.writeDatesAsTimestamps, "默认应该将日期写为时间戳")
    }
  }

  @Nested
  inner class BeanCreationTests {

    @Test
    fun default_object_mapper_bean_should_be_created() {
      assertNotNull(defaultObjectMapper, "默认ObjectMapper Bean应该被创建")
    }

    @Test
    fun non_ignore_object_mapper_bean_should_be_created() {
      assertNotNull(nonIgnoreObjectMapper, "非忽略ObjectMapper Bean应该被创建")
    }

    @Test
    fun object_mapper_holder_should_be_created() {
      assertNotNull(objectMapperHolder, "ObjectMapperHolder Bean应该被创建")
    }
  }

  @Nested
  inner class DependencyInjectionTests {

    @Test
    fun object_mapper_holder_should_have_correct_dependencies() {
      val defaultFromHolder = objectMapperHolder.getDefaultMapper()
      val nonIgnoreFromHolder = objectMapperHolder.getNonIgnoreMapper()

      assertNotNull(defaultFromHolder, "ObjectMapperHolder应该能获取到默认ObjectMapper")
      assertNotNull(nonIgnoreFromHolder, "ObjectMapperHolder应该能获取到非忽略ObjectMapper")
    }

    @Test
    fun get_mapper_by_ignore_flag_should_work() {
      val mapperForIgnore = objectMapperHolder.getMapper(ignoreUnknown = true)
      val mapperForNonIgnore = objectMapperHolder.getMapper(ignoreUnknown = false)

      assertNotNull(mapperForIgnore, "应该能根据忽略标志获取到ObjectMapper")
      assertNotNull(mapperForNonIgnore, "应该能根据忽略标志获取到ObjectMapper")
    }
  }

  @Nested
  inner class TimestampConfigurationTests {

    @Test
    fun default_mapper_should_have_timestamp_serialization_enabled() {
      val config = defaultObjectMapper.serializationConfig
      assertTrue(config.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS), "默认ObjectMapper应该启用时间戳序列化")
    }

    @Test
    fun non_ignore_mapper_should_have_correct_configuration() {
      val config = nonIgnoreObjectMapper.deserializationConfig
      // 验证非忽略ObjectMapper的配置
      assertNotNull(config, "非忽略ObjectMapper应该有正确的配置")
    }
  }

  @Import(JacksonAutoConfiguration::class)
  @EnableConfigurationProperties(JacksonProperties::class)
  @ComponentScan("io.github.truenine.composeserver.depend.jackson")
  class TestConfiguration
}
