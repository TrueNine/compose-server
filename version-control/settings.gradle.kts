pluginManagement {
  repositories {
    mavenLocal()
    maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
    maven(url = uri("https://repo.spring.io/milestone"))
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  versionCatalogs { create("libs") { from(files("libs.versions.toml")) } }
}
