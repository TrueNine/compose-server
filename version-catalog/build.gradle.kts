val l = libs

val pluginGroup = libs.versions.composeGroup.get()
val pluginVersion = libs.versions.compose.versionCatalog.get()

val yunxiaoUrl by extra { properties["url.yunxiao.1"] as String }
val yunxiaoUsername by extra { properties["usr.yunxiao.1"] as String }
val yunxiaoPassword by extra { properties["pwd.yunxiao.1"] as String }

plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  java
  signing
  `version-catalog`
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
}

group = pluginGroup

version = pluginVersion

publishing {
  repositories {
    mavenLocal()
    maven(url = uri(yunxiaoUrl)) {
      isAllowInsecureProtocol = false
      credentials {
        username = yunxiaoUsername
        password = yunxiaoPassword
      }
    }
  }

  publications {
    create<MavenPublication>("versionCatalog") {
      groupId = pluginGroup
      artifactId = project.name
      version = pluginVersion
      from(components["versionCatalog"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["versionCatalog"])
}

catalog { versionCatalog { from(files("../libs.versions.toml")) } }
