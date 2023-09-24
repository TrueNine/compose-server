pluginManagement {
  repositories {
    mavenLocal()
    maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
    maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
    maven(url = uri("https://maven.aliyun.com/repository/public"))
    maven(url = uri("https://maven.aliyun.com/repository/jcenter"))
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("libs.versions.toml"))
    }
  }
}
