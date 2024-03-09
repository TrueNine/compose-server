import net.yan100.compose.plugin.consts.Repos

val l = libs

val pluginGroup: String = l.versions.compose.group.get()
val pluginVersion: String = l.versions.compose.asProvider().get()

plugins {
  alias(libs.plugins.ktJvm)
  `java-gradle-plugin`
}

group = pluginGroup

version = pluginVersion

dependencies {
  implementation(gradleApi())
  implementation(gradleKotlinDsl())
  implementation(libs.kt.kspGoogleApi)
  implementation(libs.util.squareupJavapoet)
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
          "-Xjdk-release=${l.versions.compose.versionControlJavaVersion.get()}",
          "-jvm-target=${l.versions.compose.versionControlJavaVersion.get()}",
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
    mavenLocal()
    maven(
      url =
        uri(
          if (version.toString().uppercase().contains("SNAPSHOT")) Repos.yunXiaoSnapshot
          else Repos.yunXiaoRelese
        )
    ) {
      credentials {
        username = Repos.Credentials.yunXiaoUsername
        password = Repos.Credentials.yunXiaoPassword
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
