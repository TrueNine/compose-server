version = libs.versions.composeRdsJimmer.get()

plugins {
  idea
  alias(libs.plugins.tech.argonariod.gradlePluginJimmer)
  alias(libs.plugins.com.google.devtools.ksp)
}



jimmer {
  // 设定 jimmer 依赖版本，此处也可以使用 "latest.release" 或 "0.+" 等版本范围表达式
  version = libs.versions.jimmer.get()
}

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  implementation(project(":core"))

  ksp(libs.org.babyfish.jimmer.jimmerKsp)
  testImplementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)
  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterDataJdbc)
  testImplementation(libs.org.springframework.boot.springBootStarterJdbc)
  testImplementation(libs.org.flywaydb.flywayCore)
}

/*
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
*/
