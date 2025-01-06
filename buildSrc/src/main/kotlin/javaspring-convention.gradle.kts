import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  id("java-convention")
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = libs.versions.springCloud.get()
extra["springAiVersion"] = libs.versions.springAi.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  annotationProcessor(libs.org.springframework.springBootConfigurationProcessor)
}
