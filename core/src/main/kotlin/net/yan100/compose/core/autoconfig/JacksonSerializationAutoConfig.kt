package net.yan100.compose.core.autoconfig

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import net.yan100.compose.core.jackson.*
import net.yan100.compose.core.lang.DTimer
import net.yan100.compose.core.lang.slf4j
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*

/**
 * jackson json 序列化策略配置
 *
 * @author TrueNine
 * @since 2023-02-23
 */
@Configuration
class JacksonSerializationAutoConfig {
  private val log = slf4j(JacksonSerializationAutoConfig::class)

  @Bean
  @Lazy
  fun jacksonF(): Jackson2ObjectMapperBuilderCustomizer {
    val module = JavaTimeModule()
    val zoneOffset = ZoneOffset.ofHours(8)

    val ldts = LocalDateTimeSerializer(zoneOffset)
    val ldtd = LocalDateTimeDeserializer(zoneOffset)

    val lts = LocalTimeSerializer(zoneOffset)
    val ltd = LocalTimeDeserializer(zoneOffset)
    val lds = LocalDateSerializer(zoneOffset)
    val ldd = LocalDateDeserializer(zoneOffset)

    module.addSerializer(LocalDateTime::class.java, ldts)
    module.addDeserializer(LocalDateTime::class.java, ldtd)
    module.addSerializer(LocalTime::class.java, lts)
    module.addDeserializer(LocalTime::class.java, ltd)
    module.addSerializer(LocalDate::class.java, lds)
    module.addDeserializer(LocalDate::class.java, ldd)

    val kotlinKeyPairDeserializer = KPairDeserializer()
    module.addDeserializer(Pair::class.java, kotlinKeyPairDeserializer)

    val byteArrayDeserializer = ByteArrayDeserializer()
    module.addDeserializer(ByteArray::class.java, byteArrayDeserializer)

    log.debug("配置jackson序列化规则")

    return Jackson2ObjectMapperBuilderCustomizer { b ->
      b.modules(module)
      b.timeZone(TimeZone.getTimeZone(zoneOffset))
      b.locale(Locale.CHINA)
      b.simpleDateFormat(DTimer.DATETIME)
      b.defaultViewInclusion(true)
      b.featuresToDisable(
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS
      )

      b.serializationInclusion(JsonInclude.Include.NON_NULL)
      b.serializationInclusion(JsonInclude.Include.NON_ABSENT)
    }
  }

}
