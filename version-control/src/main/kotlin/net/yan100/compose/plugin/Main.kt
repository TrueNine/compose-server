package net.yan100.compose.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class Main : Plugin<Project> {
  override fun apply(project: Project) {
  }

  companion object
}

public fun org.gradle.api.artifacts.dsl.DependencyHandler.allAnnotationCompileOnly(dependencyNotation: Any): org.gradle.api.artifacts.Dependency? {
  this.add("annotationProcessor", dependencyNotation)
  this.add("testCompileOnly", dependencyNotation)
  this.add("testAnnotationProcessor", dependencyNotation)
  return this.add("compileOnly", dependencyNotation)
}
