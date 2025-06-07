import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension

plugins {
  id("jacoco")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  finalizedBy("jacocoTestReport")
}

tasks.withType<JacocoReport>().configureEach {
  dependsOn(tasks.withType<Test>())
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
  val mainSrc = listOf("src/main/java", "src/main/kotlin")
  classDirectories.setFrom(
    files(classDirectories.files.map {
      fileTree(it) {
        exclude("**/generated/**")
      }
    })
  )
  sourceDirectories.setFrom(files(mainSrc))
  executionData.setFrom(fileTree(buildDir).include("**/jacoco/test.exec", "**/jacoco.exec"))
} 
