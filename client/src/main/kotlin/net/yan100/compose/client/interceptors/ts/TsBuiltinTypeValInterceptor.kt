package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.KotlinToTsTypeValInterceptor
import net.yan100.compose.meta.client.ClientType

open class TsBuiltinTypeValInterceptor : KotlinToTsTypeValInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_TS_REFERENCES
  private val nameTable = mapOf(
    "java.io.Serializable" to TsTypeVal.Never,
    "java.lang.String" to TsTypeVal.String,
    "java.lang.CharSequence" to TsTypeVal.String,
    "kotlin.String" to TsTypeVal.String,
    "kotlin.Any" to TsTypeVal.Any,
    "java.lang.Cloneable" to TsTypeVal.Never,

    "java.lang.Object" to TsTypeVal.Any,
    "java.lang.Void" to TsTypeVal.Void,
    "java.lang.Boolean" to TsTypeVal.Boolean,
    "java.lang.Integer" to TsTypeVal.Number,
    "java.lang.Comparable" to TsTypeVal.Never,
    "java.lang.Long" to TsTypeVal.Number,
    "java.lang.Double" to TsTypeVal.Number,
    "java.lang.Float" to TsTypeVal.Number,
    "java.lang.Byte" to TsTypeVal.Number,
    "java.lang.Number" to TsTypeVal.Number,
    "java.lang.Character" to TsTypeVal.String,
    "kotlin.ByteArray" to TsTypeVal.Array(
      TsGeneric.Used(
        used = TsTypeVal.Number,
        index = 0
      )
    ),
    "kotlin.IntArray" to TsTypeVal.Array(
      TsGeneric.Used(
        used = TsTypeVal.Number,
        index = 0
      )
    ),
  )

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeName in nameTable
  override fun process(ctx: KtToTsContext, source: ClientType): TsTypeVal = nameTable[ctx.getTypeNameByName(source.typeName)]!!
}