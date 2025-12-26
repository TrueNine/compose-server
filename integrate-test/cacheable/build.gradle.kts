plugins {
  id("buildlogic.kotlin-spring-boot-test-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies {
  testImplementation(projects.cacheable)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.redis)

  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
}
