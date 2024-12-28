package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.*
import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.KotlinToTypescriptInterceptor
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

class TsStaticInterceptor : KotlinToTypescriptInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE

  private val supportedKinds = listOf(
    TypeKind.INTERFACE,
    TypeKind.CLASS
  )

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeKind in supportedKinds

  override fun process(ctx: KtToTsContext, source: ClientType): TsScope {
    val name = source.typeName.toTsStylePathName()
    val properties = ctx.getClientPropsByClientType(source)
    val generics = source.argumentLocations.mapIndexed { i, it ->
      TsGeneric.Defined(
        name = TsName.Name(it),
        index = i
      )
    }

    // 如果 没有属性，则处理为 type xxx = object
    if (properties.isEmpty()) {
      return TsScope.TypeVal(
        definition = TsTypeVal.Record(
          keyUsedGeneric = TsGeneric.Used(TsTypeVal.String, index = 0),
          valueUsedGeneric = TsGeneric.Used(TsTypeVal.Unknown, index = 1)
        ),
        meta = source
      )
    }

    val superTypes = source.superTypes.mapNotNull {
      when (val r = ctx.resolveTsTypeValByClientType(it)) {
        is TsTypeVal.TypeDef -> {
          r.copy(
            typeName = r.typeName,
            usedGenerics = it.toTsGenericUsed { er ->
              if (er.typeName.isGenericName()) {
                er.typeName.unwrapGenericName().toTsStyleName()
              } else {
                ctx.resolveTsTypeValByClientTypeTypeName(er.typeName).toTsName()
              }
            }
          )
        }

        is TsTypeVal.Any,
        is TsTypeVal.String,
        is TsTypeVal.Unknown,
        is TsTypeVal.TypeConstant,
        is TsTypeVal.EmptyObject -> null

        is TsTypeVal.Record,
        is TsTypeVal.Object -> r

        else -> null
      }
    }

    return TsScope.Interface(
      meta = source,
      name = name,
      superTypes = superTypes,
      generics = generics,
      properties = properties
    )
  }
}
