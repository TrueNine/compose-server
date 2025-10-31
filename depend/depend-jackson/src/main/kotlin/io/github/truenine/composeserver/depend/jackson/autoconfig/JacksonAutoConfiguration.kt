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
import org.springframework.context.annotation.Scope
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * Jackson JSON serialization strategy configuration
 *
 * @author TrueNine
 * @since 2023-02-23
 */
@Configuration
@EnableConfigurationProperties(JacksonProperties::class)
class JacksonAutoConfiguration(private val jacksonProperties: JacksonProperties) {

  init {
    log.debug("jackson auto config...")
  }

  private fun customize(builder: Jackson2ObjectMapperBuilder, customizers: List<Jackson2ObjectMapperBuilderCustomizer>) {
    log.debug("start customizing jackson,builder: {}, customizers: {}", builder, customizers)
    for (customizer in customizers) {
      customizer.customize(builder)
    }
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @ConditionalOnMissingBean(Jackson2ObjectMapperBuilder::class)
  fun jacksonObjectMapperBuilder(
    applicationContext: ApplicationContext,
    customizers: List<Jackson2ObjectMapperBuilderCustomizer>?,
  ): Jackson2ObjectMapperBuilder {
    log.debug("replace spring web default jackson config, customizers: {}", customizers)
    val builder = Jackson2ObjectMapperBuilder()
    builder.applicationContext(applicationContext)
    if (customizers != null) {
      customize(builder, customizers)
    }
    return builder
  }

  @Bean(name = [DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  @ConditionalOnMissingBean(value = [ObjectMapper::class])
  @org.springframework.context.annotation.Primary
  @org.springframework.beans.factory.annotation.Qualifier(DEFAULT_OBJECT_MAPPER_BEAN_NAME)
  fun jacksonObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
    log.debug("create jackson objectMapper, builder: {}", builder)
    return builder.createXmlMapper(false).build()
  }

  @Bean
  fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
    log.debug("config jackson custom jackson2ObjectMapperBuilderCustomizer with properties: {}", jacksonProperties)

    // Remove hardcoded timezone, use UTC as baseline
    val datetimeModuleCustom = DatetimeCustomModule()
    val kotlinModuleCustom = KotlinCustomModule()
    val kotlinModule = KotlinModule.Builder().build()
    val javaTimeModule = JavaTimeModule()

    return Jackson2ObjectMapperBuilderCustomizer { b ->
      b.modules(javaTimeModule, kotlinModule, datetimeModuleCustom, kotlinModuleCustom)

      // Use UTC timezone to avoid timezone issues
      b.timeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
      b.locale(Locale.US)
      b.simpleDateFormat(DateTimeConverter.DATETIME)
      b.defaultViewInclusion(true)
      b.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)

      // Enable or disable timestamp serialization based on configuration
      if (jacksonProperties.enableTimestampSerialization && jacksonProperties.writeDatesAsTimestamps) {
        b.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        log.debug("enabled timestamp serialization with unit: {}", jacksonProperties.timestampUnit)
      } else {
        b.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        log.debug("disabled timestamp serialization")
      }

      b.featuresToDisable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

      // Set serialization inclusion policy based on configuration
      b.serializationInclusion(jacksonProperties.serializationInclusion)

      // Set unknown property handling based on configuration
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

  /** Safe ignore annotation introspector, only ignores properties under specific conditions */
  class SafeIgnoreAnnotationIntrospector : JacksonAnnotationIntrospector() {
    override fun findPropertyIgnoralByName(config: MapperConfig<*>?, a: Annotated?): JsonIgnoreProperties.Value? {
      // Only ignore properties under specific conditions to avoid ignoring all properties
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
  @org.springframework.beans.factory.annotation.Qualifier("nonIgnoreObjectMapper")
  fun nonIgnoreObjectMapper(mapper: ObjectMapper): ObjectMapper {
    log.debug("register non-ignore objectMapper, defaultMapper = {}", mapper)
    return mapper.copy().apply {
      disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      // Ensure KotlinModule is registered only once to avoid duplicate registration
      if (!registeredModuleIds.contains(KotlinModule::class.java.name)) {
        registerModules(KotlinModule.Builder().build())
      }
      setSerializationInclusion(JsonInclude.Include.NON_NULL)
      setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
      activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
      // Use a safe annotation introspector, paired with the original introspector
      val originalIntrospector = deserializationConfig.annotationIntrospector
      setAnnotationIntrospector(IgnoreIntroPair(originalIntrospector, SafeIgnoreAnnotationIntrospector()))
    }
  }
}
