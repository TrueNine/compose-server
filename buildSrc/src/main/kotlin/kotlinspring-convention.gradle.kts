import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  id("javaspring-convention")
  id("kotlin-convention")
  kotlin("plugin.spring")
}

dependencies {
  annotationProcessor(libs.org.springframework.spring.boot.configuration.processor)
}

