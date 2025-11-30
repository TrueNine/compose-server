plugins {
  id("buildlogic.kotlin-spring-boot-test-conventions")
  id("buildlogic.spotless-conventions")
  id("io.github.truenine.composeserver.dotenv")
}

dependencies {
  implementation(projects.oss.ossVolcengineTos)

  testImplementation(libs.com.volcengine.ve.tos.java.sdk)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
