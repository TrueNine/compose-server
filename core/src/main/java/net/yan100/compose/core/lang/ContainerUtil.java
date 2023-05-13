package net.yan100.compose.core.lang;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

/**
 * 数组跑龙套
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Slf4j
public class ContainerUtil {

  public static <T, C extends Collection<Set<T>>> Set<T> unfoldNestedSetBy(Supplier<C> nestedSet) {
    if (isNullOrEmpty(nestedSet.get())) {
      return new HashSet<>(0);
    }
    return nestedSet.get().stream()
      .filter(i -> !ContainerUtil.isNullOrEmpty(i))
      .reduce((a, b) -> {
        var c = new HashSet<>(a);
        c.addAll(b);
        return c;
      }).orElse(new HashSet<>(0));
  }

  public static <T, C extends Collection<List<T>>> List<T> unfoldNestedListBy(Supplier<C> nestedList) {
    var li = isNullOrEmpty(nestedList.get());
    if (li) {
      return new ArrayList<T>(0);
    }
    return nestedList.get().stream()
      .filter(i -> !ContainerUtil.isNullOrEmpty(i))
      .reduce((a, b) -> {
        var c = new ArrayList<T>(a);
        c.addAll(b);
        return c;
      }).orElse(new ArrayList<>());
  }


  public static @Nullable byte[] unpackByteArray(Byte[] byteArray) {
    var out = new ByteArrayOutputStream();
    try (out) {
      Arrays.stream(byteArray).forEach(out::write);
      return out.toByteArray();
    } catch (IOException e) {
      log.warn("数组拷贝出现io异常", e);
      return null;
    }
  }

  public static byte[] unpackByteArray(Collection<Byte> byteCollection) {
    return unpackByteArray(byteCollection.toArray(Byte[]::new));
  }

  public static boolean isNullOrEmpty(byte[] arr) {
    return Objects.isNull(arr) || arr.length == 0;
  }

  public static boolean isNullOrEmpty(int[] arr) {
    return Objects.isNull(arr) || arr.length == 0;
  }

  public static boolean isNullOrEmpty(long[] arr) {
    return Objects.isNull(arr) || arr.length == 0;
  }

  public static boolean isNullOrEmpty(Object[] arr) {
    return Objects.isNull(arr) || arr.length == 0;
  }

  public static @Nullable <T> T lastItem(T[] arr) {
    if (isNullOrEmpty(arr)) {
      return null;
    }
    return arr[arr.length - 1];
  }

  public static boolean isNullOrEmpty(Collection<?> arr) {
    return Objects.isNull(arr) || arr.size() == 0;
  }
}
