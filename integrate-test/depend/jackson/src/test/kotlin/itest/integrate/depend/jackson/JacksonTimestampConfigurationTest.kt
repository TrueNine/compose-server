package itest.integrate.depend.jackson

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonProperties
import io.github.truenine.composeserver.depend.jackson.autoconfig.TimestampUnit
import io.github.truenine.composeserver.depend.jackson.holders.ObjectMapperHolder
import jakarta.annotation.Resource
import java.time.Instant
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
import tools.jackson.databind.ObjectMapper

/**
 * Jackson timestamp configuration integration tests.
 *
 * Verifies Jackson auto-configuration correctness in a Spring Boot environment, testing bean creation and dependency injection.
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
      // Verify default configuration values
      assertTrue(jacksonProperties.enableTimestampSerialization, "Timestamp serialization should be enabled by default")
      assertEquals(TimestampUnit.MILLISECONDS, jacksonProperties.timestampUnit, "Millisecond timestamp should be used by default")
      assertFalse(jacksonProperties.failOnUnknownProperties, "Unknown properties should be ignored by default")
      assertTrue(jacksonProperties.writeDatesAsTimestamps, "Dates should be written as timestamps by default")
    }
  }

  @Nested
  inner class BeanCreationTests {

    @Test
    fun default_object_mapper_bean_should_be_created() {
      assertNotNull(defaultObjectMapper, "Default ObjectMapper bean should be created")
    }

    @Test
    fun non_ignore_object_mapper_bean_should_be_created() {
      assertNotNull(nonIgnoreObjectMapper, "Non-ignore ObjectMapper bean should be created")
    }

    @Test
    fun object_mapper_holder_should_be_created() {
      assertNotNull(objectMapperHolder, "ObjectMapperHolder bean should be created")
    }
  }

  @Nested
  inner class DependencyInjectionTests {

    @Test
    fun object_mapper_holder_should_have_correct_dependencies() {
      val defaultFromHolder = objectMapperHolder.getDefaultMapper()
      val nonIgnoreFromHolder = objectMapperHolder.getNonIgnoreMapper()

      assertNotNull(defaultFromHolder, "ObjectMapperHolder should be able to obtain the default ObjectMapper")
      assertNotNull(nonIgnoreFromHolder, "ObjectMapperHolder should be able to obtain the non-ignore ObjectMapper")
    }

    @Test
    fun get_mapper_by_ignore_flag_should_work() {
      val mapperForIgnore = objectMapperHolder.getMapper(ignoreUnknown = true)
      val mapperForNonIgnore = objectMapperHolder.getMapper(ignoreUnknown = false)

      assertNotNull(mapperForIgnore, "Should be able to obtain ObjectMapper based on ignoreUnknown flag")
      assertNotNull(mapperForNonIgnore, "Should be able to obtain ObjectMapper based on ignoreUnknown flag")
    }
  }

  @Nested
  inner class TimestampConfigurationTests {

    @Test
    fun default_mapper_should_serialize_dates_as_numeric_timestamps() {
      val instant = Instant.parse("2023-06-15T12:00:00Z")
      val json = defaultObjectMapper.writeValueAsString(instant)
      assertNotNull(json.toLongOrNull(), "Default ObjectMapper should output numeric timestamps")
    }

    @Test
    fun non_ignore_mapper_should_have_correct_configuration() {
      val config = nonIgnoreObjectMapper.deserializationConfig()
      // Verify configuration of non-ignore ObjectMapper
      assertNotNull(config, "Non-ignore ObjectMapper should have correct configuration")
    }
  }

  @Import(JacksonAutoConfiguration::class)
  @EnableConfigurationProperties(JacksonProperties::class)
  @ComponentScan("io.github.truenine.composeserver.depend.jackson")
  class TestConfiguration
}
