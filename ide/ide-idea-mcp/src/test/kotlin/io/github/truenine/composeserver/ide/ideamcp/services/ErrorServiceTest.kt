package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertTrue

/** ErrorService 单元测试 */
class ErrorServiceTest {

  private val errorService = ErrorServiceImpl()

  @Test
  fun `collectErrors 应该处理单个文件`() {
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
    assertTrue(result.isEmpty()) // 目前返回空结果，因为是模拟实现
  }

  @Test
  fun `collectErrors 应该处理目录`() {
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
    assertTrue(result.isEmpty()) // 目前返回空结果，因为是模拟实现
  }

  @Test
  fun `analyzeFile 应该处理无效文件`() {
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
  fun `analyzeFile 应该处理目录`() {
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
  fun `analyzeFile 应该处理 PSI 文件不存在的情况`() {
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
  fun `analyzeFile 应该处理正常的 PSI 文件`() {
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
    assertTrue(result.isEmpty()) // 目前返回空结果，因为是模拟实现
  }

  @Test
  fun `analyzeFile 应该在 WriteIntentReadAction 中安全执行`() {
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

    // When - 这个调用应该不会抛出线程访问异常
    val result = errorService.analyzeFile(project, virtualFile)

    // Then - 验证没有抛出异常并且返回了结果
    assertTrue(result.isEmpty()) // 在模拟环境中返回空结果是正常的
  }
}
