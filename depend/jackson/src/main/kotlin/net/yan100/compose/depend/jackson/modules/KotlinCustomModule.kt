package net.yan100.compose.depend.jackson.modules

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.module.SimpleSerializers
import net.yan100.compose.core.domain.IPageParam
import net.yan100.compose.core.domain.IPageParamLike
import net.yan100.compose.core.typing.AnyTyping
import net.yan100.compose.depend.jackson.serializers.AnyTypingDeserializer
import net.yan100.compose.depend.jackson.serializers.AnyTypingSerializer
import net.yan100.compose.depend.jackson.serializers.IPageParamLikeSerializer
import net.yan100.compose.depend.jackson.serializers.KPairDeserializer
import kotlin.reflect.KClass

class KotlinCustomModule : SimpleModule(
  KotlinCustomModule::class.java.name,
  com.fasterxml.jackson.core.Version(0, 0, 1, "", "net.yan100", "compose")
) {

  class EnumDeserializers : Deserializers.Base() {
    @Suppress("UNCHECKED_CAST")
    override fun findEnumDeserializer(
      type: Class<*>?,
      config: DeserializationConfig?,
      beanDesc: BeanDescription?
    ): JsonDeserializer<*>? {
      if (type == null) return super.findEnumDeserializer(type, config, beanDesc)
      if (!type.isEnum) return super.findEnumDeserializer(type, config, beanDesc)
      if (!AnyTyping::class.java.isAssignableFrom(type)) return super.findEnumDeserializer(type, config, beanDesc)
      return AnyTypingDeserializer(type.kotlin as KClass<Enum<*>>)
    }
  }

  override fun setupModule(context: SetupContext) {
    super.setupModule(context)

    context.addDeserializers(EnumDeserializers())

    context.addDeserializers(
      SimpleDeserializers(
        buildMap {
          put(Pair::class.java, KPairDeserializer())
          put(IPageParamLike::class.java, IPageParamLikeSerializer())
          put(IPageParam::class.java, IPageParamLikeSerializer())
        }
      )
    )

    context.addSerializers(
      SimpleSerializers(
        buildList {
          add(AnyTypingSerializer())
        }
      )
    )


  }
}
