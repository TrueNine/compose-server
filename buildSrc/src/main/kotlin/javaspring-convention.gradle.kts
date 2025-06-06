import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  id("java-convention")
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = libs.versions.org.springframework.cloud.get()
extra["springAiVersion"] = libs.versions.org.springframework.ai.get()

dependencies {
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)
  annotationProcessor(libs.org.springframework.spring.boot.configuration.processor)
}
