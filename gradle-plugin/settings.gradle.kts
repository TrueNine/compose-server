pluginManagement {
  repositories {
    maven(url = uri(extra["url.yunxiao.1"].toString())) {
      credentials {
        username = extra["usr.yunxiao.1"].toString()
        password = extra["pwd.yunxiao.1"].toString()
      }
    }
  }
}

dependencyResolutionManagement { versionCatalogs { create("libs") { from(files("../libs.versions.toml")) } } }
