package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** Actions integration tests verifying registration and basic behavior. */
class ActionsIntegrationTest : BasePlatformTestCase() {

  fun testAllActionsCanBeCreated() {
    // Verify that all actions can be created
    assertNotNull(CleanCodeAction())
    assertNotNull(ViewErrorAction())
    assertNotNull(ViewLibCodeAction())
  }

  fun testAllActionsExtendAnAction() {
    // Verify that all actions extend AnAction
    assertTrue(CleanCodeAction() is AnAction)
    assertTrue(ViewErrorAction() is AnAction)
    assertTrue(ViewLibCodeAction() is AnAction)
  }

  fun testActionTextsAreConsistent() {
    // Verify that action texts are consistent with definitions
    val cleanCodeAction = CleanCodeAction()
    val viewErrorAction = ViewErrorAction()
    val viewLibCodeAction = ViewLibCodeAction()

    assertEquals("Clean Code", cleanCodeAction.templateText)
    assertEquals("View Errors", viewErrorAction.templateText)
    assertEquals("View Library Code", viewLibCodeAction.templateText)
  }
}
