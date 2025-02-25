package net.yan100.compose.client.interceptors.kt

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.client.interceptors.KotlinToKotlinInterceptor
import net.yan100.compose.client.isGenericName
import net.yan100.compose.client.unwrapGenericName
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

open class JavaInternalInterceptor : KotlinToKotlinInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_CLASS

  override fun defaultProcess(
    ctx: KtToKtContext,
    source: ClientType,
  ): ClientType = source

  private val supportedKinds = listOf(TypeKind.CLASS, TypeKind.INTERFACE)

  override fun supported(ctx: KtToKtContext, source: ClientType): Boolean {
    return source.typeKind in supportedKinds
  }

  override fun process(ctx: KtToKtContext, source: ClientType): ClientType {
    return source.copy(
      typeName = ctx.getTypeNameByName(source.typeName),
      superTypes = source.superTypes.map { process(ctx, it) },
      aliasForTypeName =
        source.aliasForTypeName?.let { ctx.getTypeNameByName(it) },
      arguments = source.arguments.map { it.unwrapGenericName() },
      properties =
        source.properties.map { p ->
          p.copy(
            typeName = ctx.getTypeNameByName(p.typeName),
            usedGenerics =
              p.usedGenerics.map { ig ->
                ig.copy(
                  typeName =
                    if (p.typeName.isGenericName()) ig.typeName
                    else ctx.getTypeNameByName(ig.typeName)
                )
              },
          )
        },
    )
  }
}
