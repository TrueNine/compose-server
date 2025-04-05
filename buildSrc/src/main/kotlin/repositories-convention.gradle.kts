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
  "io.projectreactor.kotlin"
)

fun RepositoryHandler.setupDependencyRepositories() {
  mavenLocal()
  mavenCentral()
  // Spring 仓库
  maven(repos["spring"]!!) {
    mavenContent {
      includeGroupAndSubgroups("org.springframework")
      includeGroupAndSubgroups("io.spring")
    }
  }

  // Maven Central 仓库
  mavenCentral {
    mavenContent {
      includedGroups.forEach { includeGroupAndSubgroups(it) }
    }
  }

  // Gradle 插件仓库
  gradlePluginPortal {
    content {
      includeGroupAndSubgroups("com.diffplug.spotless")
    }
  }
}

// 统一配置所有项目的仓库
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
