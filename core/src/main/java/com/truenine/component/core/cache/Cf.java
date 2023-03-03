package com.truenine.component.core.cache;

/**
 * 缓存 key 命名空间
 *
 * @author TrueNine
 * @since 2023-01-01
 */
public final class Cf {

  public static final class RedisTemplate {
    public static final String STRING_TEMPLATE = "cacheTemplateForStringKey";
  }

  public static final class CacheManager {
    public static final String H2 = "cacheManager2h";
    public static final String D3 = "cacheManager30d";
    public static final String M30 = "cacheManager30m";
    public static final String FOREVER = "cacheManagerForever";
  }

  public static final class User {
    public static final String DETAILS = "user:details";
  }
}
