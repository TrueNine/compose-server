val publicRepo = "https://maven.aliyun.com/repository/public"
val centralRepo = "https://maven.aliyun.com/repository/central"
val springRepo = "https://repo.spring.io/milestone"

val noChinaRepo = listOf(
  "com.squareup",
  "org.springframework",
  "io.spring",
  "org.jetbrains.kotlin",
  "org.jetbrains.kotlinx",
  "io.projectreactor.kotlin",
)

fun RepositoryHandler.setupDependencyRepositories() {
  maven(publicRepo) {
    mavenContent {
      noChinaRepo.forEach { excludeGroupAndSubgroups(it) }
    }
  }
  maven(centralRepo) {
    mavenContent {
      noChinaRepo.forEach { excludeGroupAndSubgroups(it) }
    }
  }
  maven(springRepo) {
    mavenContent {
      includeGroupAndSubgroups("org.springframework")
      includeGroupAndSubgroups("io.spring")
    }
  }
  mavenCentral {
    mavenContent {
      mavenContent {
        noChinaRepo.forEach { includeGroupAndSubgroups(it) }
      }
    }
  }
  gradlePluginPortal {
    content {
      includeGroupAndSubgroups("com.diffplug.spotless")
    }
  }
}

repositories {
  setupDependencyRepositories()
}

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

