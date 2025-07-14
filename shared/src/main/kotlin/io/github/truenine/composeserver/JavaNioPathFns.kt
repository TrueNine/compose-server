package io.github.truenine.composeserver

import io.github.truenine.composeserver.domain.IPage
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

/**
 * Checks if the path represents a regular file.
 *
 * @return true if the path exists and is a regular file, false otherwise
 */
fun Path.isFile(): Boolean = exists() && Files.isRegularFile(this)

/**
 * Checks if the file is empty or if the path doesn't represent a file.
 *
 * @return true if the file is empty, is a directory, or doesn't exist; false if it's a non-empty file
 */
fun Path.isEmpty(): Boolean = !isFile() || Files.size(this) == 0L

/**
 * Opens a FileChannel for the file with the specified mode.
 *
 * @param mode the file access mode (default: "r" for read-only)
 * @return FileChannel for the file
 * @throws FileNotFoundException if the path is not a regular file
 */
fun Path.fileChannel(mode: String = "r"): FileChannel {
  if (!isFile()) {
    throw FileNotFoundException("$this is not a file")
  }
  return RandomAccessFile(this.absolutePathString(), mode).channel
}

/**
 * Slices lines from the file within the specified range.
 *
 * @param range the range of lines to slice (inclusive)
 * @param sep the line separator (default: system line separator)
 * @param charset the character encoding (default: UTF-8)
 * @param bufferCapacity the buffer capacity for reading (default: 8192)
 * @param totalLines the total number of lines (if known, to avoid recounting)
 * @return a sequence of strings representing the sliced lines
 */
fun Path.sliceLines(
  range: LongRange,
  sep: String = lineSep,
  charset: Charset = Charsets.UTF_8,
  bufferCapacity: Int = capacity,
  totalLines: Long? = null,
): Sequence<String> {
  val lineLength = totalLines ?: countLines()
  val sliceRange = range.toSafeRange(min = 0, max = lineLength)

  return sequence {
    if (isEmpty()) return@sequence
    fileChannel().use { channel ->
      val first = sliceRange.first.toSafeInt()
      val takeCount = (sliceRange.last.toSafeInt() - first).coerceAtLeast(0)

      countWordBySeparator(sep = sep, bufferCapacity = bufferCapacity, charset = charset).drop(first).take(takeCount).forEach { (start, end) ->
        channel.position(start)
        val len = (end - start).toSafeInt()
        if (len > 0) {
          val buffer = ByteBuffer.allocateDirect(len)
          channel.read(buffer)
          buffer.flip()
          yield(String(buffer - len, charset))
        } else {
          yield("")
        }
      }
    }
  }
}

/**
 * Extracts bytes from ByteBuffer into a ByteArray.
 *
 * @param other the number of bytes to extract
 * @return ByteArray containing the extracted bytes
 */
private operator fun ByteBuffer.minus(other: Int): ByteArray {
  val arr = ByteArray(other)
  get(arr)
  return arr
}

/**
 * Extracts bytes from ByteBuffer into a ByteArray with specific positioning.
 *
 * @param other Triple containing (position, offset, length)
 * @return ByteArray containing the extracted bytes
 */
private operator fun ByteBuffer.minus(other: Triple<Int, Int, Int>): ByteArray {
  val arr = ByteArray(other.third)
  this.get(other.first, arr, other.second, other.third)
  return arr
}

/**
 * Loops through bytes in the FileChannel using a buffer.
 *
 * @param bufferCapacity the capacity of the buffer (default: 8192)
 * @param block the function to execute for each buffer
 */
inline fun FileChannel.loopBytes(bufferCapacity: Int = capacity, block: (ByteBuffer) -> Unit) {
  require(bufferCapacity > 0) { "Buffer capacity must be positive" }

  val buffer = ByteBuffer.allocateDirect(bufferCapacity)
  while (read(buffer) != -1) {
    buffer.flip()
    while (buffer.hasRemaining()) {
      block(buffer)
    }
    buffer.clear()
  }
}

/**
 * Counts words/segments separated by a specific separator in the file.
 *
 * @param sep the separator string (default: system line separator)
 * @param bufferCapacity the buffer capacity for reading (default: 8192)
 * @param charset the character encoding (default: UTF-8)
 * @return a sequence of pairs representing (start position, end position) of each segment
 * @throws IllegalStateException if separator is empty
 */
fun Path.countWordBySeparator(sep: String = lineSep, bufferCapacity: Int = capacity, charset: Charset = Charsets.UTF_8): Sequence<Pair<Long, Long>> {
  if (isEmpty()) return emptySequence()
  if (sep.isEmpty()) error("separator must be text")
  require(bufferCapacity > 0) { "buffer capacity must be positive" }

  val sepBytes = sep.toByteArray(charset)
  val sepLen = sepBytes.size

  return sequence {
    var segmentStart = 0L
    var currentPosition = 0L

    fileChannel().use { channel ->
      val buffer = ByteBuffer.allocateDirect(bufferCapacity)
      val searchBuffer = ByteArray(sepLen)
      var searchIndex = 0

      while (channel.read(buffer) != -1) {
        buffer.flip()

        while (buffer.hasRemaining()) {
          val byte = buffer.get()
          currentPosition++

          if (byte == sepBytes[searchIndex]) {
            searchBuffer[searchIndex] = byte
            searchIndex++

            if (searchIndex == sepLen) {
              // Found complete separator
              yield(segmentStart to currentPosition - sepLen)
              segmentStart = currentPosition
              searchIndex = 0
            }
          } else {
            searchIndex = 0
          }
        }
        buffer.clear()
      }

      // Yield the last segment if there's remaining content
      if (segmentStart < currentPosition) {
        yield(segmentStart to currentPosition)
      }
    }
  }
}

/**
 * Counts the number of lines in the file efficiently using FileChannel.
 *
 * @return the number of lines in the file
 */
fun Path.countLines(): Long {
  if (isEmpty()) return 0L

  var lineCount = 0L
  var lastWasNewline = false

  fileChannel().use { channel ->
    val buffer = ByteBuffer.allocateDirect(capacity)

    while (channel.read(buffer) != -1) {
      buffer.flip()

      while (buffer.hasRemaining()) {
        val byte = buffer.get()
        if (byte == '\n'.code.toByte()) {
          lineCount++
          lastWasNewline = true
        } else if (byte == '\r'.code.toByte()) {
          // Handle Windows line endings (\r\n) and Mac line endings (\r)
          lineCount++
          lastWasNewline = true
          // Peek ahead for \n to avoid double counting \r\n
          if (buffer.hasRemaining()) {
            val nextByte = buffer.get(buffer.position())
            if (nextByte == '\n'.code.toByte()) {
              buffer.get() // consume the \n
            }
          }
        } else {
          lastWasNewline = false
        }
      }
      buffer.clear()
    }
  }

  // If file doesn't end with newline and has content, count the last line
  if (!lastWasNewline && Files.size(this) > 0) {
    lineCount++
  }

  return lineCount
}

/**
 * Gets the size of the file in bytes.
 *
 * @return the file size in bytes, or 0 if the file is empty or doesn't exist
 */
fun Path.fileSize(): Long = if (isEmpty()) 0L else Files.size(this)

/**
 * Paginates lines from the file based on the provided parameters.
 *
 * @param param the pagination parameters
 * @param sep the line separator (default: system line separator)
 * @param charset the character encoding (default: UTF-8)
 * @return a paginated result containing the requested lines
 */
fun Path.pageLines(param: Pq, sep: String = lineSep, charset: Charset = Charsets.UTF_8): Pr<String> {
  if (isEmpty() || sep.isEmpty()) return IPage.emptyWith()

  val total = countLines()
  val p = param + total.toSafeInt()
  val range = p.toLongRange()
  val dataList = sliceLines(range = range, totalLines = total, sep = sep, charset = charset).toList()

  return Pr[dataList, total, p]
}
