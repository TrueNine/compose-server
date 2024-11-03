package net.yan100.compose.core

import java.io.BufferedReader
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset

@Deprecated("多此一举", replaceWith = ReplaceWith("bufferedReader()"))
fun InputStream.toReader(charset: Charset = Charsets.UTF_8): Reader {
  return BufferedReader(reader(charset))
}
