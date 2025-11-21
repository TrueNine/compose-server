package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeMetadata
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType

/** ViewLibCodeAction tests verifying library-code context-menu behavior. */
class ViewLibCodeActionTest : BasePlatformTestCase() {

  fun testActionCreation() {
    // Verify that the action can be created
    val action = ViewLibCodeAction()
    assertNotNull(action)
    assertEquals("View Library Code", action.templateText)
  }

  fun testLibCodeDialogCreation() {
    // Verify that the library code dialog can be created
    val metadata = LibCodeMetadata(libraryName = "test-lib", version = "1.0", sourceType = SourceType.SOURCE_JAR, documentation = null)

    val result =
      LibCodeResult(
        sourceCode = "", // Use empty string to avoid creating an editor
        isDecompiled = false,
        language = "java",
        metadata = metadata,
      )

    val dialog = LibCodeDialog(project, result, "TestClass")
    assertNotNull(dialog)
    assertEquals("Library code - TestClass", dialog.title)
  }

  fun testEditorResourceManagement() {
    // Verify editor resource management
    val metadata = LibCodeMetadata(libraryName = "test-lib", version = "1.0", sourceType = SourceType.SOURCE_JAR, documentation = null)

    val result = LibCodeResult(sourceCode = "public class TestClass { }", isDecompiled = false, language = "java", metadata = metadata)

    // Verify that dialog creation and disposal do not leak editors
    val dialog = LibCodeDialog(project, result, "TestClass")
    assertNotNull(dialog)

    // Use reflection to access dispose method for cleanup test
    try {
      val disposeMethod = dialog.javaClass.superclass.getDeclaredMethod("dispose")
      disposeMethod.isAccessible = true
      disposeMethod.invoke(dialog)
      // If no exception is thrown, dispose works as expected
    } catch (e: Exception) {
      fail("dispose method invocation failed: ${e.message}")
    }
  }
}
