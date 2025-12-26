val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
plugins {
  id("buildlogic.java-spring-boot-conventions")
  id("buildlogic.kotlin-conventions")
  kotlin("plugin.spring")
  kotlin("kapt")
}

dependencies {
  kapt(libs.org.springframework.boot.spring.boot.configuration.processor)
}

// Configure the jar task to include the LICENSE file
tasks.withType<Jar> {
  from(rootProject.file("LICENSE")) {
    into("META-INF")
  }
}
