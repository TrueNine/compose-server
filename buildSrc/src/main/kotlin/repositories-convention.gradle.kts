val repos = mapOf(
  "spring" to "https://repo.spring.io/milestone"
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
  maven { url = uri("https://repo.maven.apache.org/maven2/") }

  // Spring 仓库
  maven { url = uri(repos["spring"]!!) }

  // Gradle 插件仓库
  maven { url = uri("https://plugins.gradle.org/m2/") }
}

// 统一配置所有项目的仓库
allprojects {
  buildscript {
    repositories {
      setupDependencyRepositories()
    }
  }

  repositories {
    setupDependencyRepositories()
  }
}
