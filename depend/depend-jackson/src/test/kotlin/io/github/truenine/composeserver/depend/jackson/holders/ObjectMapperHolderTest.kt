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
 * ObjectMapperHolder配置持有者测试
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

      assertNotNull(mapper, "默认mapper不应为null")
      assertSame(defaultMapper, mapper, "应返回相同的默认mapper实例")
    }
  }

  @Nested
  inner class GetNonIgnoreMapper {

    @Test
    fun should_return_non_ignore_mapper() {
      val mapper = objectMapperHolder.getNonIgnoreMapper()

      assertNotNull(mapper, "非忽略mapper不应为null")
      assertSame(nonIgnoreMapper, mapper, "应返回相同的非忽略mapper实例")
    }
  }

  @Nested
  inner class GetMapper {

    @Test
    fun should_return_default_mapper_when_ignore_unknown_is_true() {
      val mapper = objectMapperHolder.getMapper(ignoreUnknown = true)

      assertNotNull(mapper, "mapper不应为null")
      assertSame(defaultMapper, mapper, "ignoreUnknown=true时应返回默认mapper")
    }

    @Test
    fun should_return_non_ignore_mapper_when_ignore_unknown_is_false() {
      val mapper = objectMapperHolder.getMapper(ignoreUnknown = false)

      assertNotNull(mapper, "mapper不应为null")
      assertSame(nonIgnoreMapper, mapper, "ignoreUnknown=false时应返回非忽略mapper")
    }

    @Test
    fun should_return_default_mapper_when_no_parameter() {
      val mapper = objectMapperHolder.getMapper()

      assertNotNull(mapper, "mapper不应为null")
      assertSame(defaultMapper, mapper, "无参数时应返回默认mapper")
    }
  }
}
