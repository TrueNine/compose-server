val springRepo = "https://repo.spring.io/milestone"


dependencyResolutionManagement {
  pluginManagement {
    repositories {
      mavenLocal()
      gradlePluginPortal()
      mavenCentral()
      maven(springRepo)
    }
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
