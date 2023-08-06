import net.yan100.compose.plugin.Repos.Credentials.yunXiaoPassword
import net.yan100.compose.plugin.Repos.Credentials.yunXiaoUsername
import net.yan100.compose.plugin.Repos.release
import net.yan100.compose.plugin.Repos.snapshot
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar
import net.yan100.compose.plugin.V
import net.yan100.compose.plugin.Repos
import net.yan100.compose.plugin.ProjectManager

plugins {
  java
  idea
  eclipse
  `visual-studio`
  `maven-publish`
  id("net.yan100.compose.plugin")
  id("org.springframework.boot") version "3.1.2"
  id("io.spring.dependency-management") version "1.1.2"
  kotlin("jvm") version "1.9.0"
  kotlin("kapt") version "1.9.0"
  kotlin("plugin.spring") version "1.9.0"
  kotlin("plugin.jpa") version "1.9.0"
  kotlin("plugin.lombok") version "1.9.0"
  id("com.github.ben-manes.versions") version "0.47.0"
}


allprojects {
  repositories {
    maven(url = uri(Repos.huaweiCloudMaven))
    maven(url = uri(Repos.aliPublic))
    maven(url = uri(Repos.aliCentral))
    maven(url = uri(Repos.aliJCenter))
    maven(url = uri(Repos.aliGradlePlugin))
    maven(url = uri(Repos.aliSpring))
    maven(url = uri(Repos.aliApacheSnapshots))
    maven(url = uri(Repos.springMilestone))
    maven(url = uri(Repos.springLibMilestone))
    maven(url = uri(Repos.springSnapshot))
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

