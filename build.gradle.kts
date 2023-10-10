import net.yan100.compose.plugin.*
import net.yan100.compose.plugin.Repos.Credentials.yunXiaoPassword
import net.yan100.compose.plugin.Repos.Credentials.yunXiaoUsername
import net.yan100.compose.plugin.Repos.yunXiaoRelese
import net.yan100.compose.plugin.Repos.yunXiaoSnapshot
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
    google()
  }

  tasks {
    withType<ProcessAot> {
      enabled = false
    }
    withType<BootJar> {
      enabled = false
    }
  }

  group = ProjectVersion.GROUP
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

  hibernate {
    enhancement {
      enableAssociationManagement.set(false)
    }
  }

  tasks {
    withType<AbstractCopyTask> {
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    withType<KotlinCompile> {
      kotlinOptions {
        freeCompilerArgs += listOf(
          "-Xjsr305=strict",
          "-Xjvm-default=all",
          "-verbose",
          "-Xjdk-release=21",
          "-jvm-target=21",
          "-Xextended-compiler-checks"
        )
        jvmTarget = "21"
      }
    }

    compileJava {
      options.isFork = true
      options.forkOptions.memoryMaximumSize = "4G"
      options.forkOptions.memoryInitialSize = "2G"
    }
    javadoc {
      if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
      }
    }
  }

  publishing {
    repositories {
      maven(url = uri(if (version.toString().uppercase().contains("SNAPSHOT")) yunXiaoSnapshot else yunXiaoRelese)) {
        credentials {
          this.username = yunXiaoUsername
          this.password = yunXiaoPassword
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
    compileOnly(l.spring.cloud.bootstrap) {
      exclude("org.apache.logging.log4j")
      exclude("org.springframework.boot", "spring-boot-starter-logging")
      exclude("org.springframework.boot", "spring-boot")
    }

    implementation(l.spring.boot.autoconfigure)
    annotationProcessor(l.spring.boot.configureprocessor)

    implementation(l.bundles.kt)
    testImplementation(l.bundles.spring.kotlin.testng) {
      exclude("org.junit.jupiter", "junit-jupiter")
    }
    allAnnotationCompileOnly(l.lombok)
  }

  dependencyManagement {
    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${V.Spring.springBoot}")
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:${V.Spring.springCloud}")
      mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${V.Spring.cloudAlibaba}")
      mavenBom("org.springframework.modulith:spring-modulith-bom:${V.Spring.modulith}")
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
    this.gradleVersion = ProjectVersion.GRADLE_VERSION
  }
}
