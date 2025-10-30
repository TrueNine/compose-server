package io.github.truenine.composeserver.gradleplugin.jar

import io.github.truenine.composeserver.gradleplugin.emptyVersion
import org.gradle.api.Project

open class JarExtensionConfig(project: Project) {
  var enabled: Boolean = false
  var bootJarDistName: String = "lib"
  var bootJarConfigName: String = "config"
  var bootJarName: String = project.name
  var defaultVersion: String = "1.0"
  var jarDistDir = "libs"
  var bootJarVersion: String = project.emptyVersion.ifEmpty { defaultVersion }
  var bootJarSeparate: Boolean = false
  var bootJarClassifier: String = "boot"
  var copyLicense: Boolean = true
}
