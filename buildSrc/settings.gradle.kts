dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
  dependencyResolutionManagement {
    repositories {
      mavenLocal()
      mavenCentral()
    }
  }
}
