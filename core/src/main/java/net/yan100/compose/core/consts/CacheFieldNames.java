/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
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
