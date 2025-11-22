package io.github.truenine.composeserver

import java.io.BufferedReader
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset

@Deprecated("Redundant helper, use bufferedReader() instead", replaceWith = ReplaceWith("bufferedReader()"))
fun InputStream.toReader(charset: Charset = Charsets.UTF_8): Reader {
  return BufferedReader(reader(charset))
}
