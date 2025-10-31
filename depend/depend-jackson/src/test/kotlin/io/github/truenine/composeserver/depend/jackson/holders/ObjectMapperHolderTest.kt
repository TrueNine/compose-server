package io.github.truenine.composeserver.depend.jackson.holders

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

/**
 * ObjectMapperHolder configuration holder tests
 *
 * @author TrueNine
 * @since 2025-01-16
 */
@SpringBootTest(classes = [JacksonAutoConfiguration::class, ObjectMapperHolder::class])
@TestPropertySource(properties = ["compose.jackson.enableTimestampSerialization=true", "compose.jackson.timestampUnit=MILLISECONDS"])
class ObjectMapperHolderTest {

  @jakarta.annotation.Resource private lateinit var objectMapperHolder: ObjectMapperHolder

  @jakarta.annotation.Resource(name = "defaultObjectMapper") private lateinit var defaultMapper: ObjectMapper

  @jakarta.annotation.Resource(name = "nonIgnoreObjectMapper") private lateinit var nonIgnoreMapper: ObjectMapper

  @Nested
  inner class GetDefaultMapper {

    @Test
    fun should_return_default_mapper() {
      val mapper = objectMapperHolder.getDefaultMapper()

      assertNotNull(mapper, "Default mapper should not be null")
      assertSame(defaultMapper, mapper, "Should return the same default mapper instance")
    }
  }

  @Nested
  inner class GetNonIgnoreMapper {

    @Test
    fun should_return_non_ignore_mapper() {
      val mapper = objectMapperHolder.getNonIgnoreMapper()

      assertNotNull(mapper, "Non-ignore mapper should not be null")
      assertSame(nonIgnoreMapper, mapper, "Should return the same non-ignore mapper instance")
    }
  }

  @Nested
  inner class GetMapper {

    @Test
    fun should_return_default_mapper_when_ignore_unknown_is_true() {
      val mapper = objectMapperHolder.getMapper(ignoreUnknown = true)

      assertNotNull(mapper, "Mapper should not be null")
      assertSame(defaultMapper, mapper, "Should return default mapper when ignoreUnknown=true")
    }

    @Test
    fun should_return_non_ignore_mapper_when_ignore_unknown_is_false() {
      val mapper = objectMapperHolder.getMapper(ignoreUnknown = false)

      assertNotNull(mapper, "Mapper should not be null")
      assertSame(nonIgnoreMapper, mapper, "Should return non-ignore mapper when ignoreUnknown=false")
    }

    @Test
    fun should_return_default_mapper_when_no_parameter() {
      val mapper = objectMapperHolder.getMapper()

      assertNotNull(mapper, "Mapper should not be null")
      assertSame(defaultMapper, mapper, "Should return default mapper when no parameter is provided")
    }
  }
}
