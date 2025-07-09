
plugins {
  id("buildlogic.javaspring-conventions")
  id("buildlogic.kotlin-conventions")
  kotlin("plugin.spring")
}

dependencies {
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.3")
}
