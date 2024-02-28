/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.util;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 策略模式基础接口
 *
 * @param <T> 选择类型
 * @author TrueNine
 * @since 2022-12-11
 */
public interface ChooseService<T> {
    /**
     * 查找第一个符合判断条件的类型
     *
     * @param chooses 策略组
     * @param type    类型
     * @param <R>     实现类
     * @return 实现
     */
    @SuppressWarnings("unchecked")
    default @Nullable <R> R findFirst(List<? extends ChooseService<T>> chooses, T type) {
        return (R) chooses.stream().filter(ele -> ele.choose(type)).findFirst().orElse(null);
    }

    /**
     * 获取一组实现了
     *
     * @param chooses 被选择服务
     * @param type    选择类型
     * @param <R>     实现类
     * @return 选中的一组服务
     */
    @SuppressWarnings("unchecked")
    default @Nullable <R> List<R> findAll(List<? extends ChooseService<T>> chooses, T type) {
        return chooses.stream()
                .filter(ele -> ele.choose(type))
                .map(r -> (R) r)
                .collect(Collectors.toList());
    }

    /**
     * 类型判断
     *
     * @param type 类型
     * @return 是否为当前类型
     */
    Boolean choose(T type);
}
