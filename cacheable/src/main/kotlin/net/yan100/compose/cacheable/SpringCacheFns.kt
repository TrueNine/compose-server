package net.yan100.compose.cacheable

import kotlin.reflect.KClass
import org.springframework.cache.Cache

operator fun <T : Any> Cache.get(key: Any, type: KClass<T>): T? =
  get(key, type.java)
