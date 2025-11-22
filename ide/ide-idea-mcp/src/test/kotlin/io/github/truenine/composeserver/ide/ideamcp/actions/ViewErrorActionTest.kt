package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** ViewErrorAction tests verifying error-view context-menu behavior. */
class ViewErrorActionTest : BasePlatformTestCase() {

  fun testActionCreation() {
    // Verify that the action can be created
    val action = ViewErrorAction()
    assertNotNull(action)
    assertEquals("View Errors", action.templateText)
  }

  fun testErrorViewOptionsDialogCreation() {
    // Verify that the error-view options dialog can be created
    val dialog = ErrorViewOptionsDialog(project)
    assertNotNull(dialog)
    assertEquals("Error view options", dialog.title)
  }
}
