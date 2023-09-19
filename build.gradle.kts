import net.yan100.compose.plugin.Repos
import net.yan100.compose.plugin.ProjectVersion

import net.yan100.compose.plugin.Repos.Credentials.yunXiaoPassword
import net.yan100.compose.plugin.Repos.Credentials.yunXiaoUsername
import net.yan100.compose.plugin.Repos.yunXiaoRelese
import net.yan100.compose.plugin.Repos.yunXiaoSnapshot
import net.yan100.compose.plugin.V
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


allprojects {
  repositories {
    Repos.publicRepositories.forEach {
      maven(url = uri(it))
    }
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

  group = ProjectVersion.GROUP
  version = ProjectVersion.VERSION

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      incremental = true
      freeCompilerArgs = listOf(
        "-Xjsr305=strict",
        "-Xjvm-default=all",
        "-verbose",
        "-Xjdk-release=17"
      )
      jvmTarget = "17"
    }
  }


  extra["springCloudVersion"] = V.Spring.springCloud
  extra["snippetsDir"] = file("build/generated-snippets")
}

val internalLibs = libs


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

  apply(plugin = "maven-publish")
  apply(plugin = "com.github.ben-manes.versions")

  java.sourceCompatibility = V.Lang.javaPlatform
  java.targetCompatibility = V.Lang.javaPlatform

  tasks.withType<AbstractCopyTask> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  kapt {
    keepJavacAnnotationProcessors = true
  }

  java {
    withSourcesJar()
  }


  tasks {
    compileJava {
      options.isFork = true
      options.forkOptions.memoryMaximumSize = "4G"
      options.forkOptions.memoryInitialSize = "2G"
    }
  }

  tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
      (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
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
    compileOnly("org.springframework.cloud:spring-cloud-starter-bootstrap") {
      exclude("org.apache.logging.log4j")
      exclude("org.springframework.boot", "spring-boot-starter-logging")
      exclude("org.springframework.boot", "spring-boot")
    }

    implementation(internalLibs.bundles.kt)
    implementation(internalLibs.bundles.spring.kotlin.testng) {
      exclude("org.junit.jupiter", "junit-jupiter")
    }

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    annotationProcessor("org.projectlombok:lombok:${V.Lang.lombok}")
    compileOnly("org.projectlombok:lombok:${V.Lang.lombok}")
    testAnnotationProcessor("org.projectlombok:lombok:${V.Lang.lombok}")
    testCompileOnly("org.projectlombok:lombok:${V.Lang.lombok}")
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

tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
  this.gradleVersion = ProjectVersion.GRADLE_VERSION
}

