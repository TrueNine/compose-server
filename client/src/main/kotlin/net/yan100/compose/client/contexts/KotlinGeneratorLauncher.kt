package net.yan100.compose.client.contexts

import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.PropertyInterceptor
import net.yan100.compose.client.interceptors.TypeInterceptor
import net.yan100.compose.meta.client.ClientApi
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind


class KotlinGeneratorLauncher(
  api: ClientApi,
  private val interceptors: List<Interceptor<*>> = emptyList(),
) {
  internal val api: ClientApi = api.copy()
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


  internal fun handleClientTypeInterceptors(definitions: List<ClientType>, interceptors: List<Interceptor<*>>): List<ClientType> {
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
          is TypeInterceptor -> newAll.addAll(processInterceptor(inter, all))
          is PropertyInterceptor -> {
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


  private fun <T : Any> processInterceptor(interceptor: Interceptor<T>, all: List<T>): List<T> {
    val processed = all.filter(interceptor::supported).map(interceptor::process)
    return all.filterNot(interceptor::supported) + processed
  }
}
