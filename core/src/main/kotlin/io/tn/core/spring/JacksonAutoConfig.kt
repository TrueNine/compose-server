package io.tn.core.spring

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import io.tn.core.lang.DTimer
import io.tn.core.lang.KtLogBridge
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * jackson json 序列化策略配置
 *
 * @author TrueNine
 * @since 2023-02-23
 */
@Configuration
open class JacksonAutoConfig {


  @Bean
  @Lazy
  open fun jacksonF(): Jackson2ObjectMapperBuilderCustomizer {
    val module = JavaTimeModule()
    val ldts = LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DTimer.DATETIME))
    val ldtd = LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DTimer.DATETIME))
    val lts = LocalTimeSerializer(DateTimeFormatter.ofPattern(DTimer.TIME))
    val ltd = LocalTimeDeserializer(DateTimeFormatter.ofPattern(DTimer.TIME))
    val lds = LocalDateSerializer(DateTimeFormatter.ofPattern(DTimer.DATE))
    val ldd = LocalDateDeserializer(DateTimeFormatter.ofPattern(DTimer.DATE))

    module.addSerializer(LocalDateTime::class.java, ldts)
    module.addDeserializer(LocalDateTime::class.java, ldtd)
    module.addSerializer(LocalTime::class.java, lts)
    module.addDeserializer(LocalTime::class.java, ltd)
    module.addSerializer(LocalDate::class.java, lds)
    module.addDeserializer(LocalDate::class.java, ldd)
    log.info("配置jackson序列化规则")
    return Jackson2ObjectMapperBuilderCustomizer { b ->
      b.modules(module)
      b.timeZone("GMT+8")
      b.locale(Locale.CHINA)
      b.simpleDateFormat(DTimer.DATETIME)
      b.defaultViewInclusion(true)
      b.featuresToDisable(
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
      )
      b.serializationInclusion(JsonInclude.Include.NON_NULL)
      b.serializationInclusion(JsonInclude.Include.NON_ABSENT)
    }
  }

  companion object {
    @JvmStatic
    private val log = KtLogBridge.getLog(JacksonAutoConfig::class.java)
  }
}
