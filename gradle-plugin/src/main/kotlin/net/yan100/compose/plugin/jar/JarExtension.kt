/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.plugin.jar

import net.yan100.compose.plugin.consts.PluginConsts
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.springframework.boot.gradle.tasks.bundling.BootJar

class JarExtension(
  private val project: Project,
  private val dsl: JarExtensionConfig,
) {
  init {
    if (null != project.tasks.findByName("jar") && dsl.copyLicense) {
      jarCopyLicense()
    }
    if (null != project.tasks.findByName("bootJar") && dsl.bootJarSeparate) {
      springBootJarSeparate()
    }
  }

  private fun springBootJarSeparate() {
    val runtimeClasspath =
      project.configurations.named<org.gradle.api.artifacts.Configuration>("runtimeClasspath")

    val cleanTask =
      project.tasks.register<Delete>(BOOT_JAR_CLEAN_TASK_NAME) {
        group = PluginConsts.TASK_GROUP
        delete(
          "${project.layout.buildDirectory.get().asFile.absolutePath}/libs/${dsl.bootJarDistName}"
        )
        delete(
          "${project.layout.buildDirectory.get().asFile.absolutePath}/libs/${dsl.bootJarConfigName}"
        )
      }

    val copyLibTask =
      project.tasks.register<Copy>(BOOT_JAR_COPY_LIB_TASK_NAME) {
        group = PluginConsts.TASK_GROUP
        into(
          "${project.layout.buildDirectory.get().asFile.absolutePath}/libs/${dsl.bootJarDistName}"
        )
        from(runtimeClasspath)
      }

    val copyConfigTask =
      project.tasks.register<Copy>(BOOT_JAR_COPY_CONFIG_TASK_NAME) {
        group = PluginConsts.TASK_GROUP
        into(
          "${project.layout.buildDirectory.get().asFile.absolutePath}/libs/${dsl.bootJarConfigName}"
        )
        from(runtimeClasspath)
      }

    project.tasks.withType(BootJar::class.java).configureEach { bootJar ->
      bootJar.archiveClassifier.set(dsl.bootJarClassifier)
      bootJar.archiveVersion.set(dsl.bootJarVersion)
      bootJar.dependsOn(cleanTask)
      bootJar.dependsOn(copyLibTask)
      bootJar.dependsOn(copyConfigTask)

      bootJar.exclude("*.jar")
      bootJar.manifest {
        bootJar.archiveBaseName.set(dsl.bootJarName)
        bootJar.archiveVersion.set(dsl.bootJarVersion)

        it.attributes(
          mutableMapOf(
            "Manifest-Version" to "1.0",
            "Class-Path" to
              runtimeClasspath.get().files.joinToString(" ") { f -> "${dsl}/${f.name}" }
          )
        )
      }
    }
  }

  private fun jarCopyLicense() {
    project.rootProject.layout.projectDirectory.asFileTree
      .firstOrNull { file ->
        PluginConsts.LICENSE_FILE_NAMES.any { dName -> dName.equals(file.name, ignoreCase = true) }
      }
      ?.also { licenseFile ->
        project.tasks.withType(Jar::class.java).configureEach { jarTask ->
          jarTask.from(licenseFile.absolutePath) { it.include(licenseFile.name) }
        }
      }
  }

  companion object {
    const val BOOT_JAR_CLEAN_TASK_NAME = "bootJarClearLib"
    const val BOOT_JAR_COPY_LIB_TASK_NAME = "bootJarCopyLib"
    const val BOOT_JAR_COPY_CONFIG_TASK_NAME = "bootJarCopyConfig"
  }
}
