package itest.integrate.depend.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.truenine.composeserver.depend.jackson.autoconfig.*
import jakarta.annotation.Resource
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.ObjectMapper
import kotlin.test.*

/**
 * Jackson custom configuration integration tests.
 *
 * Tests customizing Jackson behavior via configuration properties.
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
      // Verify custom configuration values
      assertFalse(jacksonProperties.enableTimestampSerialization, "Timestamp serialization should be disabled")
      assertEquals(TimestampUnit.SECONDS, jacksonProperties.timestampUnit, "Second-based timestamps should be used")
      assertEquals(JsonInclude.Include.ALWAYS, jacksonProperties.serializationInclusion, "Properties should always be included")
      assertTrue(jacksonProperties.failOnUnknownProperties, "Should fail when encountering unknown properties")
      assertFalse(jacksonProperties.writeDatesAsTimestamps, "Dates should not be written as timestamps")
    }
  }

  @Nested
  inner class CustomObjectMapperConfigurationTests {
    @Test
    fun default_mapper_should_respect_custom_unknown_properties_configuration() {
      val config = defaultObjectMapper.deserializationConfig()

      // When failOnUnknownProperties=true, it should fail when encountering unknown properties
      assertTrue(config.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES), "Custom configuration should fail when encountering unknown properties")
    }
  }

  @Import(JacksonAutoConfiguration::class)
  @EnableConfigurationProperties(JacksonProperties::class)
  @ComponentScan("io.github.truenine.composeserver.depend.jackson")
  class TestConfiguration
}
