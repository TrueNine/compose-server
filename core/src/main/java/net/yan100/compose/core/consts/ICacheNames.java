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
    String M1 = "durational_cache_duration_1m";
    String M5 = "durational_cache_duration_5m";
    String M10 = "durational_cache_duration_10m";
    String M30 = "durational_cache_duration_30m";

    String H1 = "durational_cache_duration_1h";
    String H2 = "durational_cache_duration_2h";
    String H3 = "durational_cache_duration_3h";

    String D1 = "durational_cache_duration_1d";
    String D2 = "durational_cache_duration_2d";
    String D3 = "durational_cache_duration_3d";
    String D7 = "durational_cache_duration_7d";

    String D30 = "durational_cache_duration_30d";
    String D60 = "durational_cache_duration_60d";
    String D180 = "durational_cache_duration_180d";
    String D365 = "durational_cache_duration_365d";

    String FOREVER = "durational_cache_duration_fo";

    String W1 = D7;
    String MO1 = D30;
    String Y1 = D365;

    String[] ALL = new String[]{M1, M5, M10, M30, H1, H2, H3, D1, D2, D3, D7, D30, D60, D180, D365, FOREVER};

    interface IRedis {
        String HANDLE = "JsonStringRedisTemplateYan100Handle";
        String CACHE_MANAGER = "JsonStringRedisTemplateYan100CacheManager";
    }

    interface ICaffeine {
        String HANDLE = "JavaCaffeineTemplateYan100Handle";
        String CACHE_MANAGER = "JavaCaffeineTemplateYan100CacheManager";
    }
}
