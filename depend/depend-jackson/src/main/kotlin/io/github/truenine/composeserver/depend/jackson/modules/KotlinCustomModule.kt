package io.github.truenine.composeserver.depend.jackson.modules

import io.github.truenine.composeserver.depend.jackson.serializers.*
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.domain.IPageParamLike
import tools.jackson.core.Version
import tools.jackson.databind.module.*

/**
 * Custom Kotlin Jackson module.
 *
 * Provides serialization and deserialization support for Kotlin-specific types, including:
 * - Pair type serialization / deserialization
 * - IPageParam and IPageParamLike serialization / deserialization
 *
 * This module is compatible with timestamp serializers and does not interfere with time type handling.
 */
class KotlinCustomModule : SimpleModule(KotlinCustomModule::class.java.name, Version(1, 0, 0, "", "io.github.truenine", "composeserver")) {

  override fun setupModule(context: SetupContext) {
    super.setupModule(context)

    // Register serializers
    context.addSerializers(SimpleSerializers().apply { addSerializer(Pair::class.java, KPairSerializer()) })

    // Register deserializers
    context.addDeserializers(
      SimpleDeserializers().apply {
        addDeserializer(Pair::class.java, KPairDeserializer())
        addDeserializer(IPageParamLike::class.java, IPageParamLikeSerializer())
        addDeserializer(IPageParam::class.java, IPageParamLikeSerializer())
      }
    )
  }
}
