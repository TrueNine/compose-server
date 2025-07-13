package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.TempDirMapping
import io.github.truenine.composeserver.testtoolkit.log
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * # Java NIO Path 扩展函数测试
 *
 * 测试 Path 相关的扩展函数，包括文件行数统计、行切片、分页等功能
 */
class JavaNioPathFnsTest {
  @TempDirMapping lateinit var tempDir: Path

  @Test
  fun `测试切片行功能 - 验证首行处理`() {
    val firstLineFile = tempDir.resolve("firstLine.txt")

    val firstLine = "\nLine 1\nLine 2\nLine 3\nLine 4\n"
    File(firstLineFile.toUri()).writeText(firstLine)
    val lines = firstLineFile.countLines()

    log.info("测试文本长度: {}", firstLine.length)
    log.info("行数: {}", lines)

    val result = firstLineFile.sliceLines(sep = "\n", range = 0L..firstLine.length)
    val listResult = result.toList()
    log.info("切片结果: {}", listResult)
  }

  @Test
  fun `测试切片行功能 - 验证多行文本处理`() {
    val tempFile = tempDir.resolve("temper.txt")
    val text = "Line 1\nLine 2\nLine 3\nLine 4"
    log.info("文本长度: {}", text.length)

    File(tempFile.toUri()).writeText(text)

    val result = tempFile.sliceLines(sep = "\n", range = 0L..text.length)
    val listResult = result.toList()

    log.info("行切片结果: {}", listResult)

    assertEquals(4, listResult.size, "行数应该为 4")
    assertEquals("Line 1", listResult[0], "第一行应该是 'Line 1'")
    assertEquals("Line 2", listResult[1], "第二行应该是 'Line 2'")
    assertEquals("Line 3", listResult[2], "第三行应该是 'Line 3'")
    assertEquals("Line 4", listResult[3], "第四行应该是 'Line 4'")
  }

  @Test
  fun `测试行数统计功能 - 验证不同情况下的行数计算`() {
    val tempFile = File.createTempFile("test count lines", ".txt")
    tempFile.deleteOnExit()
    tempFile.writeBytes("Hello\nWorld\nThis\nis\na\nTest\ne".toByteArray())

    val testPath = tempFile.toPath()
    val actualLines = testPath.countLines()
    assertEquals(7, actualLines, "统计的行数应该与预期值匹配")

    tempFile.writeText("")
    val emptyLines = testPath.countLines()
    assertEquals(0, emptyLines, "空文件的行数应该为 0")

    tempFile.writeText("he\n")
    val oneLines = testPath.countLines()
    assertEquals(1, oneLines, "单行文件的行数应该为 1")

    tempFile.writeText("a\nb")
    val twoLines = testPath.countLines()
    assertEquals(2, twoLines, "两行文件的行数应该为 2")
  }

  @Test
  fun `测试分页行功能 - 验证文件内容分页读取`() {
    val tempFile = File.createTempFile("test page lines", ".txt")
    tempFile.deleteOnExit()
    tempFile.writeText("Hello\nWorld\nThis\nis\na\nTest\ne")
    log.info("临时文件: {}", tempFile)
    log.info("文件是否存在: {}", tempFile.exists())
    val testPath = tempFile.toPath()
    log.info("文件行数: {}", testPath.countLines())

    val pre = testPath.pageLines(Pq[1, 4], "\n")

    log.info("分页结果: {}", pre)

    assertEquals(7, pre.t, "总行数应该为 7")
    assertEquals(3, pre.d.size, "当前页数据大小应该为 3")
    assertEquals("a", pre[0], "第一个元素应该是 'a'")
    assertEquals(2, pre.p, "页码应该为 2")

    val pr1 = testPath.pageLines(Pq[3, 2], "\n")

    assertEquals(7, pr1.t, "总行数应该为 7")
    assertEquals("e", pr1[0], "第一个元素应该是 'e'")
    assertEquals(4, pr1.p, "页码应该为 4")
  }
}
