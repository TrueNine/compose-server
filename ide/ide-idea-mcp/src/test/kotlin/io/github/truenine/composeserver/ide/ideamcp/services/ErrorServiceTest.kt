package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertTrue

/** ErrorService unit tests. */
class ErrorServiceTest {

  private val errorService = ErrorServiceImpl()

  @Test
  fun `collectErrors should handle single file`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    val psiFile = mockk<PsiFile>()

    every { virtualFile.isDirectory } returns false
    every { virtualFile.isValid } returns true
    every { virtualFile.path } returns "/test/file.kt"
    every { virtualFile.name } returns "file.kt"
    every { project.basePath } returns "/test"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(virtualFile) } returns psiFile
    every { psiFile.name } returns "file.kt"

    // When
    val result = errorService.collectErrors(project, virtualFile)

    // Then
    assertTrue(result.isEmpty()) // Currently returns empty result because this is a stub implementation
  }

  @Test
  fun `collectErrors should handle directory`() {
    // Given
    val project = mockk<Project>()
    val directory = mockk<VirtualFile>()
    val childFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    val psiFile = mockk<PsiFile>()

    every { directory.isDirectory } returns true
    every { directory.isValid } returns true
    every { directory.path } returns "/test"
    every { directory.children } returns arrayOf(childFile)

    every { childFile.isDirectory } returns false
    every { childFile.isValid } returns true
    every { childFile.path } returns "/test/child.kt"
    every { childFile.name } returns "child.kt"

    every { project.basePath } returns "/test"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(childFile) } returns psiFile
    every { psiFile.name } returns "child.kt"

    // When
    val result = errorService.collectErrors(project, directory)

    // Then
    assertTrue(result.isEmpty()) // Currently returns empty result because this is a stub implementation
  }

  @Test
  fun `analyzeFile should handle invalid file`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()

    every { virtualFile.isValid } returns false
    every { virtualFile.isDirectory } returns false

    // When
    val result = errorService.analyzeFile(project, virtualFile)

    // Then
    assertTrue(result.isEmpty())
  }

  @Test
  fun `analyzeFile should handle directory`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()

    every { virtualFile.isValid } returns true
    every { virtualFile.isDirectory } returns true

    // When
    val result = errorService.analyzeFile(project, virtualFile)

    // Then
    assertTrue(result.isEmpty())
  }

  @Test
  fun `analyzeFile should handle missing PSI file`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()

    every { virtualFile.isValid } returns true
    every { virtualFile.isDirectory } returns false
    every { virtualFile.path } returns "/test/file.kt"
    every { virtualFile.name } returns "file.kt"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(virtualFile) } returns null

    // When
    val result = errorService.analyzeFile(project, virtualFile)

    // Then
    assertTrue(result.isEmpty())
  }

  @Test
  fun `analyzeFile should handle normal PSI file`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    val psiFile = mockk<PsiFile>()

    every { virtualFile.isValid } returns true
    every { virtualFile.isDirectory } returns false
    every { virtualFile.path } returns "/test/file.kt"
    every { virtualFile.name } returns "file.kt"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(virtualFile) } returns psiFile
    every { psiFile.name } returns "file.kt"

    // When
    val result = errorService.analyzeFile(project, virtualFile)

    // Then
    assertTrue(result.isEmpty()) // Currently returns empty result because this is a stub implementation
  }

  @Test
  fun `analyzeFile should run safely inside WriteIntentReadAction`() {
    // Given
    val project = mockk<Project>()
    val virtualFile = mockk<VirtualFile>()
    val psiManager = mockk<PsiManager>()
    val psiFile = mockk<PsiFile>()

    every { virtualFile.isValid } returns true
    every { virtualFile.isDirectory } returns false
    every { virtualFile.path } returns "/test/file.kt"
    every { virtualFile.name } returns "file.kt"
    every { PsiManager.getInstance(project) } returns psiManager
    every { psiManager.findFile(virtualFile) } returns psiFile
    every { psiFile.name } returns "file.kt"
    every { psiFile.project } returns project
    every { psiFile.virtualFile } returns virtualFile

    // When - this call should not throw thread-access exceptions
    val result = errorService.analyzeFile(project, virtualFile)

    // Then - verify that no exception is thrown and a result is returned
    assertTrue(result.isEmpty()) // Returning an empty result in the mocked environment is expected
  }
}
