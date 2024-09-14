package net.yan100.compose.cacheable.service

import net.yan100.compose.cacheable.CacheDuration
import kotlin.reflect.KClass


interface ICacheStore<C : ICache> {
  operator fun get(duration: CacheDuration): C
  fun getNative(): Any
  fun nameToDuration(name: String): CacheDuration
  fun durationToName(duration: CacheDuration): String
}

interface IMemoryCacheStore : ICacheStore<IMemoryCache>
interface IPersistentCacheStore : ICacheStore<IPersistentCache>

interface ICache {
  fun getNative(): Any

  operator fun <T : Any> get(key: Any, clazz: KClass<T>): T?
  operator fun set(key: Any, value: Any?) = put(key, value)
  operator fun minusAssign(key: Any) = evict(key)

  fun getAny(key: Any): Any?
  fun put(key: Any, value: Any?)
  fun evict(key: Any)
  fun clear()
}

interface IMemoryCache : ICache

interface IPersistentCache : IMemoryCache
