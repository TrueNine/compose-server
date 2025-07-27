package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.TempDirMapping
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*

/**
 * Comprehensive test suite for Java NIO Path extension functions.
 *
 * Tests all Path-related extension functions including file operations, line counting, slicing, pagination, and various edge cases.
 */
class JavaNioPathFnsTest {
  @TempDirMapping lateinit var tempDir: Path

  // ========== Path.isFile() Tests ==========

  @Test
  fun testIsFileWithRegularFile() {
    val testFile = tempDir.resolve("regular.txt")
    Files.createFile(testFile)

    assertTrue(testFile.isFile(), "Regular file should return true")
  }

  @Test
  fun testIsFileWithDirectory() {
    val testDir = tempDir.resolve("testdir")
    Files.createDirectory(testDir)

    assertFalse(testDir.isFile(), "Directory should return false")
  }

  @Test
  fun testIsFileWithNonExistentPath() {
    val nonExistent = tempDir.resolve("nonexistent.txt")

    assertFalse(nonExistent.isFile(), "Non-existent path should return false")
  }

  // ========== Path.isEmpty() Tests ==========

  @Test
  fun testIsEmptyWithEmptyFile() {
    val emptyFile = tempDir.resolve("empty.txt")
    Files.createFile(emptyFile)

    assertTrue(emptyFile.isEmpty(), "Empty file should return true")
  }

  @Test
  fun testIsEmptyWithNonEmptyFile() {
    val nonEmptyFile = tempDir.resolve("nonempty.txt")
    Files.write(nonEmptyFile, "content".toByteArray())

    assertFalse(nonEmptyFile.isEmpty(), "Non-empty file should return false")
  }

  @Test
  fun testIsEmptyWithDirectory() {
    val testDir = tempDir.resolve("testdir")
    Files.createDirectory(testDir)

    assertTrue(testDir.isEmpty(), "Directory should return true")
  }

  @Test
  fun testIsEmptyWithNonExistentPath() {
    val nonExistent = tempDir.resolve("nonexistent.txt")

    assertTrue(nonExistent.isEmpty(), "Non-existent path should return true")
  }

  // ========== Path.fileChannel() Tests ==========

  @Test
  fun testFileChannelWithValidFile() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "test content".toByteArray())

    testFile.fileChannel().use { channel ->
      assertNotNull(channel, "FileChannel should not be null")
      assertTrue(channel.isOpen, "FileChannel should be open")
    }
  }

  @Test
  fun testFileChannelWithReadWriteMode() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "test content".toByteArray())

    testFile.fileChannel("rw").use { channel ->
      assertNotNull(channel, "FileChannel should not be null")
      assertTrue(channel.isOpen, "FileChannel should be open")
    }
  }

  @Test
  fun testFileChannelWithDirectory() {
    val testDir = tempDir.resolve("testdir")
    Files.createDirectory(testDir)

    assertFailsWith<FileNotFoundException> { testDir.fileChannel() }
  }

  @Test
  fun testFileChannelWithNonExistentFile() {
    val nonExistent = tempDir.resolve("nonexistent.txt")

    assertFailsWith<FileNotFoundException> { nonExistent.fileChannel() }
  }

  // ========== Path.fileSize() Tests ==========

  @Test
  fun testFileSizeWithEmptyFile() {
    val emptyFile = tempDir.resolve("empty.txt")
    Files.createFile(emptyFile)

    assertEquals(0L, emptyFile.fileSize(), "Empty file size should be 0")
  }

  @Test
  fun testFileSizeWithNonEmptyFile() {
    val content = "Hello, World!"
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, content.toByteArray())

    assertEquals(content.length.toLong(), testFile.fileSize(), "File size should match content length")
  }

  @Test
  fun testFileSizeWithDirectory() {
    val testDir = tempDir.resolve("testdir")
    Files.createDirectory(testDir)

    assertEquals(0L, testDir.fileSize(), "Directory size should return 0")
  }

  // ========== Path.countLines() Tests ==========

  @Test
  fun testCountLinesWithEmptyFile() {
    val emptyFile = tempDir.resolve("empty.txt")
    Files.createFile(emptyFile)

    assertEquals(0L, emptyFile.countLines(), "Empty file should have 0 lines")
  }

  @Test
  fun testCountLinesWithSingleLine() {
    val singleLineFile = tempDir.resolve("single.txt")
    Files.write(singleLineFile, "single line".toByteArray())

    assertEquals(1L, singleLineFile.countLines(), "Single line file should have 1 line")
  }

  @Test
  fun testCountLinesWithMultipleLines() {
    val multiLineFile = tempDir.resolve("multi.txt")
    Files.write(multiLineFile, "Line 1\nLine 2\nLine 3".toByteArray())

    assertEquals(3L, multiLineFile.countLines(), "Multi-line file should have correct line count")
  }

  @Test
  fun testCountLinesWithTrailingNewline() {
    val trailingNewlineFile = tempDir.resolve("trailing.txt")
    Files.write(trailingNewlineFile, "Line 1\nLine 2\n".toByteArray())

    assertEquals(2L, trailingNewlineFile.countLines(), "File with trailing newline should count correctly")
  }

  @Test
  fun testCountLinesWithDifferentLineEndings() {
    val windowsFile = tempDir.resolve("windows.txt")
    Files.write(windowsFile, "Line 1\r\nLine 2\r\nLine 3".toByteArray())

    assertEquals(3L, windowsFile.countLines(), "Windows line endings should be counted correctly")
  }

  // ========== Path.countWordBySeparator() Tests ==========

  @Test
  fun testCountWordBySeparatorWithEmptyFile() {
    val emptyFile = tempDir.resolve("empty.txt")
    Files.createFile(emptyFile)

    val result = emptyFile.countWordBySeparator().toList()
    assertTrue(result.isEmpty(), "Empty file should return empty sequence")
  }

  @Test
  fun testCountWordBySeparatorWithDefaultSeparator() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "Line 1\nLine 2\nLine 3".toByteArray())

    val result = testFile.countWordBySeparator().toList()
    assertTrue(result.isNotEmpty(), "File with lines should return non-empty sequence")
  }

  @Test
  fun testCountWordBySeparatorWithCustomSeparator() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "word1,word2,word3".toByteArray())

    val result = testFile.countWordBySeparator(",").toList()
    assertTrue(result.isNotEmpty(), "File with custom separator should return non-empty sequence")
  }

  @Test
  fun testCountWordBySeparatorWithEmptySeparator() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "content".toByteArray())

    assertFailsWith<IllegalStateException> { testFile.countWordBySeparator("").toList() }
  }

  @Test
  fun testCountWordBySeparatorWithDifferentCharsets() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "测试\n内容".toByteArray(Charsets.UTF_8))

    val result = testFile.countWordBySeparator(charset = Charsets.UTF_8).toList()
    assertTrue(result.isNotEmpty(), "File with UTF-8 content should work correctly")
  }

  // ========== Path.sliceLines() Tests ==========

  @Test
  fun testSliceLinesWithEmptyFile() {
    val emptyFile = tempDir.resolve("empty.txt")
    Files.createFile(emptyFile)

    val result = emptyFile.sliceLines(0L..10L).toList()
    assertTrue(result.isEmpty(), "Empty file should return empty sequence")
  }

  @Test
  fun testSliceLinesWithValidRange() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "Line 1\nLine 2\nLine 3\nLine 4".toByteArray())

    val result = testFile.sliceLines(0L..2L).toList()
    assertTrue(result.isNotEmpty(), "Valid range should return non-empty sequence")
  }

  @Test
  fun testSliceLinesWithCustomSeparator() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "Part1|Part2|Part3".toByteArray())

    val result = testFile.sliceLines(0L..2L, sep = "|").toList()
    assertTrue(result.isNotEmpty(), "Custom separator should work correctly")
  }

  @Test
  fun testSliceLinesWithDifferentCharsets() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "测试1\n测试2".toByteArray(Charsets.UTF_8))

    val result = testFile.sliceLines(0L..1L, charset = Charsets.UTF_8).toList()
    assertTrue(result.isNotEmpty(), "Different charset should work correctly")
  }

  @Test
  fun testSliceLinesWithProvidedTotalLines() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "Line 1\nLine 2\nLine 3".toByteArray())

    val result = testFile.sliceLines(0L..1L, totalLines = 3L).toList()
    assertTrue(result.isNotEmpty(), "Provided total lines should work correctly")
  }

  // ========== Path.pageLines() Tests ==========

  @Test
  fun testPageLinesWithEmptyFile() {
    val emptyFile = tempDir.resolve("empty.txt")
    Files.createFile(emptyFile)

    val result = emptyFile.pageLines(Pq[0, 10])
    assertEquals(0L, result.t, "Empty file should have 0 total")
    assertTrue(result.d.isEmpty(), "Empty file should have empty data")
  }

  @Test
  fun testPageLinesWithValidPagination() {
    val testFile = tempDir.resolve("test.txt")
    // Use system line separator to match the default behavior
    val content = "Line 1${System.lineSeparator()}Line 2${System.lineSeparator()}Line 3${System.lineSeparator()}Line 4${System.lineSeparator()}Line 5"
    Files.write(testFile, content.toByteArray())

    val result = testFile.pageLines(Pq[0, 2])
    assertEquals(5L, result.t, "Total should match line count")
    assertEquals(2, result.d.size, "Page size should match request")
    assertEquals("Line 1", result[0], "First line should match")
    assertEquals("Line 2", result[1], "Second line should match")
  }

  @Test
  fun testPageLinesWithSingleLine() {
    val testFile = tempDir.resolve("test.txt")
    Files.write(testFile, "content".toByteArray())

    val result = testFile.pageLines(Pq[0, 10])
    assertEquals(1L, result.t, "Single line file should return 1 line")
    assertEquals(1, result.d.size, "Should return single line")
    assertEquals("content", result[0], "Content should match")
  }

  @Test
  fun testPageLinesWithCustomCharset() {
    val testFile = tempDir.resolve("test.txt")
    val content = "测试1\n测试2\n测试3"
    Files.write(testFile, content.toByteArray(Charsets.UTF_8))

    val result = testFile.pageLines(Pq[0, 2], charset = Charsets.UTF_8)
    assertTrue(result.t > 0, "Custom charset should work")
    assertTrue(result.d.isNotEmpty(), "Should return non-empty data")
  }
}
