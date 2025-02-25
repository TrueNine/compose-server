package net.yan100.compose.core.consts

/**
 * 缓存 key 命名空间
 *
 * @author TrueNine
 * @since 2023-01-01
 */
interface ICacheNames {
  interface IRedis {
    companion object {
      const val HANDLE: String = "JsonStringRedisTemplateYan100Handle"
      const val CACHE_MANAGER: String =
        "JsonStringRedisTemplateYan100CacheManager"
    }
  }

  interface ICaffeine {
    companion object {
      const val HANDLE: String = "JavaCaffeineTemplateYan100Handle"
      const val CACHE_MANAGER: String = "JavaCaffeineTemplateYan100CacheManager"
    }
  }

  companion object {
    const val M1: String = "durational_cache_duration_1m"
    const val M5: String = "durational_cache_duration_5m"
    const val M10: String = "durational_cache_duration_10m"
    const val M30: String = "durational_cache_duration_30m"

    const val H1: String = "durational_cache_duration_1h"
    const val H2: String = "durational_cache_duration_2h"
    const val H3: String = "durational_cache_duration_3h"

    const val D1: String = "durational_cache_duration_1d"
    const val D2: String = "durational_cache_duration_2d"
    const val D3: String = "durational_cache_duration_3d"
    const val D7: String = "durational_cache_duration_7d"

    const val D30: String = "durational_cache_duration_30d"
    const val D60: String = "durational_cache_duration_60d"
    const val D180: String = "durational_cache_duration_180d"
    const val D365: String = "durational_cache_duration_365d"

    const val FOREVER: String = "durational_cache_duration_fo"

    const val W1: String = D7
    const val MO1: String = D30
    const val Y1: String = D365

    val ALL: Array<String> =
      arrayOf(
        M1,
        M5,
        M10,
        M30,
        H1,
        H2,
        H3,
        D1,
        D2,
        D3,
        D7,
        D30,
        D60,
        D180,
        D365,
        FOREVER,
      )
  }
}
