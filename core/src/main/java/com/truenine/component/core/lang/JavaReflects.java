package com.truenine.component.core.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 反射工具方法
 * Copyright
 *
 * @author TrueNine
 * @since 2022-06-11
 * @deprecated 有更好的反射替代方案
 */
@Deprecated
public class JavaReflects {

  private static final List<Class<?>> CLASS_POOL = new CopyOnWriteArrayList<>();

  static {
    var classNamePool = ResourcesLocator
      .getClassNamePool();
    for (String s : classNamePool) {
      try {
        CLASS_POOL.add(
          Class.forName(s)
        );
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static boolean isAnnotated(Class<? extends Annotation> anno, Object targetObject) {
    if (null == targetObject) {
      return false;
    }
    return targetObject.getClass().isAnnotationPresent(anno);
  }

  public static <A extends Annotation> A getAnnotationFromClass(Class<?> cls, Class<A> aCls) {
    return cls.getAnnotation(aCls);
  }

  public static <A extends Annotation> List<Class<?>> getAnnotatedAllClass(Class<A> annotation) {
    return CLASS_POOL.stream().filter(c -> Objects.nonNull(c.getAnnotation(annotation))).toList();
  }

  public static <F extends AccessibleObject> void accessible(F ref, Consumer<F> exec) {
    ref.setAccessible(true);
    exec.accept(ref);
    ref.setAccessible(false);
  }

  public static <F extends AccessibleObject> void accessibleAll(F[] refs, Consumer<F> exec) {
    Arrays.stream(refs).forEach(ref -> accessible(ref, exec));
  }
}
