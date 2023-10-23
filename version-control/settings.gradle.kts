

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
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}



dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("libs.versions.toml"))
    }
  }
}
