val release = "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
val snapshot = "https://packages.aliyun.com/maven/repository/2336368-snapshot-7SUFMh/"
val pluginGroup = "net.yan100.compose"
val pluginVersion = libs.versions.compose.version.control.plugin.get().toString()
val yunXiaoUsername = System.getenv("YUNXIAO_USER")
val yunXiaoPassword = System.getenv("YUNXIAO_PWD")


plugins {
  alias(libs.plugins.kt.jvm)
  java
  `version-catalog`
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
}

group = pluginGroup
version = pluginVersion

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(gradleApi())
  implementation("${kotlin("stdlib")}:1.9.10")
}

tasks {
  compileKotlin {
    compilerOptions {
      freeCompilerArgs = listOf(
        "-Xjsr305=strict",
        "-Xjvm-default=all",
        "-verbose"
      )
    }
  }
}



gradlePlugin {
  plugins {
    register("${pluginGroup}.${project.name}") {
      id = "${pluginGroup}.${project.name}"
      implementationClass = "${pluginGroup}.plugin.Main"
    }
  }
}


publishing {
  repositories {
    mavenLocal()
    maven(url = uri(if (pluginVersion.uppercase().contains("SNAPSHOT")) snapshot else release)) {
      credentials {
        this.username = yunXiaoUsername
        this.password = yunXiaoPassword
      }
    }
  }

  publications {
    create<MavenPublication>("gradlePlugin") {
      groupId = "${pluginGroup}.${project.name}"
      artifactId = "${pluginGroup}.${project.name}.gradle.plugin"
      version = pluginVersion
      from(components["java"])
    }

    create<MavenPublication>("versionCatalog") {
      groupId = pluginGroup
      artifactId = "${project.name}-catalog"
      version = pluginVersion
      from(components["versionCatalog"])
    }
  }
}

catalog {
  versionCatalog {
    from(files("libs.versions.toml"))
  }
}
