package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.typing.AnyTyping
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import org.babyfish.jimmer.sql.runtime.AbstractScalarProvider

abstract class AbstractJimmerTypingProvider<T : AnyTyping, S : Any>(ct: KClass<T>, st: KClass<S>) : AbstractScalarProvider<T, S>(ct.java, st.java) {
  private val converterCache: MutableMap<Type, Method> = ConcurrentHashMap()

  private fun getCallable(type: Type): Method {
    val st = scalarType
    val fn = converterCache[st]
    return if (fn != null) {
      fn.also { converterCache[type] = fn }
    } else {
      val methods = (type as Class<*>).declaredMethods.filter { (it.name == "get" || it.name == "findVal") && it.parameters.size == 1 }
      val callFn = methods.firstOrNull() ?: error("type: $type has no method named 'get' or 'findVal'")
      callFn.also { converterCache[type] = callFn }
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun toScalar(sqlValue: S): T? {
    return getCallable(scalarType)(sqlValue) as? T?
  }

  @Suppress("UNCHECKED_CAST")
  override fun toSql(scalarValue: T): S? {
    return scalarValue as? S?
  }
}
