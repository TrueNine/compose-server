package net.yan100.compose.client.generator

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.entries.TsFile
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.standardInterceptors
import net.yan100.compose.meta.client.ClientApiStubs


class TypescriptGenerator(
  interceptorChain: MutableList<Interceptor<*, *, *>> = mutableListOf(),
  stubsProvider: () -> ClientApiStubs
) {
  private val stubs = stubsProvider().copy()
  var context: KtToTsContext = KtToTsContext(
    stubs,
    *interceptorChain.toTypedArray(),
    *standardInterceptors.toTypedArray()
  )

  internal val convertedTsScopes: List<TsScope> get() = context.tsScopes

  internal fun renderInterface(interfaceScope: TsScope.Interface) = TsFile.SingleInterface(interfaceScope)
  fun renderStaticInterfacesToFiles(): List<TsFile<TsFile.SingleInterface>> {
    val interfaces = context.tsScopes.filterIsInstance<TsScope.Interface>()
    return interfaces.map {
      renderInterface(it)
    }
  }

  internal fun renderEnum(enums: TsScope.Enum): TsFile.SingleEnum = TsFile.SingleEnum(enums)

  fun renderEnumsToFiles(): List<TsFile.SingleEnum> {
    val enums = context.tsScopes.filterIsInstance<TsScope.Enum>()
    return enums.map {
      renderEnum(it)
    }
  }

  fun renderExecutorToFile(): TsFile.SingleTypeUtils {
    return TsFile.SingleTypeUtils(
      fileName = TsName.Name("Executor"),
      usedNames = listOf("HTTPMethod", "BodyType").map(TsName::Name),
      render = {
        """
type HTTPMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'HEAD' | 'OPTIONS' | 'TRACE'
type BodyType = 'json' | 'form'
export type Executor = (requestOptions: {
  readonly uri: `/${'$'}{string}`
  readonly method: HTTPMethod
  readonly headers?: {readonly [key: string]: string}
  readonly body?: unknown
  readonly bodyType?: BodyType
}) => Promise<unknown>
    """.trimIndent().plus("\n")
      }
    )
  }
}
