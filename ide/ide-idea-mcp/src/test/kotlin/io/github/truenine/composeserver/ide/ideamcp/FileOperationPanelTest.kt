package io.github.truenine.composeserver.ide.ideamcp

import kotlin.test.*

/** FileOperationPanel unit tests verifying basic behavior of the file operation panel. */
class FileOperationPanelTest {

  @Test
  fun `file operation panel should initialize correctly`() {
    // Because FileOperationPanel depends on the IDEA project environment, we only test basic class loading here.
    val panelClass = FileOperationPanel::class.java
    assertNotNull(panelClass, "FileOperationPanel class should exist")
    assertTrue(panelClass.name.contains("FileOperationPanel"), "Class name should contain FileOperationPanel")
  }

  @Test
  fun `file operation panel should have correct package name`() {
    val panelClass = FileOperationPanel::class.java
    assertEquals("io.github.truenine.composeserver.ide.ideamcp", panelClass.packageName, "Package name should be correct")
  }

  @Test
  fun `file operation panel should extend correct superclass`() {
    val panelClass = FileOperationPanel::class.java
    val superClass = panelClass.superclass
    assertNotNull(superClass, "Superclass should not be null")
    assertTrue(superClass.name.contains("SimpleToolWindowPanel"), "Should extend SimpleToolWindowPanel")
  }

  @Test
  fun `file operation panel should have required constructor`() {
    val panelClass = FileOperationPanel::class.java
    val constructors = panelClass.constructors
    assertTrue(constructors.isNotEmpty(), "Constructor list should not be empty")

    val primaryConstructor = constructors.first()
    assertEquals(1, primaryConstructor.parameterCount, "Primary constructor should have one parameter")

    val parameterType = primaryConstructor.parameterTypes.first()
    assertTrue(parameterType.name.contains("Project"), "Constructor parameter should be of Project type")
  }
}
