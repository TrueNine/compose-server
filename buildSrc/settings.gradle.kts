val publicRepo = "https://maven.aliyun.com/repository/public"
val gradlePluginRepo = "https://maven.aliyun.com/repository/gradle-plugin"
val springRepo = "https://repo.spring.io/milestone"


dependencyResolutionManagement {
  pluginManagement {
    repositories {
      mavenLocal()
      maven(publicRepo)
      maven(gradlePluginRepo)
      maven(springRepo)
      gradlePluginPortal()
    }
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
