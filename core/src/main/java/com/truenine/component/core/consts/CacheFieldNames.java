package com.truenine.component.core.consts;

/**
 * 缓存 key 命名空间
 *
 * @author TrueNine
 * @since 2023-01-01
 */
public interface CacheFieldNames {

  interface RedisTemplate {
    String STRING_TEMPLATE = "cacheTemplateForStringKey";
  }

  interface CacheManagerNames {
    String H2 = "cacheManager2h";
    String D3 = "cacheManager30d";
    String M30 = "cacheManager30m";
    String FOREVER = "cacheManagerForever";
  }

  interface User {
    String DETAILS = "user:details";
  }
}
