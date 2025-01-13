val publicRepo = "https://maven.aliyun.com/repository/public"
val centralRepo = "https://maven.aliyun.com/repository/central"
val springRepo = "https://repo.spring.io/milestone"

fun RepositoryHandler.setupDependencyRepositories() {
  mavenLocal()
  mavenCentral()
  maven(publicRepo)
  maven(centralRepo)
  maven(springRepo)
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

