package io.github.truenine.composeserver.depend.jackson.autoconfig

import com.fasterxml.jackson.annotation.JsonInclude
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * JacksonProperties配置类测试
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class JacksonPropertiesTest {

  @Nested
  inner class DefaultValues {

    @Test
    fun default_properties_should_have_correct_values() {
      val properties = JacksonProperties()

      assertTrue(properties.enableTimestampSerialization)
      assertEquals(TimestampUnit.MILLISECONDS, properties.timestampUnit)
      assertEquals(JsonInclude.Include.NON_NULL, properties.serializationInclusion)
      assertFalse(properties.failOnUnknownProperties)
      assertTrue(properties.writeDatesAsTimestamps)
    }
  }

  @Nested
  inner class CustomValues {

    @Test
    fun custom_properties_should_override_defaults() {
      val properties =
        JacksonProperties(
          enableTimestampSerialization = false,
          timestampUnit = TimestampUnit.SECONDS,
          serializationInclusion = JsonInclude.Include.ALWAYS,
          failOnUnknownProperties = true,
          writeDatesAsTimestamps = false,
        )

      assertFalse(properties.enableTimestampSerialization)
      assertEquals(TimestampUnit.SECONDS, properties.timestampUnit)
      assertEquals(JsonInclude.Include.ALWAYS, properties.serializationInclusion)
      assertTrue(properties.failOnUnknownProperties)
      assertFalse(properties.writeDatesAsTimestamps)
    }
  }

  @Nested
  inner class TimestampUnitEnum {

    @Test
    fun timestamp_unit_enum_should_have_correct_values() {
      assertEquals(2, TimestampUnit.entries.size)
      assertEquals("MILLISECONDS", TimestampUnit.MILLISECONDS.name)
      assertEquals("SECONDS", TimestampUnit.SECONDS.name)
    }
  }
}
