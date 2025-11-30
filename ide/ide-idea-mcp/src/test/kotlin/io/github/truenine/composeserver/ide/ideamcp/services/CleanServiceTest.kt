package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.github.truenine.composeserver.ide.ideamcp.tools.CleanOperation
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

/** CleanService unit tests. */
class CleanServiceTest {

  private val project = mockk<Project>()
  private val fileManager = mockk<FileManager>()
  private val cleanService = TestableCleanServiceImpl(project, fileManager)

  @Test
  fun `cleanCode should process a single file`() = runBlocking {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val options = CleanOptions(formatCode = true, optimizeImports = true, runInspections = false)

    every { virtualFile.isDirectory } returns false
    every { virtualFile.path } returns "/test/file.kt"
    every { virtualFile.extension } returns "kt"
    every { fileManager.collectFilesRecursively(any(), any()) } returns emptyList()

    // When
    val result = cleanService.cleanCode(project, virtualFile, options)

    // Then
    assertEquals(1, result.processedFiles) // Single file
    assertTrue(result.executionTime >= 0)
    assertTrue(result.summary.contains("Processed"))
  }

  @Test
  fun `cleanCode should process directory`() = runBlocking {
    // Given
    val project = mockk<Project>()
    val directory = mockk<VirtualFile>()
    val file1 = mockk<VirtualFile>()
    val file2 = mockk<VirtualFile>()
    val options = CleanOptions()

    every { directory.isDirectory } returns true
    every { directory.path } returns "/test"
    every { file1.isDirectory } returns false
    every { file1.extension } returns "kt"
    every { file1.path } returns "/test/file1.kt"
    every { file2.isDirectory } returns false
    every { file2.extension } returns "java"
    every { file2.path } returns "/test/file2.java"

    every { fileManager.collectFilesRecursively(directory, any()) } returns listOf(file1, file2)

    // When
    val result = cleanService.cleanCode(project, directory, options)

    // Then
    assertEquals(2, result.processedFiles)
    assertTrue(result.executionTime >= 0)
    verify { fileManager.collectFilesRecursively(directory, any()) }
  }

  @Test
  fun `CleanOptions default values should be correct`() {
    // Given
    val options = CleanOptions()

    // Then
    assertTrue(options.formatCode)
    assertTrue(options.optimizeImports)
    assertTrue(options.runInspections)
    assertFalse(options.rearrangeCode)
  }

  @Test
  fun `CleanOptions custom values should be set correctly`() {
    // Given
    val options = CleanOptions(formatCode = false, optimizeImports = true, runInspections = false, rearrangeCode = true)

    // Then
    assertFalse(options.formatCode)
    assertTrue(options.optimizeImports)
    assertFalse(options.runInspections)
    assertTrue(options.rearrangeCode)
  }

  @Test
  fun `CleanResult should contain correct information`() {
    // Given
    val operations = listOf(CleanOperation("FORMAT", "Code formatting", 3), CleanOperation("OPTIMIZE_IMPORTS", "Optimize imports", 2))

    val result =
      CleanResult(
        processedFiles = 5,
        modifiedFiles = 3,
        operations = operations,
        errors = listOf("File lock error"),
        summary = "Processing completed",
        executionTime = 1000L,
      )

    // Then
    assertEquals(5, result.processedFiles)
    assertEquals(3, result.modifiedFiles)
    assertEquals(2, result.operations.size)
    assertEquals(1, result.errors.size)
    assertEquals("Processing completed", result.summary)
    assertEquals(1000L, result.executionTime)
  }

  @Test
  fun `isCodeFile should recognize code files correctly`() {
    // Given
    val ktFile = mockk<VirtualFile>()
    val javaFile = mockk<VirtualFile>()
    val txtFile = mockk<VirtualFile>()
    val directory = mockk<VirtualFile>()

    every { ktFile.isDirectory } returns false
    every { ktFile.extension } returns "kt"
    every { javaFile.isDirectory } returns false
    every { javaFile.extension } returns "java"
    every { txtFile.isDirectory } returns false
    every { txtFile.extension } returns "txt"
    every { directory.isDirectory } returns true

    // When & Then
    assertTrue(cleanService.isCodeFile(ktFile))
    assertTrue(cleanService.isCodeFile(javaFile))
    assertFalse(cleanService.isCodeFile(txtFile))
    assertFalse(cleanService.isCodeFile(directory))
  }

  @Test
  fun `collectFilesToProcess should collect files correctly`() = runBlocking {
    // Given
    val project = mockk<Project>()
    val directory = mockk<VirtualFile>()
    val codeFile = mockk<VirtualFile>()
    val nonCodeFile = mockk<VirtualFile>()

    every { directory.isDirectory } returns true
    every { codeFile.isDirectory } returns false
    every { codeFile.extension } returns "kt"
    every { nonCodeFile.isDirectory } returns false
    every { nonCodeFile.extension } returns "txt"

    every { fileManager.collectFilesRecursively(directory, any()) } returns listOf(codeFile, nonCodeFile)

    // When
    cleanService.collectFilesToProcess(project, directory)

    // Then
    // Since filtering happens inside the lambda passed to collectFilesRecursively,
    // this test focuses on verifying that the method is invoked.
    verify { fileManager.collectFilesRecursively(directory, any()) }
  }

  @Test
  fun `createSummary should generate correct summary`() {
    // Given
    val operations = listOf(CleanOperation("FORMAT", "Code formatting", 5), CleanOperation("OPTIMIZE_IMPORTS", "Optimize imports", 3))
    val errors = listOf("Error1", "Error2")

    // When
    val summary = cleanService.createSummary(10, 8, operations, errors)

    // Then
    assertTrue(summary.contains("Processed 10 files"))
    assertTrue(summary.contains("modified 8 files"))
    assertTrue(summary.contains("Code formatting: 5 files"))
    assertTrue(summary.contains("Optimize imports: 3 files"))
    assertTrue(summary.contains("Encountered 2 errors"))
  }

  @Test
  fun `createSummary should work correctly when no files modified`() {
    // Given
    val operations = emptyList<CleanOperation>()
    val errors = emptyList<String>()

    // When
    val summary = cleanService.createSummary(5, 0, operations, errors)

    // Then
    assertTrue(summary.contains("Processed 5 files"))
    assertFalse(summary.contains("modified"))
    assertFalse(summary.contains("Operations performed"))
    assertFalse(summary.contains("Encountered"))
  }

  @Test
  fun `updateOperationCount should update operation count correctly`() {
    // Given
    val operations = mutableListOf<CleanOperation>()

    // When - first time adding operation
    cleanService.updateOperationCount(operations, "FORMAT", "Code formatting")

    // Then
    assertEquals(1, operations.size)
    assertEquals("FORMAT", operations[0].type)
    assertEquals("Code formatting", operations[0].description)
    assertEquals(1, operations[0].filesAffected)

    // When - add the same operation type again
    cleanService.updateOperationCount(operations, "FORMAT", "Code formatting")

    // Then
    assertEquals(1, operations.size) // Still only one operation
    assertEquals(2, operations[0].filesAffected) // But the count increased
  }
}

/** Testable CleanService implementation exposing protected methods for testing. */
private class TestableCleanServiceImpl(project: Project, private val testFileManager: FileManager) : CleanServiceImpl(project) {

  override val fileManager: FileManager
    get() = testFileManager

  public override fun isCodeFile(file: VirtualFile): Boolean = super.isCodeFile(file)

  public override fun collectFilesToProcess(project: Project, virtualFile: VirtualFile): List<VirtualFile> = super.collectFilesToProcess(project, virtualFile)

  public override fun createSummary(processedFiles: Int, modifiedFiles: Int, operations: List<CleanOperation>, errors: List<String>): String =
    super.createSummary(processedFiles, modifiedFiles, operations, errors)

  public override fun updateOperationCount(operations: MutableList<CleanOperation>, type: String, description: String) =
    super.updateOperationCount(operations, type, description)
}
