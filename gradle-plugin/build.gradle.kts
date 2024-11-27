import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val pluginGroup = libs.versions.composeGroup.get()
val pluginVersion = libs.versions.composeGradlePlugin.get()

plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  signing
  `java-gradle-plugin`
  `maven-publish`
}

group = pluginGroup
version = pluginVersion

dependencies {
  implementation(gradleApi())
  implementation(gradleKotlinDsl())
  implementation(libs.org.springframework.boot.springBootGradlePlugin)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
    freeCompilerArgs =
      listOf(
        "-Xjsr305=strict",
        "-Xjvm-default=all",
        "-verbose",
        "-Xjdk-release=${libs.versions.java.get()}",
        "-jvm-target=${libs.versions.java.get()}"
      )
  }

  jvmToolchain(libs.versions.java.get().toInt())
}

gradlePlugin {
  plugins {
    register("${pluginGroup}.${project.name}") {
      id = "${pluginGroup}.${project.name}"
      implementationClass = "${pluginGroup}.gradleplugin.Main"
    }

    register("${pluginGroup}.settings-${project.name}") {
      id = "${pluginGroup}.settings-${project.name}"
      implementationClass = "${pluginGroup}.gradleplugin.SettingsMain"
    }
  }
}

java {
  sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
  targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
  toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get())) }
  withSourcesJar()
}

publishing {
  repositories {
    mavenLocal()
    val yunxiaoUrl by extra { properties["url.yunxiao.1"] as String }
    maven(url = uri(yunxiaoUrl)) {
      credentials {
        val yunxiaoUsername by extra { properties["usr.yunxiao.1"] as String }
        val yunxiaoPassword by extra { properties["pwd.yunxiao.1"] as String }
        username = yunxiaoUsername
        password = yunxiaoPassword
      }
    }
  }

  publications {
    create<MavenPublication>("gradlePlugin") {
      groupId = "${pluginGroup}.${project.name}"
      artifactId = "${pluginGroup}.${project.name}.gradle.plugin"
      version = pluginVersion
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["gradlePlugin"])
}
