package io.tn.core.lang

object ContainerKt {
  fun <T, C : Collection<Set<T>>> unfoldNestedSetBy(container: (C)): Set<T> =
    ContainerUtil.unfoldNestedSetBy {
      container
    }

  fun <T, C : Collection<List<T>>> unfoldNestedListBy(container: C): List<T> =
    ContainerUtil.unfoldNestedListBy {
      container
    }
}