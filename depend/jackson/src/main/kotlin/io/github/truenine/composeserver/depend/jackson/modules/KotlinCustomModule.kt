package io.github.truenine.composeserver.depend.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.truenine.composeserver.depend.jackson.serializers.IPageParamLikeSerializer
import io.github.truenine.composeserver.depend.jackson.serializers.KPairDeserializer
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.domain.IPageParamLike

class KotlinCustomModule : SimpleModule(KotlinCustomModule::class.java.name, com.fasterxml.jackson.core.Version(0, 0, 1, "", "net.yan100", "compose")) {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(
      SimpleDeserializers(
        buildMap {
          put(Pair::class.java, KPairDeserializer())
          put(IPageParamLike::class.java, IPageParamLikeSerializer())
          put(IPageParam::class.java, IPageParamLikeSerializer())
        }
      )
    )
  }
}
