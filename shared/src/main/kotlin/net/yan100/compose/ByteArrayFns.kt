package net.yan100.compose

val ByteArray.utf8String: String
  get() = String(this, Charsets.UTF_8)
