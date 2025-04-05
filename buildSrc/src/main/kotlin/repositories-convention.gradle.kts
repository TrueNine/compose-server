val repositoriesMap = mapOf(
  "spring" to "https://repo.spring.io/milestone", "mavenCentral" to "https://repo.maven.apache.org/maven2/", "gradlePlugins" to "https://plugins.gradle.org/m2/"
)

val includedGroups = setOf(
  "com.squareup",
  "org.babyfish.jimmer",
  "org.springframework",
  "io.spring",
  "cn.hutool",
  "org.flywaydb",
  "org.jetbrains.kotlin",
  "org.jetbrains.kotlinx",
  "org/seleniumhq",
  "io.projectreactor.kotlin",
  "com.fasterxml.jackson",
  "com.fasterxml.jackson.core",
  "com.fasterxml.jackson.module",
  "com.fasterxml.jackson.datatype"
)

fun RepositoryHandler.setupDependencyRepositories() {
  mavenLocal()

  // Maven Central 仓库
  maven {
    url = uri(repositoriesMap["mavenCentral"]!!)
    mavenContent {
      releasesOnly()
    }
  }

  // Spring 仓库
  maven {
    url = uri(repositoriesMap["spring"]!!)
    mavenContent {
      includeGroupAndSubgroups("org.springframework")
      includeGroupAndSubgroups("io.spring")
    }
  }

  // Gradle 插件仓库
  maven {
    url = uri(repositoriesMap["gradlePlugins"]!!)
    mavenContent {
      includeGroupAndSubgroups("com.diffplug.spotless")
    }
  }
}

allprojects {
  repositories {
    setupDependencyRepositories()
  }

  buildscript {
    repositories {
      setupDependencyRepositories()
    }
  }
}
