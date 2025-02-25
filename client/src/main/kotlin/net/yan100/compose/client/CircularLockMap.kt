package net.yan100.compose.client

class CircularLockMap<K, V : Any>(
  private val maxCount: Int = 63,
  private val contents: MutableList<V> = mutableListOf(),
  private val contentsMap: MutableMap<K, V> = mutableMapOf(),
  val update: () -> Map<K, V>,
) {
  private fun clearAndUpdate() {
    contentsMap.clear()
    contents.clear()
    contentsMap.putAll(update())
    contents.addAll(contentsMap.values)
  }

  private var circularCounter: Int = 0
  val map: Map<K, V>
    get() {
      if (circularCounter > maxCount) {
        circularCounter = 0
        error("Circular reference detected in definitions")
      }
      return if (contentsMap.isNotEmpty()) {
        circularCounter = 0
        contentsMap
      } else {
        circularCounter += 1
        clearAndUpdate()
        contentsMap
      }
    }

  val elements: List<V>
    get() {
      if (circularCounter > maxCount) {
        circularCounter = 0
        error("Circular reference detected in definitions")
      }
      return if (contents.isNotEmpty()) {
        circularCounter = 0
        contents
      } else {
        circularCounter += 1
        clearAndUpdate()
        contents
      }
    }

  operator fun component1(): Map<K, V> = map

  operator fun component2(): List<V> = elements

  operator fun get(key: K): V? {
    return map[key]
  }
}
