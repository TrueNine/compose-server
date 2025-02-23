import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  id("java-convention")
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = libs.versions.spring.cloud.asProvider().get()
extra["springAiVersion"] = libs.versions.spring.ai.get()

dependencies {
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)
  annotationProcessor(libs.org.springframework.spring.boot.configuration.processor)
}
