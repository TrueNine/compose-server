package io.tn.core.lang.exceptions;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 抛出运行时异常的工具类
 *
 * @author TrueNine
 * @since 2023-02-20
 */
public class RuntimeExceptionThrow {


  public static <E> E tryCache(Supplier<E> execute, Consumer<Throwable> errExecute) {
    try {
      return execute.get();
    } catch (Throwable ex) {
      errExecute.accept(ex);
    }
    return null;
  }

  public static void runtimeErr(String msg) {
    throw new RuntimeException(msg);
  }

  public static void runtimeErr() {
    runtimeErr("出现运行时错误");
  }

  public static void runtimeErr(String msg, Throwable throwable) {
    throw new RuntimeException(msg, throwable);
  }

  public static void runtimeErr(Throwable throwable) {
    runtimeErr("出现运行时错误", throwable);
  }
}
