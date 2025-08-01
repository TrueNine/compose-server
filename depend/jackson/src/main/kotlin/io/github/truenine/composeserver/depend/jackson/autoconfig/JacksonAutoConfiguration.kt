package io.github.truenine.composeserver.depend.jackson.autoconfig

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.truenine.composeserver.DateTimeConverter
import io.github.truenine.composeserver.depend.jackson.modules.DatetimeCustomModule
import io.github.truenine.composeserver.depend.jackson.modules.KotlinCustomModule
import io.github.truenine.composeserver.slf4j
import java.time.ZoneOffset
import java.util.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
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
class JacksonAutoConfiguration {

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
    log.debug("config jackson custom jackson2ObjectMapperBuilderCustomizer")
    val zoneOffset = ZoneOffset.ofHours(8)

    val datetimeModuleCustom = DatetimeCustomModule(zoneOffset)
    val kotlinModuleCustom = KotlinCustomModule()
    val kotlinModule = KotlinModule.Builder().build()
    val javaTimeModule = JavaTimeModule()

    return Jackson2ObjectMapperBuilderCustomizer { b ->
      b.modules(javaTimeModule, kotlinModule, datetimeModuleCustom, kotlinModuleCustom)

      b.timeZone(TimeZone.getTimeZone(zoneOffset))
      b.locale(Locale.US)
      b.simpleDateFormat(DateTimeConverter.DATETIME)
      b.defaultViewInclusion(true)
      b.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
      b.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
      b.serializationInclusion(JsonInclude.Include.NON_NULL)
      b.serializationInclusion(JsonInclude.Include.NON_EMPTY)
      b.serializationInclusion(JsonInclude.Include.NON_ABSENT)
    }
  }

  companion object {
    const val NON_IGNORE_OBJECT_MAPPER_BEAN_NAME = "nonJsonIgnoreObjectMapper"
    const val DEFAULT_OBJECT_MAPPER_BEAN_NAME = "jacksonObjectMapper"

    private val log = slf4j<JacksonAutoConfiguration>()
  }

  class IgnoreJsonIgnoreAnnotationIntrospector : JacksonAnnotationIntrospector() {
    override fun findPropertyIgnoralByName(config: MapperConfig<*>?, a: Annotated?): JsonIgnoreProperties.Value = JsonIgnoreProperties.Value.empty()

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
  fun nonDeserializerObjectMapper(mapper: ObjectMapper): ObjectMapper {
    log.debug("register non-ignore objectMapper, defaultMapper = {}", mapper)
    return mapper.copy().let {
      it
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .registerModules(KotlinModule.Builder().build())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        .activateDefaultTyping(it.polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
      it
    }
  }
}
