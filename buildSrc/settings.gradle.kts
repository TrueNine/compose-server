val springRepo = "https://repo.spring.io/milestone"

// 明确设置buildSrc的项目名称
rootProject.name = "buildSrc"

dependencyResolutionManagement {
  pluginManagement {
    repositories {
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
