package io.tn.core.dev;

import java.lang.annotation.*;

/**
 * 当前被标记目标未被实现，暂时不可用
 *
 * @author TrueNine
 * @date 2022-11-08
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
public @interface UnImplemented {
}
