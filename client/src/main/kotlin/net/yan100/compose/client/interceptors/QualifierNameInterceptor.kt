package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToKtContext

abstract class QualifierNameInterceptor :
  Interceptor<String, String, KtToKtContext> {
  val kotlinJavaNameMap =
    mapOf(
      "kotlin.collections.Map\$Entry" to "java.util.Map\$Entry",
      "kotlin.io.Serializable" to "java.io.Serializable",
      "kotlin.Comparable" to "java.lang.Comparable",
      "kotlin.Nothing" to "java.lang.Void",
      "kotlin.Number" to "java.lang.Number",
      "kotlin.Any" to "java.lang.Object",
      "kotlin.Int" to "java.lang.Integer",
      "kotlin.Long" to "java.lang.Long",
      "kotlin.Float" to "java.lang.Float",
      "kotlin.Double" to "java.lang.Double",
      "kotlin.Char" to "java.lang.Character",
      "kotlin.Byte" to "java.lang.Byte",
      "kotlin.Short" to "java.lang.Short",
      "kotlin.Boolean" to "java.lang.Boolean",
      "kotlin.Unit" to "java.lang.Void",
      "kotlin.CharSequence" to "java.lang.CharSequence",
      "kotlin.String" to "java.lang.String",
      "kotlin.Enum" to "java.lang.Enum",
      "kotlin.collections.Iterable" to "java.lang.Iterable",
      "kotlin.collections.Iterator" to "java.util.Iterator",
      "kotlin.collections.Collection" to "java.util.Collection",
      "kotlin.collections.List" to "java.util.List",
      "kotlin.collections.Set" to "java.util.Set",
      "kotlin.collections.Map" to "java.util.Map",
    )

  override val executeStage: ExecuteStage = ExecuteStage.CONVERT_QUALIFIER_NAME

  override fun defaultProcess(ctx: KtToKtContext, source: String): String =
    source

  open class KotlinNameToJavaNameInterceptor : QualifierNameInterceptor() {
    override fun supported(ctx: KtToKtContext, source: String): Boolean {
      return source in kotlinJavaNameMap.keys
    }

    override fun process(ctx: KtToKtContext, source: String): String {
      return kotlinJavaNameMap[source]!!
    }
  }
}
