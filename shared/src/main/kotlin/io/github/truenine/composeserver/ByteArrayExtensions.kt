package io.github.truenine.composeserver

val ByteArray.utf8String: String
  get() = String(this, Charsets.UTF_8)
