package io.github.truenine.composeserver.depend.jackson.autoconfig

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import io.github.truenine.composeserver.DateTimeConverter
import io.github.truenine.composeserver.depend.jackson.modules.DatetimeCustomModule
import io.github.truenine.composeserver.depend.jackson.modules.KotlinCustomModule
import io.github.truenine.composeserver.logger
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import tools.jackson.databind.AnnotationIntrospector
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.JavaType
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.cfg.MapperConfig
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.introspect.AnnotatedClass
import tools.jackson.databind.introspect.AnnotatedMember
import tools.jackson.databind.introspect.AnnotationIntrospectorPair
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder
import tools.jackson.module.kotlin.KotlinModule

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

  private fun createBaseJsonMapperBuilder(): JsonMapper.Builder {
    val kotlinModule = KotlinModule.Builder().build()

    val builder = JsonMapper.builder()
    builder.addModule(kotlinModule)

    // Register custom modules
    builder.addModule(DatetimeCustomModule())
    builder.addModule(KotlinCustomModule())

    builder.defaultTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
    builder.defaultLocale(Locale.US)
    builder.defaultDateFormat(SimpleDateFormat(DateTimeConverter.DATETIME))

    builder.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    builder.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)

    if (jacksonProperties.failOnUnknownProperties) {
      builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    } else {
      builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    builder.changeDefaultPropertyInclusion { incl -> incl.withValueInclusion(jacksonProperties.serializationInclusion) }

    // Timestamp behavior is primarily controlled by DatetimeCustomModule; log configuration here
    if (jacksonProperties.enableTimestampSerialization && jacksonProperties.writeDatesAsTimestamps) {
      log.debug("enabled timestamp serialization with unit: {}", jacksonProperties.timestampUnit)
    } else {
      log.debug("disabled timestamp serialization")
    }

    return builder
  }

  @Bean(name = [DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  @ConditionalOnMissingBean(value = [ObjectMapper::class])
  @org.springframework.context.annotation.Primary
  @org.springframework.beans.factory.annotation.Qualifier(DEFAULT_OBJECT_MAPPER_BEAN_NAME)
  fun jacksonObjectMapper(): ObjectMapper {
    log.debug("create default JsonMapper")
    val mapper = createBaseJsonMapperBuilder().build()

    return mapper
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

    override fun hasIgnoreMarker(config: MapperConfig<*>?, m: AnnotatedMember?): Boolean = false

    override fun isIgnorableType(config: MapperConfig<*>?, ac: AnnotatedClass?): Boolean = false
  }

  class IgnoreIntroPair(primary: AnnotationIntrospector, secondary: AnnotationIntrospector) : AnnotationIntrospectorPair(primary, secondary) {
    override fun findPropertyIgnoralByName(config: MapperConfig<*>?, a: Annotated?): JsonIgnoreProperties.Value = JsonIgnoreProperties.Value.empty()

    override fun hasIgnoreMarker(config: MapperConfig<*>?, m: AnnotatedMember?): Boolean = false

    override fun isIgnorableType(config: MapperConfig<*>?, ac: AnnotatedClass?): Boolean = false
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @Bean(name = [NON_IGNORE_OBJECT_MAPPER_BEAN_NAME])
  @org.springframework.beans.factory.annotation.Qualifier("nonIgnoreObjectMapper")
  fun nonIgnoreObjectMapper(mapper: ObjectMapper): ObjectMapper {
    log.debug("register non-ignore objectMapper, defaultMapper = {}", mapper)
    val hasKotlinModule = mapper.registeredModules().any { module -> module.javaClass == KotlinModule::class.java }
    val builder = JsonMapper.builder()

    // Preserve modules from the default mapper
    mapper.registeredModules().forEach { module -> builder.addModule(module) }

    builder.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    // Ensure KotlinModule is registered only once to avoid duplicate registration
    if (!hasKotlinModule) {
      builder.addModule(KotlinModule.Builder().build())
    }
    builder.changeDefaultPropertyInclusion { incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL) }
    builder.changeDefaultVisibility { vc -> vc.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY) }
    // Use a safe annotation introspector, paired with the original introspector
    val originalIntrospector = mapper.deserializationConfig().annotationIntrospector
    builder.annotationIntrospector(IgnoreIntroPair(originalIntrospector, SafeIgnoreAnnotationIntrospector()))
    val polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build()
    val defaultTypingBuilder =
      object : DefaultTypeResolverBuilder(polymorphicTypeValidator, DefaultTyping.NON_FINAL_AND_ENUMS, JsonTypeInfo.As.PROPERTY) {
        override fun useForType(t: JavaType): Boolean {
          if (super.useForType(t)) {
            return true
          }
          if (isJacksonNaturalType(t)) {
            return false
          }
          return !t.isPrimitive()
        }
      }
    builder.setDefaultTyping(defaultTypingBuilder)
    return builder.build()
  }

  private fun isJacksonNaturalType(javaType: JavaType): Boolean {
    val rawClass = javaType.rawClass
    if (rawClass.isPrimitive) {
      return true
    }
    return rawClass == String::class.java ||
      CharSequence::class.java.isAssignableFrom(rawClass) ||
      rawClass == java.lang.Boolean::class.java ||
      rawClass == java.lang.Character::class.java
  }
}
