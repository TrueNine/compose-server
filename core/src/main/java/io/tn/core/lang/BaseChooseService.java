package io.tn.core.lang;

import jakarta.annotation.Nullable;

import java.util.List;

/**
 * 策略模式基础接口
 *
 * @param <T> 选择类型
 * @author TrueNine
 * @since 2022-12-11
 */
public interface BaseChooseService<T> {
  /**
   * 查找第一个符合判断条件的类型
   *
   * @param chooses 策略组
   * @param type    类型
   * @param <R>     实现类
   * @return 实现
   */
  @SuppressWarnings("unchecked")
  default @Nullable <R> R findFirst(List<? extends BaseChooseService<T>> chooses, T type) {
    return (R) chooses.stream().filter(ele -> ele.choose(type)).findFirst().orElse(null);
  }

  /**
   * 类型判断
   *
   * @param type 类型
   * @return 是否为当前类型
   */
  Boolean choose(T type);
}
