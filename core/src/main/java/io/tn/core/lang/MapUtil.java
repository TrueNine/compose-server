package io.tn.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Map 工具
 *
 * @author TrueNine
 * @date 2022-11-15
 */
public class MapUtil {

  public static <K, V> void batchKeyAppendChain(Map<K, List<V>> map,
                                                K[] ks,
                                                V val) {
    batchKeyAppendChain(map, ks, val, new ArrayList<>());
  }

  public static <K, V> void batchKeyAppendChain(Map<K, List<V>> map,
                                                K[] ks,
                                                V val,
                                                List<V> cs) {
    for (K k : ks) {
      appendChain(map, k, val, cs);
    }
  }

  public static <K, V> void appendChain(Map<K, List<V>> map,
                                        K Key,
                                        V val) {
    appendChain(map, Key, val, new ArrayList<>());
  }

  public static <K, V> void appendChain(Map<K, List<V>> map,
                                        K key,
                                        V val,
                                        List<V> cs) {
    if (map.containsKey(key)) {
      map.get(key).add(val);
    } else {
      cs.add(val);
      map.put(key, cs);
    }
  }
}
