package net.yan100.compose.client.contexts

import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.PropertyToPropertyInterceptor
import net.yan100.compose.client.interceptors.TypeToTypeInterceptor
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind


class KotlinGeneratorLauncher(
  api: ClientApiStubs,
  private val interceptors: List<Interceptor<*, *>> = emptyList(),
) {
  internal val api: ClientApiStubs = api.copy()
  internal var definitions = api.definitions.toMutableList()
  internal var services = api.services.toMutableList()

  internal val builtinDefinitions get() = definitions.filter { it.builtin == true }
  internal val typeAliasDefinitions get() = definitions.filter { it.typeKind == TypeKind.TYPEALIAS && it.isAlias == true }
  internal val enumDefinitions get() = definitions.filter { it.typeKind == TypeKind.ENUM_CLASS }
  internal val jimmerDefinitions get() = definitions.filter { it.typeKind == TypeKind.IMMUTABLE }
  internal val jimmerEmbeddedDefinitions get() = definitions.filter { it.typeKind == TypeKind.EMBEDDABLE }
  internal val otherDefinitions
    get() = definitions.filter {
      it.typeKind != TypeKind.TYPEALIAS
        && it.typeKind != TypeKind.ENUM_CLASS
        && it.typeKind != TypeKind.IMMUTABLE
        && it.typeKind != TypeKind.EMBEDDABLE
    }


  fun renderTopLevelUtils() {

  }


  internal fun handleClientTypeInterceptors(definitions: List<ClientType>, interceptors: List<Interceptor<*, *>>): List<ClientType> {
    if (definitions.isEmpty() || interceptors.isEmpty()) return definitions
    var all = definitions
    var modified: Boolean
    val maxIterations = 512
    var iterationCount = 0
    do {
      if (iterationCount++ >= maxIterations) throw IllegalStateException("Maximum number of iterations reached")
      modified = false
      val newAll = mutableListOf<ClientType>()
      for (inter in interceptors) {
        when (inter) {
          is TypeToTypeInterceptor -> newAll.addAll(processInterceptor(inter, all))
          is PropertyToPropertyInterceptor -> {
            newAll.addAll(all.map { typ ->
              if (typ.properties.isNotEmpty()) {
                val props = processInterceptor(inter, typ.properties)
                if (props != typ.properties) {
                  modified = true
                }
                typ.copy(properties = props)
              } else typ
            })
          }

          else -> error("Unsupported interceptor type: ${inter::class}")
        }
      }
      all = newAll
    } while (modified)
    return all
  }

  private fun <T : Any, S : Any> processInterceptor(interceptor: Interceptor<S, T>, all: List<S>): List<T> {
    val (supported, unsupported) = all.partition { interceptor.supported(it) }
    return unsupported.map { interceptor.defaultProcess(it) } + supported.map { interceptor.process(it) }
  }
}
