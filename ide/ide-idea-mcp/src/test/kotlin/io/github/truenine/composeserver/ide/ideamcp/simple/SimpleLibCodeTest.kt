package io.github.truenine.composeserver.ide.ideamcp.simple

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeServiceImpl
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/** Simple LibCodeService test verifying basic behavior. */
class SimpleLibCodeTest {

  @Test
  fun `simple test - service should work correctly`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // When
    val result = libCodeService.getLibraryCode(mockProject, "java.lang.String")

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertTrue(result.language.isNotEmpty())
    assertNotNull(result.metadata)

    println("✅ Simple test passed:")
    println("  Source length: ${result.sourceCode.length}")
    println("  Language: ${result.language}")
    println("  Library name: ${result.metadata.libraryName}")
  }
}
