import Repos.Credentials.yunXiaoPassword
import Repos.Credentials.yunXiaoUsername
import Repos.release
import Repos.snapshot
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  java
  idea
  eclipse
  `visual-studio`
  `maven-publish`
  id("org.springframework.boot") version V.Plugin.spring
  id("io.spring.dependency-management") version V.Plugin.springDependencyManagement
  kotlin("jvm") version V.Plugin.kotlinJvmPlugin
  kotlin("kapt") version V.Plugin.kotlinKapt
  kotlin("plugin.spring") version V.Plugin.kotlinSpring
  kotlin("plugin.jpa") version V.Plugin.kotlinJpa
  kotlin("plugin.lombok") version V.Plugin.kotlinLombok
  id("com.github.ben-manes.versions") version "0.47.0"
}


allprojects {
  repositories {
    maven(url = uri(Repos.aliPublic))
    maven(url = uri(Repos.aliCentral))
    maven(url = uri(Repos.aliJCenter))
    maven(url = uri(Repos.aliGradlePlugin))
    maven(url = uri(Repos.aliSpring))
    maven(url = uri(Repos.aliApacheSnapshots))
    maven(url = uri(Repos.springMilestone))
    maven(url = uri(Repos.springLibMilestone))
    maven(url = uri(Repos.springSnapshot))
    maven(url = uri(Repos.huaweiCloudMaven))
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  tasks.withType<ProcessAot> {
    enabled = false
  }

  tasks.withType<BootJar> {
    enabled = false
  }



  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      incremental = true
      freeCompilerArgs = listOf(
        "-Xjsr305=strict",
        "-Xjvm-default=all",
        "-verbose",
        "-Xjdk-release=${V.Lang.java}"
      )
      jvmTarget = V.Lang.java
    }
  }

  tasks.withType<AbstractCopyTask> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  group = ProjectManager.group
  version = ProjectManager.version

  extra["springCloudVersion"] = V.Spring.springCloud
}

subprojects {
  apply(plugin = "idea")
  apply(plugin = "eclipse")
  apply(plugin = "visual-studio")
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")
  apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "maven-publish")

  java.sourceCompatibility = V.Lang.javaPlatform

  java {
    withSourcesJar()
  }

  tasks {
    compileJava {
      options.isFork = true
      options.forkOptions.memoryMaximumSize = "2G"
      options.forkOptions.memoryInitialSize = "1G"
    }
  }

  tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
      (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
  }

  publishing {
    repositories {
      maven(url = uri(if (version.toString().uppercase().contains("SNAPSHOT")) snapshot else release)) {
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
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.cloud:spring-cloud-starter-bootstrap") {
      exclude("org.apache.logging.log4j")
      exclude("org.springframework.boot", "spring-boot-starter-logging")
      exclude("org.springframework.boot", "spring-boot")
    }
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    annotationProcessor("org.projectlombok:lombok:${V.Lang.lombok}")

    compileOnly("org.projectlombok:lombok:${V.Lang.lombok}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testAnnotationProcessor("org.projectlombok:lombok:${V.Lang.lombok}")
    testCompileOnly("org.projectlombok:lombok:${V.Lang.lombok}")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
      exclude("org.junit.jupiter", "junit-jupiter")
    }

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-testng:${V.Test.kotlinTestNG}")
    testImplementation("org.testng:testng:${V.Test.testNG}")
    testImplementation("io.mockk:mockk:${V.Test.mockk}")
  }

  dependencyManagement {
    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${V.Spring.springBoot}")
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:${V.Spring.springCloud}")
      mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${V.Spring.cloudAlibaba}")
    }
  }

  configurations {
    compileOnly {
      extendsFrom(configurations.annotationProcessor.get())
    }
  }
}

tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
  this.gradleVersion = ProjectManager.gradleVersion
}

