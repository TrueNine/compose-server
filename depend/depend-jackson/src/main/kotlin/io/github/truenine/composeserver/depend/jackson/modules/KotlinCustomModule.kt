package io.github.truenine.composeserver.depend.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.module.SimpleSerializers
import io.github.truenine.composeserver.depend.jackson.serializers.IPageParamLikeSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.KPairDeserializer
import io.github.truenine.composeserver.depend.jackson.serializers.KPairSerializer
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.domain.IPageParamLike

/**
 * Kotlin自定义模块
 *
 * 提供Kotlin特有类型的序列化和反序列化支持，包括：
 * - Pair类型的序列化/反序列化
 * - IPageParam和IPageParamLike的序列化/反序列化
 *
 * 该模块与时间戳序列化模块兼容，不会干扰时间类型的序列化行为
 */
class KotlinCustomModule :
  SimpleModule(KotlinCustomModule::class.java.name, com.fasterxml.jackson.core.Version(1, 0, 0, "", "io.github.truenine", "composeserver")) {

  override fun setupModule(context: SetupContext) {
    super.setupModule(context)

    // 注册序列化器
    context.addSerializers(SimpleSerializers().apply { addSerializer(Pair::class.java, KPairSerializer()) })

    // 注册反序列化器
    context.addDeserializers(
      SimpleDeserializers().apply {
        addDeserializer(Pair::class.java, KPairDeserializer())
        addDeserializer(IPageParamLike::class.java, IPageParamLikeSerializer())
        addDeserializer(IPageParam::class.java, IPageParamLikeSerializer())
      }
    )
  }
}
