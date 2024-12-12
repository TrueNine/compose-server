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


import net.yan100.compose.testtookit.TempDirMapping
import net.yan100.compose.testtookit.log
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaNioPathFnsTest {
  @TempDirMapping
  lateinit var tempDir: Path

  @Test
  fun `test slice line first line`() {
    val firstLineFile = tempDir.resolve("firstLine.txt")

    val firstLine = "\nLine 1\nLine 2\nLine 3\nLine 4\n"
    File(firstLineFile.toUri()).writeText(firstLine)
    val lines = firstLineFile.countLines()

    log.info("test len {}", firstLine.length)
    log.info("lines {}", lines)

    val result = firstLineFile.sliceLines(sep = "\n", range = 0L..firstLine.length)
    val listResult = result.toList()
  }

  @Test
  fun `test slice lines`() {
    val tempFile = tempDir.resolve("temper.txt")
    val text = "Line 1\nLine 2\nLine 3\nLine 4"
    log.info("text len {}", text.length)

    File(tempFile.toUri()).writeText(text)

    val result = tempFile.sliceLines(sep = "\n", range = 0L..text.length)
    val listResult = result.toList()

    log.info("line result: {}", listResult)

    assertEquals(4, listResult.size, "The number of lines should be 4")
    assertEquals("Line 1", listResult[0], "The first line should be 'Line 1'")
    assertEquals("Line 2", listResult[1], "The second line should be 'Line 2'")
    assertEquals("Line 3", listResult[2], "The third line should be 'Line 3'")
    assertEquals("Line 4", listResult[3], "The fourth line should be 'Line 4'")
  }

  @Test
  fun `test count lines`() {
    val tempFile = File.createTempFile("test count lines", ".txt")
    tempFile.deleteOnExit()
    tempFile.writeBytes("Hello\nWorld\nThis\nis\na\nTest\ne".toByteArray())

    val testPath = tempFile.toPath()
    val actualLines = testPath.countLines()
    assertEquals(7, actualLines, "The number of lines counted does not match the expected value.")

    tempFile.writeText("")
    val emptyLines = testPath.countLines()
    assertEquals(0, emptyLines, "The number of lines counted for an empty file should be 0.")

    tempFile.writeText("he\n")
    val oneLines = testPath.countLines()
    assertEquals(1, oneLines, "The number of lines counted for an empty file should be 0.")

    tempFile.writeText("a\nb")
    val twoLines = testPath.countLines()
    assertEquals(2, twoLines, "The number of lines counted for an empty file should be 0.")
  }

  @Test
  fun `test page lines`() {
    val tempFile = File.createTempFile("test page lines", ".txt")
    tempFile.deleteOnExit()
    tempFile.writeText("Hello\nWorld\nThis\nis\na\nTest\ne")
    log.info("tempFile: {}", tempFile)
    log.info("tempFile exists: {}", tempFile.exists())
    val testPath = tempFile.toPath()
    log.info("tempPath countLines: {}", testPath.countLines())


    val pre = testPath.pageLines(Pq[1, 4], "\n")

    log.info("pr: {}", pre)

    assertEquals(7, pre.t)
    assertEquals(3, pre.d.size)
    assertEquals("a", pre[0])
    assertEquals(2, pre.p)

    val pr1 = testPath.pageLines(Pq[3, 2], "\n")

    assertEquals(7, pr1.t)
    assertEquals("e", pr1[0])
    assertEquals(4, pr1.p)
  }
}
