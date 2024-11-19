import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val l = libs

val pluginGroup = libs.versions.composeGroup.get()
val pluginVersion = libs.versions.compose.gradlePlugin.get()


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

dependencies {
  implementation(gradleApi())
  implementation(gradleKotlinDsl())
  implementation(libs.org.springframework.boot.springBootGradlePlugin)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(l.versions.java.get())
    freeCompilerArgs =
      listOf(
        "-Xjsr305=strict",
        "-Xjvm-default=all",
        "-verbose",
        "-Xjdk-release=${libs.versions.java.get()}",
        "-jvm-target=${libs.versions.java.get()}"
      )
  }

  jvmToolchain(l.versions.java.get().toInt())
}

gradlePlugin {
  plugins {
    register("${pluginGroup}.${project.name}") {
      id = "${pluginGroup}.${project.name}"
      implementationClass = "${pluginGroup}.gradleplugin.Main"
    }

    // TODO 暂时屏蔽 settings 插件
    /*register("${pluginGroup}.${project.name}-settings") {
      id = "${pluginGroup}.${project.name}-settings"
      implementationClass = "${pluginGroup}.gradleplugin.SettingsMain"
    }*/
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
  toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
  //withSourcesJar()
}

publishing {
  repositories {
    mavenLocal()

    val yunxiaoUrl by extra { properties["url.yunxiao.1"] as String }
    maven(url = uri(yunxiaoUrl)) {
      credentials {
        val yunxiaoUsername by extra { properties["usr.yunxiao.1"] as String }
        val yunxiaoPassword by extra { properties["pwd.yunxiao.1"] as String }
        username = yunxiaoUsername
        password = yunxiaoPassword
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
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["gradlePlugin"])
}

catalog { versionCatalog { from(files("../libs.versions.toml")) } }
