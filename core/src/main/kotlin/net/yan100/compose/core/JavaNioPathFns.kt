/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core

import net.yan100.compose.core.domain.IPage
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

const val capacity = 8192
private val lineSep: String = System.lineSeparator()

fun Path.isFile(): Boolean {
  return if (exists()) Files.isRegularFile(this) else false
}

fun Path.isEmpty(): Boolean {
  return if (isFile()) Files.size(this) == 0L else true
}

fun Path.fileChannel(mode: String = "r"): FileChannel {
  return if (isFile()) RandomAccessFile(this.absolutePathString(), mode).channel else throw FileNotFoundException("$this is not a file")
}

fun Path.sliceLines(
  range: LongRange,
  sep: String = lineSep,
  charset: Charset = Charsets.UTF_8,
  bufferCapacity: Int = capacity,
  totalLines: Long? = null,
): Sequence<String> {
  // TODO 写死的 countLines
  val lineLength = totalLines ?: countLines()
  val sliceRange = range.toSafeRange(min = 0, max = lineLength)

  return sequence {
    if (isEmpty()) return@sequence
    fileChannel().use { channel ->
      val first = sliceRange.first.toSafeInt()
      countWordBySeparator(sep = sep, bufferCapacity = bufferCapacity, charset = charset)
        .drop(first)
        .take(sliceRange.last.toSafeInt() - sliceRange.first.toSafeInt())
        .map {
          channel.position(it.first)
          val len = it.second.toSafeInt() - it.first.toSafeInt()
          val buffer = ByteBuffer.allocateDirect(len)
          channel.read(buffer)
          buffer.flip()
          if (len > 0) String(buffer - len, charset) else ""
        }
        .let { yieldAll(it) }
    }
  }
}

private operator fun ByteBuffer.minus(other: Int): ByteArray {
  val arr = ByteArray(other)
  get(arr)
  return arr
}

private operator fun ByteBuffer.minus(other: Triple<Int, Int, Int>): ByteArray {
  val arr = ByteArray(other.third)
  this.get(other.first, arr, other.second, other.third)
  return arr
}

inline fun FileChannel.loopBytes(bufferCapacity: Int = capacity, block: (ByteBuffer) -> Unit) {
  val buffer = ByteBuffer.allocateDirect(bufferCapacity)
  while (read(buffer) != -1) {
    buffer.flip()
    while (buffer.hasRemaining()) {
      block(buffer)
    }
    buffer.clear()
  }
}

fun Path.countWordBySeparator(sep: String = lineSep, bufferCapacity: Int = capacity, charset: Charset = Charsets.UTF_8): Sequence<Pair<Long, Long>> {
  if (isEmpty()) return emptySequence()
  if (sep.isEmpty()) error("separator must be text")

  val sepBytes = sep.toByteArray(charset = charset)
  val sepLen = sepBytes.size
  var lastBytes: ByteArray? = null
  return sequence {
    var prevPosition = 0L
    var stepSize = 0L
    fileChannel().use { channel ->
      channel.loopBytes(bufferCapacity) { buffer ->
        val currentPosition = buffer.position()
        val remaining = buffer.remaining()
        if (remaining > sepLen) {
          val stepBytes = buffer - sepLen
          buffer.position((buffer.position() - sepLen) + 1)
          if (stepBytes.contentEquals(sepBytes)) {
            yield(prevPosition to stepSize)
            prevPosition = (currentPosition + sepLen).toLong()
          }
        } else lastBytes = buffer - buffer.remaining()
        stepSize++
      }
    }
    lastBytes?.also { yield(prevPosition to stepSize) }
  }
}

fun Path.countLines(): Long = Files.lines(this).count()

fun Path.fileSize(): Long {
  if (isEmpty()) return 0L
  return Files.size(this)
}

fun Path.pageLines(param: Pq, sep: String = lineSep, charset: Charset = Charsets.UTF_8): Pr<String> {
  return if (isEmpty() || sep.isEmpty()) IPage.emptyWith()
  else {
    val total = countLines()
    val p = param + total
    val range = p.toLongRange()
    val dataList = sliceLines(range = range, totalLines = total, sep = sep, charset = charset).toList()
    return Pr[dataList, total, p]
  }
}
