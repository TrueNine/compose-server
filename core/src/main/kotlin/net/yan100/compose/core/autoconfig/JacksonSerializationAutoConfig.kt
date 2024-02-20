/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.autoconfig

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.yan100.compose.core.jackson.*
import net.yan100.compose.core.lang.AnyTyping
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

  /** kotlin module 配置 */
  private fun ktm(): KotlinModule {
    val kotlinKeyPairDeserializer = KPairDeserializer()

    val k = KotlinModule.Builder().build()
    k.addDeserializer(Pair::class.java, kotlinKeyPairDeserializer)

    return k
  }

  @Lazy
  @Bean
  fun jacksonF(): Jackson2ObjectMapperBuilderCustomizer {
    val km = ktm()

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

    // 将 byteArray 处理为 int 数组
    val byteArraySerializer = ByteArraySerializer()
    val byteArrayDeserializer = ByteArrayDeserializer()
    module.addSerializer(ByteArray::class.java, byteArraySerializer)
    module.addDeserializer(ByteArray::class.java, byteArrayDeserializer)

    // 处理枚举类型
    val anyTypingDeserializer = AnyTypingDeserializer()
    module.addDeserializer(AnyTyping::class.java, anyTypingDeserializer)

    log.debug("配置jackson序列化规则")

    return Jackson2ObjectMapperBuilderCustomizer { b ->
      b.modules(module, km)
      b.timeZone(TimeZone.getTimeZone(zoneOffset))
      b.locale(Locale.CHINA)
      b.simpleDateFormat(DTimer.DATETIME)
      b.defaultViewInclusion(true)
      b.featuresToDisable(
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
      )

      b.serializationInclusion(JsonInclude.Include.NON_NULL)
      b.serializationInclusion(JsonInclude.Include.NON_EMPTY)
      b.serializationInclusion(JsonInclude.Include.NON_ABSENT)
    }
  }
}
