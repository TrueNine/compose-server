val yunxiaoUrl by extra { properties["url.yunxiao.1"] as String }
val yunxiaoUsername by extra { properties["usr.yunxiao.1"] as String }
val yunxiaoPassword by extra { properties["pwd.yunxiao.1"] as String }

plugins {
  signing
  `version-catalog`
  `maven-publish`
}

group = libs.versions.composeGroup.get()
version = libs.versions.composeVersionCatalog.get()

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
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["versionCatalog"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["versionCatalog"])
}

catalog { versionCatalog { from(files("../gradle/libs.versions.toml")) } }
