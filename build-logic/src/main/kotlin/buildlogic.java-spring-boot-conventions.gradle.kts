val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("buildlogic.java-conventions")
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = libs.versions.org.springframework.cloud.get()
extra["springAiVersion"] = libs.versions.org.springframework.ai.get()

dependencies {
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)
  annotationProcessor(libs.org.springframework.boot.spring.boot.configuration.processor)
}
