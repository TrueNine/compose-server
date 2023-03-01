package com.truenine.component.data.common.crawler.annotations;

import java.lang.annotation.*;

/**
 * 页面路径
 *
 * @author TrueNine
 * @since 2022-10-28
 */// schedulers
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PagePath {
  String ROOT_PATH = "/spider-nav-default-root";

  /**
   * 路径备选地址
   *
   * @return {@link String}[]
   */
  String value() default ROOT_PATH;

  /**
   * 路径备用地址
   *
   * @return {@link String}[]
   */
  String path() default ROOT_PATH;

  /**
   * 路径的优先顺序
   *
   * @return int
   */
  int order() default Integer.MIN_VALUE;

  /**
   * 当前页面是不是动态页面
   *
   * @return boolean
   */
  boolean dynamic() default false;
}
