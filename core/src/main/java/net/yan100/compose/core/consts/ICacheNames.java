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
public interface ICacheNames {
    interface IRedis {
        String HANDLE = "JsonStringRedisTemplateYan100Handle";
        String CACHE_MANAGER = "JsonStringRedisTemplateYan100CacheManager";

        String M1 = "RedisJsonStringCache_duration_1m";
        String M5 = "RedisJsonStringCache_duration_5m";
        String M10 = "RedisJsonStringCache_duration_10m";
        String M30 = "RedisJsonStringCache_duration_30m";
        String H1 = "RedisJsonStringCache_duration_1h";
        String H2 = "RedisJsonStringCache_duration_2h";
        String H3 = "RedisJsonStringCache_duration_3h";
        String D1 = "RedisJsonStringCache_duration_1d";
        String D2 = "RedisJsonStringCache_duration_2d";
        String D3 = "RedisJsonStringCache_duration_3d";
        String D7 = "RedisJsonStringCache_duration_7d";

        String D30 = "RedisJsonStringCache_duration_30d";
        String D60 = "RedisJsonStringCache_duration_60d";
        String D180 = "RedisJsonStringCache_duration_180d";
        String D365 = "RedisJsonStringCache_duration_365d";

        String W1 = D7;
        String MO1 = D30;
        String Y1 = D365;
        String[] ALL = new String[]{M1, M5, M10, M30, H1, H2, H3, D1, D2, D3, D7, D30, D60, D180, D365};
    }

    interface ICaffeine {
        String HANDLE = "JavaCaffeineTemplateYan100Handle";
        String CACHE_MANAGER = "JavaCaffeineTemplateYan100CacheManager";

        String M1 = "CaffeineCache_duration_1m";
        String M5 = "CaffeineCache_duration_5m";
        String M10 = "CaffeineCache_duration_10m";
        String M30 = "CaffeineCache_duration_30m";
        String H1 = "CaffeineCache_duration_1h";
        String H2 = "CaffeineCache_duration_2h";
        String H3 = "CaffeineCache_duration_3h";
        String D1 = "CaffeineCache_duration_1d";
        String D2 = "CaffeineCache_duration_2d";
        String D3 = "CaffeineCache_duration_3d";
        String D7 = "CaffeineCache_duration_7d";

        String D30 = "CaffeineCache_duration_30d";
        String D60 = "CaffeineCache_duration_60d";
        String D180 = "CaffeineCache_duration_180d";
        String D365 = "CaffeineCache_duration_365d";

        String W1 = D7;
        String MO1 = D30;
        String Y1 = D365;
        String[] ALL = new String[]{M1, M5, M10, M30, H1, H2, H3, D1, D2, D3, D7, D30, D60, D180, D365};
    }
}
