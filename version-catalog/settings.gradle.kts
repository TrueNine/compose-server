pluginManagement {
  repositories {
    maven(url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin/"))
    maven(url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/"))
    maven(url = uri("https://repo.spring.io/milestone"))
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

dependencyResolutionManagement { versionCatalogs { create("libs") { from(files("../libs.versions.toml")) } } }
