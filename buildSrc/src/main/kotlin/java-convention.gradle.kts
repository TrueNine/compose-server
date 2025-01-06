import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  `java-library`
  idea
  id("publish-convention")
}

java {
  sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
  targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
  toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get())) }
  withSourcesJar()
}

sourceSets {
  test {
    resources {
      project.rootProject.layout.projectDirectory.also { rootDir ->
        srcDir(rootDir.dir("common-test-resources").asFile.absolutePath)
      }
    }
  }
}

tasks.test {
  useJUnitPlatform()
}

tasks.jar {
  archiveClassifier = ""
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

tasks.javadoc {
  enabled = false
}
