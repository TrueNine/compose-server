package com.truenine.component.core.lang;

/**
 * 空体默认接口，查看当前类或者实现是否为空体
 *
 * @author TrueNine
 * @since 2023-04-11
 */
public interface EmptyDefaultModel {
  static boolean isEmptyDefaultModel(Object anyObject) {
    return anyObject instanceof EmptyDefaultModel;
  }
}
