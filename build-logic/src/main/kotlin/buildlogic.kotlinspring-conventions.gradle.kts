val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
plugins {
  id("buildlogic.javaspring-conventions")
  id("buildlogic.kotlin-conventions")
  kotlin("plugin.spring")
}

dependencies {
  annotationProcessor(libs.org.springframework.boot.spring.boot.configuration.processor)
}
