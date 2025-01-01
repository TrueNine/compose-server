package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.ifNotGenericName
import net.yan100.compose.client.interceptors.TsPostScopeInterceptor
import net.yan100.compose.meta.client.ClientType

class TsPostScopeUseGenericInterceptor : TsPostScopeInterceptor() {
  override fun supported(ctx: KtToTsContext, source: Pair<ClientType, TsScope<*>>): Boolean = source.second.isRequireUseGeneric
  override fun process(ctx: KtToTsContext, source: Pair<ClientType, TsScope<*>>): TsScope<*> {
    val (_, scope) = source
    return when (scope) {
      is TsScope.Interface -> {
        val r = scope.meta
        // TODO 处理父类类型
        val supers = ctx.getUnUsedSuperTypes(r)
        val properties = if (scope.properties.any { it.isRequireUseGeneric() }) {
          (r.properties zip scope.properties).map { (meta, prop) ->
            meta.typeName.ifNotGenericName(prop) {
              if (prop.isRequireUseGeneric()) {
                val uses = ctx.getTsGenericByGenerics(meta.usedGenerics)
                prop.fillGenerics(uses)
              } else prop
            }
          }
        } else scope.properties
        scope.copy(
          superTypes = supers,
          properties = properties
        )
      }

      is TsScope.TypeAlias -> TODO()
      is TsScope.TypeVal -> {
        scope.meta
        scope
      }

      is TsScope.Class,
      is TsScope.Enum -> scope
    }
  }
}
