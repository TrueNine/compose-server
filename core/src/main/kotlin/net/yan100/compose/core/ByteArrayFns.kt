package net.yan100.compose.core

val ByteArray.utf8String: String
  get() = String(this, Charsets.UTF_8)
