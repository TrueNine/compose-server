import Repos.Credentials.yunXiaoPassword
import Repos.Credentials.yunXiaoUsername
import Repos.release
import Repos.snapshot
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("java")
  id("java-library")
  id("org.springframework.boot") version V.Spring.springBoot
  id("io.spring.dependency-management") version V.Plugin.dependencyManagementPlugin
  kotlin("jvm") version V.Lang.kotlin
  kotlin("kapt") version V.Lang.kotlin
  kotlin("plugin.spring") version V.Lang.kotlin
  kotlin("plugin.jpa") version V.Lang.kotlin
  kotlin("plugin.lombok") version V.Lang.kotlin
  id("maven-publish")
}


allprojects {
  repositories {
    maven(release) {
      this.isAllowInsecureProtocol = true
      credentials {
        this.username = yunXiaoUsername
        this.password = yunXiaoPassword
      }
    }
    maven(snapshot) {
      this.isAllowInsecureProtocol = true
      credentials {
        this.username = yunXiaoUsername
        this.password = yunXiaoPassword
      }
    }

    maven(Repos.aliCentral)
    maven(Repos.aliJCenter)
    maven(Repos.aliPublic)
    maven(Repos.aliGradlePlugin)
    maven(Repos.aliSpring)
    maven(Repos.aliApacheSnapshots)
    maven(Repos.springMilestone)
    maven(Repos.springLibMilestone)
    maven(Repos.springSnapshot)
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
      freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
      jvmTarget = V.Lang.javaStr
    }
  }

  tasks.withType<AbstractCopyTask> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  group = ProjectManager.group
  version = ProjectManager.version
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
  apply(plugin = "kotlin")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  java.sourceCompatibility = V.Lang.javaPlatform

  java {
    withSourcesJar()
  }

  tasks.named("compileKotlin") {
    dependsOn("clean")
  }

  tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
      (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
  }

  publishing {
    repositories {
      maven(
        if (version.toString().endsWith("SNAPSHOT")) snapshot else release
      ) {
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
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${V.Lang.kotlin}")
    api("org.jetbrains.kotlin:kotlin-reflect:${V.Lang.kotlin}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${V.Lang.kotlinxCoroutine}")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:${V.Lang.reactorKotlinExtension}")

    compileOnly("org.springframework.cloud:spring-cloud-starter-bootstrap")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
      exclude("org.junit.jupiter", "junit-jupiter")
    }

    testApi("io.projectreactor:reactor-test")
    testApi("org.jetbrains.kotlin:kotlin-test-testng:${V.Test.kotlinTestNG}")
    testApi("org.testng:testng:${V.Test.testNG}")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
  }

  dependencyManagement {
    dependencies {
      dependency("${group}:core:${V.Component.core}")
      dependency("${group}:oss:${V.Component.oss}")
      dependency("${group}:security:${V.Component.security}")
      dependency("${group}:rds:${V.Component.rds}")
      dependency("${group}:rds-gen:${V.Component.rdsGen}")
      dependency("${group}:web-api-doc:${V.Component.webApiDoc}")
      dependency("${group}:data-common-crawler:${V.Component.dataCommonCrawler}")
      dependency("${group}:data-common-data-extract:${V.Component.dataCommonDataExtract}")
      dependency("${group}:cacheable:${V.Component.cacheable}")
      dependency("${group}:schedule:${V.Component.schedule}")
      dependency("${group}:depend-flyway:${V.Component.dependFlyway}")
      dependency("${group}:depend-web-servlet:${V.Component.dependWebServlet}")
    }

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
  gradleVersion = V.Lang.gradleWrapper
}


