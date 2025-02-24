package net.yan100.compose.depend.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import net.yan100.compose.core.domain.IPageParam
import net.yan100.compose.core.domain.IPageParamLike
import net.yan100.compose.depend.jackson.serializers.IPageParamLikeSerializer
import net.yan100.compose.depend.jackson.serializers.KPairDeserializer

class KotlinCustomModule : SimpleModule(
  KotlinCustomModule::class.java.name,
  com.fasterxml.jackson.core.Version(0, 0, 1, "", "net.yan100", "compose")
) {
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
