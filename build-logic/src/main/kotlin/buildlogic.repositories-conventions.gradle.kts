fun RepositoryHandler.setupDependencyRepositories() {
  mavenLocal()
  maven { url = uri("https://repo.maven.apache.org/maven2/") }
  maven { url = uri("https://repo.spring.io/milestone/") }
  maven { url = uri("https://plugins.gradle.org/m2/") }
  mavenCentral()
  gradlePluginPortal()
}

repositories {
  setupDependencyRepositories()
}

subprojects {
  repositories {
    setupDependencyRepositories()
  }
}

allprojects {
  buildscript {
    repositories {
      setupDependencyRepositories()
    }
  }
}