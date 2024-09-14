package net.yan100.compose.cacheable

import org.springframework.cache.Cache
import kotlin.reflect.KClass

operator fun <T : Any> Cache.get(key: Any, type: KClass<T>): T? = get(key, type.java)
