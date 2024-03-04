val l = libs
val release = "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
val snapshot = "https://packages.aliyun.com/maven/repository/2336368-snapshot-7SUFMh/"
val pluginGroup = libs.versions.compose.group.get()
val pluginVersion = libs.versions.compose.asProvider().get().toString()
val yunXiaoUsername = System.getenv("YUNXIAO_USER")
val yunXiaoPassword = System.getenv("YUNXIAO_PWD")

plugins {
  alias(libs.plugins.ktJvm)
  java
  `version-catalog`
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
}

group = pluginGroup

version = pluginVersion

repositories {
  mavenLocal()
  maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
  maven(url = uri("https://repo.spring.io/milestone"))
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(gradleApi())
  implementation(gradleKotlinDsl())
  implementation(libs.bundles.kt)
}

kotlin {
  jvmToolchain { languageVersion.set(JavaLanguageVersion.of(l.versions.java.get().toInt())) }
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.ERROR)
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile> {
    kotlinOptions {
      freeCompilerArgs +=
        listOf(
          "-Xjsr305=strict",
          "-Xjvm-default=all",
          "-verbose",
          "-Xjdk-release=${l.versions.java.get()}",
          "-jvm-target=${l.versions.java.get()}",
          "-Xextended-compiler-checks"
        )
      jvmTarget = l.versions.java.get()
    }
  }
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
    maven(url = layout.buildDirectory.dir("local-maven-repo"))
    mavenLocal()
    maven(url = uri(if (pluginVersion.uppercase().contains("SNAPSHOT")) snapshot else release)) {
      credentials {
        username = yunXiaoUsername
        password = yunXiaoPassword
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

    create<MavenPublication>("gradleSettingsPlugin") {
      groupId = "${pluginGroup}.${project.name}"
      artifactId = "${pluginGroup}.${project.name}-settings.gradle.plugin"
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

catalog { versionCatalog { from(files("libs.versions.toml")) } }
