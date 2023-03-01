package io.tn.core.spring

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import io.tn.core.annotations.SensitiveRef

/**
 * jackson json 脱敏属性策略配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
class JacksonSensitiveSerializer
  : JsonSerializer<String>(), ContextualSerializer {

  private lateinit var strategy: SensitiveRef.Strategy

  override fun serialize(
    value: String?,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    value?.apply {
      gen?.writeString(strategy.desensitizeSerializer().invoke(this))
    }
  }

  override fun createContextual(
    prov: SerializerProvider?,
    property: BeanProperty?
  ): JsonSerializer<*>? {
    val ref: SensitiveRef? = property?.getAnnotation(SensitiveRef::class.java)
    val pros = property?.type?.rawClass
    val that = this
    ref?.apply {
      pros?.apply {
        strategy = ref.value
        return that
      }
    }
    return prov?.findValueSerializer(property?.type, property)
  }
}
