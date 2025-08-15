package io.github.truenine.composeserver.depend.jackson.autoconfig

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.truenine.composeserver.DateTimeConverter
import io.github.truenine.composeserver.depend.jackson.modules.DatetimeCustomModule
import io.github.truenine.composeserver.depend.jackson.modules.KotlinCustomModule
import io.github.truenine.composeserver.logger
import java.time.ZoneOffset
import java.util.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
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
@EnableConfigurationProperties(JacksonProperties::class)
class JacksonAutoConfiguration(private val jacksonProperties: JacksonProperties) {

  init {
    log.debug("jackson 自动配置中...")
  }

  private fun customize(builder: Jackson2ObjectMapperBuilder, customizers: List<Jackson2ObjectMapperBuilderCustomizer>) {
    log.debug("start customizing jackson,builder: {}, customizers: {}", builder, customizers)
    for (customizer in customizers) {
      customizer.customize(builder)
    }
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @ConditionalOnMissingBean
  fun jacksonObjectMapperBuilder(
    applicationContext: ApplicationContext,
    customizers: List<Jackson2ObjectMapperBuilderCustomizer>,
  ): Jackson2ObjectMapperBuilder {
    log.debug("replace spring web default jackson config, customizers: {}", customizers)
    val builder = Jackson2ObjectMapperBuilder()
    builder.applicationContext(applicationContext)
    customize(builder, customizers)
    return builder
  }

  @Primary
  @Bean(name = [DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  @ConditionalOnMissingBean
  fun jacksonObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
    log.debug("create jackson objectMapper, builder: {}", builder)
    return builder.createXmlMapper(false).build()
  }

  @Bean
  @Primary
  fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
    log.debug("config jackson custom jackson2ObjectMapperBuilderCustomizer with properties: {}", jacksonProperties)

    // 移除硬编码时区，使用UTC作为基准
    val datetimeModuleCustom = DatetimeCustomModule()
    val kotlinModuleCustom = KotlinCustomModule()
    val kotlinModule = KotlinModule.Builder().build()
    val javaTimeModule = JavaTimeModule()

    return Jackson2ObjectMapperBuilderCustomizer { b ->
      b.modules(javaTimeModule, kotlinModule, datetimeModuleCustom, kotlinModuleCustom)

      // 使用UTC时区，避免时区问题
      b.timeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
      b.locale(Locale.US)
      b.simpleDateFormat(DateTimeConverter.DATETIME)
      b.defaultViewInclusion(true)
      b.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)

      // 根据配置启用或禁用时间戳序列化
      if (jacksonProperties.enableTimestampSerialization && jacksonProperties.writeDatesAsTimestamps) {
        b.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        log.debug("enabled timestamp serialization with unit: {}", jacksonProperties.timestampUnit)
      } else {
        b.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        log.debug("disabled timestamp serialization")
      }

      b.featuresToDisable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

      // 根据配置设置序列化包含策略
      b.serializationInclusion(jacksonProperties.serializationInclusion)

      // 根据配置设置未知属性处理
      if (jacksonProperties.failOnUnknownProperties) {
        b.featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      } else {
        b.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      }
    }
  }

  companion object {
    const val NON_IGNORE_OBJECT_MAPPER_BEAN_NAME = "nonIgnoreObjectMapper"
    const val DEFAULT_OBJECT_MAPPER_BEAN_NAME = "defaultObjectMapper"

    @JvmStatic private val log = logger<JacksonAutoConfiguration>()
  }

  /** 安全的忽略注解内省器，只在特定条件下忽略属性 */
  class SafeIgnoreAnnotationIntrospector : JacksonAnnotationIntrospector() {
    override fun findPropertyIgnoralByName(config: MapperConfig<*>?, a: Annotated?): JsonIgnoreProperties.Value? {
      // 只在特定条件下忽略属性，避免所有属性都被忽略
      return null
    }

    override fun _isIgnorable(a: Annotated?): Boolean = false

    override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean = false

    override fun isIgnorableType(ac: AnnotatedClass?): Boolean = false
  }

  class IgnoreIntroPair(primary: AnnotationIntrospector, secondary: AnnotationIntrospector) : AnnotationIntrospectorPair(primary, secondary) {
    override fun findPropertyIgnoralByName(config: MapperConfig<*>?, a: Annotated?): JsonIgnoreProperties.Value = JsonIgnoreProperties.Value.empty()

    override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean = false

    override fun isIgnorableType(ac: AnnotatedClass?): Boolean = false
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @Bean(name = [NON_IGNORE_OBJECT_MAPPER_BEAN_NAME])
  fun nonIgnoreObjectMapper(mapper: ObjectMapper): ObjectMapper {
    log.debug("register non-ignore objectMapper, defaultMapper = {}", mapper)
    return mapper.copy().apply {
      disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      // 确保KotlinModule只注册一次，避免重复注册
      if (!registeredModuleIds.contains(KotlinModule::class.java.name)) {
        registerModules(KotlinModule.Builder().build())
      }
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
      setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
      activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
      // 使用安全的注解内省器，与原有的内省器配对
      val originalIntrospector = deserializationConfig.annotationIntrospector
      setAnnotationIntrospector(IgnoreIntroPair(originalIntrospector, SafeIgnoreAnnotationIntrospector()))
    }
  }
}
