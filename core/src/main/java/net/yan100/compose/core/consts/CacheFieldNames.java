package net.yan100.compose.core.consts;

/**
 * 缓存 key 命名空间
 *
 * @author TrueNine
 * @since 2023-01-01
 */
public interface CacheFieldNames {

    interface RedisTemplate {
        String STRING_TEMPLATE = "RedisCacheTemplateForStringKey";
    }

    interface Redis {
        String M30 = "RedisCacheManager30m";
        String H1 = "RedisCacheManager2h";
        String H2 = "RedisCacheManager2h";
        String D3 = "RedisCacheManager30d";
        String FOREVER = "RedisCacheManagerForever";
    }

    interface Caffeine {
        String CACHE = "CaffeineCacheManager";
        String M30 = "CaffeineCacheManager30m";
        String H1 = "CaffeineCacheManager2h";
        String H2 = "CaffeineCacheManager2h";
        String H3 = "CaffeineCacheManager3h";
        String D1 = "CaffeineCacheManager1d";
        String D2 = "CaffeineCacheManager2d";
        String D3 = "CaffeineCacheManager30d";
        String FOREVER = "CaffeineCacheManagerForever";
    }
}
