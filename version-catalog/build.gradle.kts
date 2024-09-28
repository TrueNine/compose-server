val l = libs

val pluginGroup = libs.versions.composeGroup.get()
val pluginVersion = libs.versions.compose.versionCatalog.get()

val yunxiaoUrl by extra { properties["yunxiaoUrl"] as String }
val yunxiaoUsername by extra { properties["yunxiaoUsername"] as String }
val yunxiaoPassword by extra { properties["yunxiaoPassword"] as String }

plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  java
  signing
  `version-catalog`
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
  // id("com.gradle.plugin-publish") version "1.2.1"
  // id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-2"
}

group = pluginGroup

version = pluginVersion

repositories {
  maven(url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin/"))
  maven(url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/"))
  maven(url = uri("https://repo.spring.io/milestone"))
  gradlePluginPortal()
  mavenCentral()
  google()
}

gradlePlugin {
  plugins {
    register("${pluginGroup}.${project.name}") {
      id = "${pluginGroup}.${project.name}"
      implementationClass = "${pluginGroup}.plugin.Main"
    }
    register("${pluginGroup}.${project.name}-settings") {
      id = "${pluginGroup}.${project.name}-settings"
      implementationClass = "${pluginGroup}.plugin.SettingsMain"
    }
  }
}

publishing {
  repositories {
    mavenLocal()
    maven(url = layout.buildDirectory.dir("local-maven-repo"))
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
