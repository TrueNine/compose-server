
plugins {
  id("buildlogic.java-conventions")
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudVersion"] = "2025.0.0"
extra["springAiVersion"] = "1.0.0"

dependencies {
  implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.3")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.3")
}
