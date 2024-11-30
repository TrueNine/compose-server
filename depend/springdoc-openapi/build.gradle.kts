plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

version = libs.versions.composeDependSpringdocOpenapi.get()


dependencies {
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  api(libs.io.swagger.core.v3.swaggerAnnotationsJakarta)

  implementation(project(":core"))
  implementation(libs.org.springdoc.springdocOpenapiStarterWebmvcUi)

  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}
