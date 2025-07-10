package io.github.truenine.composeserver

import java.util.stream.Stream

fun <T> Stream<T>.slice(start: Int = 0, end: Int? = null): Stream<T> = skip(start.toLong()).limit((end ?: count()).toLong())
