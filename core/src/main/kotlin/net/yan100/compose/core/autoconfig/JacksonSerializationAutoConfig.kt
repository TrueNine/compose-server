/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.autoconfig

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.*
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*
import net.yan100.compose.core.jackson.*
import net.yan100.compose.core.log.slf4j
import net.yan100.compose.core.typing.AnyTyping
import net.yan100.compose.core.util.DTimer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

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

    val k = KotlinModule.Builder()
      .build()
    k.addDeserializer(Pair::class.java, kotlinKeyPairDeserializer)

    return k
  }

  @Bean
  @Primary
  fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
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
      b.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)

      b.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

      b.serializationInclusion(JsonInclude.Include.NON_NULL)
      b.serializationInclusion(JsonInclude.Include.NON_EMPTY)

      b.serializationInclusion(JsonInclude.Include.NON_ABSENT)
    }
  }

  class IgnoreJsonIgnoreAnnotationIntrospector : JacksonAnnotationIntrospector() {
    override fun findPropertyIgnoralByName(config: MapperConfig<*>?, a: Annotated?): JsonIgnoreProperties.Value = JsonIgnoreProperties.Value.empty()
    override fun _isIgnorable(a: Annotated?): Boolean = false
    override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean = false
    override fun isIgnorableType(ac: AnnotatedClass?): Boolean = false
  }

  companion object {
    const val SPRING_DEFAULT_OBJECT_MAPPER_BEAN_NAME = "jacksonObjectMapper"
    const val NON_IGNORE_OBJECT_MAPPER_BEAN_NAME = "nonJsonIgnoreObjectMapper"
    const val DEFAULT_OBJECT_MAPPER_BEAN_NAME = "defaultObjectMapper"
  }

  @Primary
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @ConditionalOnMissingBean(ObjectMapper::class, name = [SPRING_DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  @Bean(name = [DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  fun defaultObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
    log.debug("注册默认的 objectMapper")
    return builder.createXmlMapper(false).build()
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnBean(ObjectMapper::class)
  @Bean(name = [NON_IGNORE_OBJECT_MAPPER_BEAN_NAME])
  fun nonDeserializerObjectMapper(
    mapper: ObjectMapper
  ): ObjectMapper {
    log.debug("注册非忽略注解的 ObjectMapper, defaultMapper = {}", mapper)
    return mapper.copy().let {
      val re = IgnoreJsonIgnoreAnnotationIntrospector()
      val intros =
        (it.deserializationConfig.annotationIntrospector.allIntrospectors() + it.serializationConfig.annotationIntrospector.allIntrospectors())
          .filterNot { i -> i is JacksonAnnotationIntrospector }
          .distinct().toMutableList()
      intros += re
      var pair: AnnotationIntrospectorPair? = null
      if (intros.size >= 2) {
        for (i in 1 until intros.size) {
          val p = AnnotationIntrospectorPair(intros[i], intros[i - 1])
          intros[i] = p
        }
        pair = intros.last() as AnnotationIntrospectorPair?
      }
      it.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .registerModules(KotlinModule.Builder().build())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        .activateDefaultTyping(it.polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.WRAPPER_OBJECT)
      if (null != pair) it.setAnnotationIntrospector(pair)
      it
    }
  }
}
