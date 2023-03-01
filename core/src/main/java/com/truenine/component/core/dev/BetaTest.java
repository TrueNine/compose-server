package com.truenine.component.core.dev;

import java.lang.annotation.*;

/**
 * 该注解标志这这个类、方法、参数……
 * <br/>
 * 可能在未来版本移除或者变更，因为底层依赖不稳定
 * <br/>
 * 或者目前也没有找到好的解决方式
 *
 * @author TrueNine
 * @date 2022-10-26
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({
  ElementType.ANNOTATION_TYPE,
  ElementType.CONSTRUCTOR,
  ElementType.FIELD,
  ElementType.METHOD,
  ElementType.TYPE})
@Documented
public @interface BetaTest {
}
