package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** CleanCodeAction tests verifying context-menu clean-up action behavior. */
class CleanCodeActionTest : BasePlatformTestCase() {

  fun testActionCreation() {
    // Verify that the action can be created
    val action = CleanCodeAction()
    assertNotNull(action)
    assertEquals("Clean Code", action.templateText)
  }

  fun testCleanOptionsDialogCreation() {
    // Verify that the clean-up options dialog can be created
    val dialog = CleanOptionsDialog(project)
    assertNotNull(dialog)
    assertEquals("Code clean-up options", dialog.title)
  }
}
