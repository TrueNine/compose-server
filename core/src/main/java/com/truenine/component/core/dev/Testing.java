package com.truenine.component.core.dev;

import java.util.function.Consumer;

/**
 * 测试
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class Testing {
  public static void around(Runnable before,
                            Runnable execute,
                            Runnable after,
                            Consumer<Throwable> error,
                            Runnable finallyExecute) {
    try {
      before.run();
      execute.run();
      after.run();
    } catch (Throwable ex) {
      error.accept(ex);
    } finally {
      finallyExecute.run();
    }
  }

  public static long timer(Runnable task) {
    long start = System.currentTimeMillis();
    task.run();
    long end = System.currentTimeMillis();
    return end - start;
  }
}
