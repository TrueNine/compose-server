pluginManagement {
  includeBuild("../../build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
  }
}

dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
  }
  versionCatalogs {
    create("libs") {
      from(files("../../gradle/libs.versions.toml"))
    }
  }
}

rootProject.name = "gradleplugin-dotenv"

