package net.yan100.compose.core.models

import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "kotlin 入参替代品")
class KPair<K, V> {
    var first: K? = null
    var second: V? = null

    @Suppress("UNCHECKED_CAST")
    fun toPair(): Pair<K, V> {
        return Pair(first, second) as Pair<K, V>
    }
}
