plugins {
  `java-gradle-plugin`
}

val gp = libs.versions.composeGroup.get()

group = libs.versions.composeGroup.get()
version = libs.versions.composeGradlePlugin.get()

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.org.jetbrains.kotlin.kotlinGradlePluginApi)
  compileOnly(libs.org.jetbrains.kotlin.kotlinGradlePlugin)
  compileOnly(libs.org.jetbrains.kotlin.kotlinStdlib)
  compileOnly(gradleKotlinDsl())

  implementation(libs.org.springframework.boot.springBootGradlePlugin)

  testImplementation(gradleApi())
  testImplementation(gradleTestKit())
  testImplementation(gradleKotlinDsl())
}

gradlePlugin {
  plugins {
    register("${gp}.${project.name}") {
      id = "${gp}.${project.name}"
      displayName = "${gp}.${project.name}.gradle.plugin"
      implementationClass = "${gp}.gradleplugin.Main"
      description = "compose server development gradle"
    }

    register("${gp}.settings-${project.name}") {
      id = "${gp}.settings-${project.name}"
      displayName = "${gp}.settings-${project.name}.gradle.plugin"
      implementationClass = "${gp}.gradleplugin.Main"
      implementationClass = "${gp}.gradleplugin.SettingsMain"
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("gradlePlugin") {
      groupId = "${gp}.${project.name}"
      artifactId = "${gp}.${project.name}.gradle.plugin"
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["gradlePlugin"])
}
