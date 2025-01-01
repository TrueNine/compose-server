package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.TsPreReferenceInterceptor
import net.yan100.compose.meta.client.ClientType

open class TsSpringMvcInterceptor : TsPreReferenceInterceptor() {
  val mappingMap = mapOf(
    "org.springframework.web.multipart.MultipartFile" to TsTypeVal.Ref(typeName = TsName.Name("File"))
  )

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeName in mappingMap

  override fun process(
    ctx: KtToTsContext,
    source: ClientType
  ): TsTypeVal<*> = mappingMap[source.typeName]!!
}
