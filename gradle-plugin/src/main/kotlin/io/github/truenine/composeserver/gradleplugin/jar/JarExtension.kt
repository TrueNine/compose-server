package io.github.truenine.composeserver.gradleplugin.jar

import io.github.truenine.composeserver.gradleplugin.consts.Constant
import io.github.truenine.composeserver.gradleplugin.wrap
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.springframework.boot.gradle.tasks.bundling.BootJar

class JarExtension(private val project: Project, private val dsl: JarExtensionConfig) {
  init {
    if (null != project.tasks.findByName("jar") && dsl.copyLicense) {
      jarCopyLicense()
    }

    if (null != project.tasks.findByName("bootJar") && null != project.configurations.findByName("runtimeClasspath") && dsl.bootJarSeparate) {
      springBootJarSeparate()
    }
  }

  private fun springBootJarSeparate() =
    project.wrap {
      extra["snippetsDir"] = file("build/generated-snippets")

      val runtimeClasspath = configurations.named<org.gradle.api.artifacts.Configuration>("runtimeClasspath")

      // 获取 developmentOnly 配置中的依赖，用于在复制时排除
      val developmentOnlyDependencies = configurations.findByName("developmentOnly")?.resolvedConfiguration?.resolvedArtifacts?.map { it.file } ?: emptyList()

      val cleanTask =
        tasks.register<Delete>(BOOT_JAR_CLEAN_TASK_NAME) {
          group = Constant.TASK_GROUP
          delete("${layout.buildDirectory.get().asFile.absolutePath}/${dsl.jarDistDir}/${dsl.bootJarDistName}".replace("//", "/"))
          delete("${layout.buildDirectory.get().asFile.absolutePath}/${dsl.jarDistDir}/${dsl.bootJarConfigName}".replace("//", "/"))
        }

      val copyLibTask =
        tasks.register<Copy>(BOOT_JAR_COPY_LIB_TASK_NAME) {
          group = Constant.TASK_GROUP
          into(
            listOf(layout.buildDirectory.get().asFile.absolutePath, dsl.jarDistDir, dsl.bootJarDistName)
              .filter(String::isNotEmpty)
              .joinToString(separator = "/")
          )
          from(runtimeClasspath) {
            // 排除 developmentOnly 依赖
            exclude { fileTreeElement -> developmentOnlyDependencies.any { devDep -> fileTreeElement.file.absolutePath == devDep.absolutePath } }
          }
        }

      val copyConfigTask =
        tasks.register<Copy>(BOOT_JAR_COPY_CONFIG_TASK_NAME) {
          group = Constant.TASK_GROUP
          into(
            listOf(layout.buildDirectory.get().asFile.absolutePath, dsl.jarDistDir, dsl.bootJarConfigName)
              .filter(String::isNotEmpty)
              .joinToString(separator = "/")
          )
          from(mainResources!!)
        }

      tasks.withType(BootJar::class.java).configureEach { bootJar ->
        bootJar.archiveClassifier.set(dsl.bootJarClassifier)
        bootJar.archiveVersion.set(dsl.bootJarVersion)
        bootJar.archiveBaseName.set(dsl.bootJarName)
        bootJar.excludes.add("*.jar")

        bootJar.dependsOn(cleanTask)
        bootJar.dependsOn(copyLibTask)
        bootJar.dependsOn(copyConfigTask)

        bootJar.manifest {
          // 过滤掉 developmentOnly 依赖后生成 Class-Path
          val filteredClassPath =
            runtimeClasspath
              .get()
              .filter { file -> developmentOnlyDependencies.none { devDep -> file.absolutePath == devDep.absolutePath } }
              .joinToString(" ") { f -> "${dsl.bootJarDistName}/${f.name}" }

          it.attributes(mutableMapOf("Manifest-Version" to "1.0", "Class-Path" to filteredClassPath))
        }
      }
    }

  private fun jarCopyLicense() {
    project.rootProject.layout.projectDirectory.asFileTree
      .firstOrNull { file -> Constant.FileNameSet.LICENSE.any { dName -> dName.equals(file.name, ignoreCase = true) } }
      ?.also { licenseFile ->
        project.tasks.withType(Jar::class.java).configureEach { jarTask -> jarTask.from(licenseFile.absolutePath) { it.include(licenseFile.name) } }
      }
  }

  companion object {
    const val BOOT_JAR_CLEAN_TASK_NAME = "composeBootJarClearLib"
    const val BOOT_JAR_COPY_LIB_TASK_NAME = "composeBootJarCopyLib"
    const val BOOT_JAR_COPY_CONFIG_TASK_NAME = "composeBootJarCopyConfig"
  }
}
