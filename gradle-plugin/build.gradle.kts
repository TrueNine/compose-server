import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val l = libs

val pluginGroup = libs.versions.composeGroup.get()
val pluginVersion = libs.versions.compose.get()
val yunxiaoUrl by extra { properties["yunxiaoUrl"] as String }
val yunxiaoUsername by extra { properties["yunxiaoUsername"] as String }
val yunxiaoPassword by extra { properties["yunxiaoPassword"] as String }
val sonatypeUsername by extra { properties["sonatypeUsername"] as String }
val sonatypePassword by extra { properties["sonatypePassword"] as String }



plugins {
  alias(libs.plugins.ktJvm)
  java
  signing
  `version-catalog`
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
  //id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = pluginGroup

version = pluginVersion

repositories {
  mavenLocal()
  maven(url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin/"))
  maven(url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/"))
  maven(url = uri("https://repo.spring.io/milestone"))
  gradlePluginPortal()
  mavenCentral()
  google()
}

dependencies {
  implementation(gradleApi())
  implementation(gradleKotlinDsl())
  implementation(libs.gradlePlugin.springBoot)
  implementation(libs.gradlePlugin.springBootDependencyManagement)
  implementation(libs.bundles.kt)

  testImplementation(gradleTestKit())
  testImplementation(libs.bundles.test.kotlinJunit5)
}

kotlin {
  compilerOptions {
    apiVersion = KotlinVersion.KOTLIN_2_0
    jvmTarget = JvmTarget.fromTarget(l.versions.java.get())
    freeCompilerArgs =
      listOf(
        "-Xjsr305=strict",
        "-Xjvm-default=all-compatibility",
        "-verbose",
        "-Xjdk-release=${l.versions.java.get()}",
        "-jvm-target=${l.versions.java.get()}",
        "-Xextended-compiler-checks"
      )
  }

  jvmToolchain(21)
}

tasks {
  test { useJUnitPlatform() }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.ERROR)
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

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21

  toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
  withSourcesJar()
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


//nexusPublishing {
//  repositories {
//    sonatype {
//      nexusUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//      snapshotRepositoryUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//      username = sonatypeUsername
//      password = sonatypePassword
//    }
//  }
//}

// https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signing_publications
signing {
  useGpgCmd()
  sign(publishing.publications["gradlePlugin"])
  sign(publishing.publications["versionCatalog"])
}

catalog { versionCatalog { from(files("../libs.versions.toml")) } }
