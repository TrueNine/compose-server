package com.truenine.component.core.lang;

import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 一个 any字典，方便 set get 值
 * 此类以任何类型作为值，key 为 {@link String}
 *
 * @author TrueNine
 * @since 2022-11-18
 */
@EqualsAndHashCode
public class AnyDict {
  private static final String LESSED = "isSingle" + UUID.randomUUID();
  private final Map<String, Object> DICT = new ConcurrentHashMap<>();

  @Nullable
  @SuppressWarnings("unchecked")
  public <V> V get(String key) {
    return (V) DICT.get(key);
  }

  @SuppressWarnings("unchecked")
  public <V> void forEach(BiConsumer<String, V> kv) {
    for (Map.Entry<String, Object> entry : DICT.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      kv.accept(key, (V) value);
    }
  }

  @SuppressWarnings("unchecked")
  public <V> V getOrElse(String key, V defaultVal) {
    return Optional.ofNullable((V) DICT.get(key)).orElse(defaultVal);
  }

  public void clear() {
    DICT.clear();
  }

  public boolean isLessed() {
    return Boolean.TRUE.equals(get(LESSED));
  }

  public void setLessed(boolean isSingle) {
    DICT.put(LESSED, isSingle);
  }

  public void remove(String key, Object value) {
    DICT.remove(key, value);
  }

  public void remove(String key) {
    DICT.remove(key);
  }

  public void put(String key, Object anyValue) {
    DICT.put(key, anyValue);
  }

  @Override
  public String toString() {
    return DICT.toString();
  }
}
