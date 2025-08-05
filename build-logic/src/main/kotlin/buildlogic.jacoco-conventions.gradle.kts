plugins {
  id("jacoco")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
  finalizedBy("jacocoTestReport")
}

tasks.withType<JacocoReport>().configureEach {
  dependsOn(tasks.withType<Test>())

  // 明确声明对Spotless任务的依赖关系以避免隐式依赖问题
  mustRunAfter(tasks.withType<com.diffplug.gradle.spotless.SpotlessTask>())

  reports {
    xml.required.set(false)
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
  executionData.setFrom(fileTree(project.layout.buildDirectory).include("**/jacoco/test.exec", "**/jacoco.exec"))
}
