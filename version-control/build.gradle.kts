plugins {
  kotlin("jvm") version "1.9.0"
  java
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(gradleApi())
  implementation("${kotlin("stdlib")}:1.9.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    incremental = true
    freeCompilerArgs = listOf(
      "-Xjsr305=strict",
      "-Xjvm-default=all",
      "-verbose",
      "-Xjdk-release=17"
    )
    jvmTarget = "17"
  }
}

val release = "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
val snapshot = "https://packages.aliyun.com/maven/repository/2336368-snapshot-7SUFMh/"
val pluginGroup = "net.yan100.compose"
val pluginVersion = "1.1.3"
val yunXiaoUsername = System.getenv("YUNXIAO_USER")
val yunXiaoPassword = System.getenv("YUNXIAO_PWD")

project.group = pluginGroup
project.version = pluginVersion

gradlePlugin {
  plugins {
    register("${pluginGroup}.${project.name}") {
      id = "${pluginGroup}.${project.name}"
      implementationClass = "${pluginGroup}.plugin.Main"
    }
  }
}


publishing {
  repositories {
    maven(url = uri(if (pluginVersion.uppercase().contains("SNAPSHOT")) snapshot else release)) {
      credentials {
        this.username = yunXiaoUsername
        this.password = yunXiaoPassword
      }
    }
  }

  publications {
    create<MavenPublication>("maven") {
      groupId = "${pluginGroup}.${project.name}"
      artifactId = "${pluginGroup}.${project.name}.gradle.plugin"
      version = pluginVersion
      from(components["java"])
    }
  }
}
