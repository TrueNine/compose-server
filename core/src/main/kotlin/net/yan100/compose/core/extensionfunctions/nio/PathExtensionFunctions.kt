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
package net.yan100.compose.core.extensionfunctions.nio

import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import net.yan100.compose.core.extensionfunctions.range.toSafeRange
import net.yan100.compose.core.models.page.IPage
import net.yan100.compose.core.models.page.IPageParam

private const val capacity = 8192
private const val oneCapacity = 1
private const val lineSep = '\n'

fun Path.isFile(): Boolean {
  return if (exists()) Files.isRegularFile(this) else false
}

fun Path.isEmpty(): Boolean {
  return if (isFile()) Files.size(this) == 0L else true
}

fun Path.lines(): List<String> {
  return if (isEmpty()) emptyList() else Files.readAllLines(this)
}

fun Path.sliceLines(range: LongRange, charset: Charset = Charsets.UTF_8, bufferCapacity: Int = capacity, totalLines: Long? = null): Sequence<String> {
  val lineLength = totalLines ?: countLines()
  val sliceRange = range.toSafeRange(min = 0, max = lineLength)
  return sequence {
    if (isEmpty()) return@sequence
    val access = RandomAccessFile(this@sliceLines.absolutePathString(), "r")
    access.channel.use { channel ->
      val buffer = ByteBuffer.allocate(bufferCapacity)
      var currentLine = 0L
      var lineStart = 0L

      while (currentLine < sliceRange.first) {
        val bytesRead = channel.read(buffer)
        if (bytesRead == -1) break
        buffer.flip()
        while (buffer.hasRemaining()) {
          if (buffer.get().toInt().toChar() == '\n') {
            currentLine++
            if (currentLine >= sliceRange.first) {
              lineStart = channel.position() - buffer.remaining()
              break
            }
          }
        }
        buffer.clear()
      }
      channel.position(lineStart)
      while (currentLine <= sliceRange.last) {
        val bytesRead = channel.read(buffer)
        if (bytesRead == -1) break
        buffer.flip()
        buildString {
          while (buffer.hasRemaining()) {
            val byte = buffer.get()
            if (byte.toInt().toChar() == '\n') {
              currentLine++
              yield(toString())
              clear()
              if (currentLine > sliceRange.last) break
            } else append(byte.toInt().toChar())
          }
        }
        buffer.clear()
      }
    }
  }
}

private fun Path.countSeparatorByChar(char: Char = lineSep, bufferCapacity: Int = capacity): Long {
  if (isEmpty()) return 0L
  var lines = 0L
  val charByte = char.code.toByte()
  var lastCharWasSeparator = false

  FileChannel.open(this, StandardOpenOption.READ).use { channel ->
    val buffer = ByteBuffer.allocateDirect(bufferCapacity)
    while (channel.read(buffer) != -1) {
      buffer.flip()
      while (buffer.hasRemaining()) {
        if (buffer.get() == charByte) {
          lines++
          lastCharWasSeparator = true
        } else {
          // if (lastCharWasSeparator) lines++
          lastCharWasSeparator = false
        }
      }
      buffer.clear()
    }
    if (!lastCharWasSeparator) lines++
  }
  return lines
}

fun Path.countLines(bufferCapacity: Int = capacity): Long = countSeparatorByChar(bufferCapacity = bufferCapacity)

fun Path.fileSize(): Long {
  if (isEmpty()) return 0L
  return Files.size(this)
}

fun Path.pageLines(param: IPageParam): IPage<String> {
  return if (isEmpty()) IPage.empty()
  else {
    val total = countLines()
    val p = param.ofSafeTotal(total)
    val range = p.toLongRange()

    val dataList = sliceLines(range, totalLines = total).toList()
    return IPage.of(dataList = dataList, total = total, offset = p.safeOffset.toLong())
  }
}
