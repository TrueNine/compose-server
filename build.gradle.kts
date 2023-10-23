import net.yan100.compose.plugin.Repos.Credentials.yunXiaoPassword
import net.yan100.compose.plugin.Repos.Credentials.yunXiaoUsername
import net.yan100.compose.plugin.Repos.yunXiaoRelese
import net.yan100.compose.plugin.Repos.yunXiaoSnapshot
import net.yan100.compose.plugin.aliYunXiao
import net.yan100.compose.plugin.allAnnotationCompileOnly
import net.yan100.compose.plugin.chinaRegionRepositories
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  java
  idea
  eclipse
  `visual-studio`
  `maven-publish`
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.hibernate.orm)
  alias(libs.plugins.spring.boot.dependencymanagement)
  alias(libs.plugins.kt.jvm)
  alias(libs.plugins.kt.kapt)
  alias(libs.plugins.kt.spring)
  alias(libs.plugins.kt.lombok)
  alias(libs.plugins.kt.jpa)
  alias(libs.plugins.versions)
  id("net.yan100.compose.version-control")
}

val l = libs
version = libs.versions.compose.asProvider().get()

allprojects {
  repositories {
    chinaRegionRepositories()
    aliYunXiao()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }

  tasks {
    withType<ProcessAot> {
      enabled = false
    }
    withType<BootJar> {
      enabled = false
    }
  }

  group = l.versions.compose.group.get()
  version = l.versions.compose.asProvider().get()

  extra["springCloudVersion"] = l.versions.spring.cloud.get()
  extra["snippetsDir"] = file("build/generated-snippets")
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.kapt")
  apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")
  apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

  apply(plugin = "org.springframework.boot")
  apply(plugin = "org.hibernate.orm")
  apply(plugin = "io.spring.dependency-management")

  apply(plugin = "com.github.ben-manes.versions")
  apply(plugin = "maven-publish")


  kapt {
    keepJavacAnnotationProcessors = true
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
  }

  kotlin {
    jvmToolchain(21)
  }

  tasks {
    withType<AbstractCopyTask> {
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    withType<JavaCompile> {
      options.compilerArgs.addAll(
        listOf(
          "--enable-preview"
        )
      )
    }
    withType<KotlinCompile> {
      kotlinOptions {
        freeCompilerArgs += listOf(
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

    test {
      useJUnitPlatform()
    }

    compileJava {
      options.isFork = true
      options.forkOptions.memoryMaximumSize = "4G"
      options.forkOptions.memoryInitialSize = "2G"
    }
    javadoc {
      if (JavaVersion.current().isJava9Compatible) (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
  }

  publishing {
    repositories {
      maven(url = uri(if (version.toString().uppercase().contains("SNAPSHOT")) yunXiaoSnapshot else yunXiaoRelese)) {
        credentials {
          username = yunXiaoUsername
          password = yunXiaoPassword
        }
      }
    }

    publications {
      create<MavenPublication>("maven") {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
        from(components["java"])
      }
    }
  }

  dependencies {
    implementation(l.spring.boot.autoconfigure)
    annotationProcessor(l.spring.boot.configureprocessor)
    implementation(l.bundles.kt)
    testImplementation(l.bundles.spring.kotlin.junit5)
    allAnnotationCompileOnly(l.lombok)
  }

  dependencyManagement {
    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${l.versions.spring.boot.get()}")
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:${l.versions.spring.cloud.get()}")
      mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${l.versions.spring.cloudalibaba.get()}")
      mavenBom("org.springframework.modulith:spring-modulith-bom:${l.versions.spring.modulith.get()}")
    }
  }

  configurations {
    compileOnly {
      extendsFrom(configurations.annotationProcessor.get())
    }
  }
}

tasks {
  wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = l.versions.gradle.asProvider().get()
  }
}
