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

import cn.hutool.core.io.resource.ClassPathResource
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.test.Test
import kotlin.test.assertEquals
import net.yan100.compose.core.models.page.IPageParam
import org.junit.jupiter.api.io.TempDir

class PathExtensionFunctionsTest {
  private val logPath = Paths.get(ClassPathResource("logs_test/1010.testlog").absolutePath)

  @Test
  fun `test lines`() {
    val lines = logPath.readLines().count()
    val aLines = logPath.lines().count()
    assertEquals(lines, aLines)
  }

  @TempDir lateinit var tempDir: Path

  @Test
  fun `test slice lines`() {
    val tempFile = tempDir.resolve("temp.txt")
    val text = "Line 1\nLine 2\nLine 3\nLine 4"
    File(tempFile.toUri()).writeText(text)

    val result = tempFile.sliceLines(0L..text.length)
    val listResult = result.toList()

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
    tempFile.writeBytes("Hello\nWorld\nThis\nis\na\nTest\ne".toByteArray())
    println(tempFile)
    println(tempFile.exists())
    val testPath = tempFile.toPath()
    println(testPath.countLines())
    val pr =
      testPath.pageLines(
        param =
          object : IPageParam {
            override var offset: Int? = 1
            override var pageSize: Int? = 1
            override var unPage: Boolean? = false
          }
      )

    println(pr)
  }
}
