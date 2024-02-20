import net.yan100.compose.plugin.Repos.Credentials.yunXiaoPassword
import net.yan100.compose.plugin.Repos.Credentials.yunXiaoUsername
import net.yan100.compose.plugin.Repos.yunXiaoRelese
import net.yan100.compose.plugin.Repos.yunXiaoSnapshot
import net.yan100.compose.plugin.aliYunXiao
import net.yan100.compose.plugin.allAnnotationCompileOnly
import net.yan100.compose.plugin.chinaRegionRepositories
import net.yan100.compose.plugin.distribute
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  java
  idea
  eclipse
  `visual-studio`
  `maven-publish`
  alias(libs.plugins.springBoot)
  alias(libs.plugins.hibernateOrm)
  alias(libs.plugins.springBootDependencyManagement)
  alias(libs.plugins.ktJvm)
  alias(libs.plugins.ktKapt)
  alias(libs.plugins.ktSpring)
  alias(libs.plugins.ktNoArg)
  alias(libs.plugins.ktAllOpen)
  alias(libs.plugins.ktLombok)
  alias(libs.plugins.ktJpa)
  alias(libs.plugins.versions)
  alias(libs.plugins.spotless)
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



  group = l.versions.compose.group.get()
  version = l.versions.compose.asProvider().get()

  extra["springCloudVersion"] = l.versions.spring.cloud.get()
  extra["snippetsDir"] = file("build/generated-snippets")

  tasks {
    withType<ProcessAot> {
      enabled = false
    }
    withType<BootJar> {
      enabled = false
    }
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.kapt")

  apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
  apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
  apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")
  apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

  apply(plugin = "org.springframework.boot")
  apply(plugin = "org.hibernate.orm")
  apply(plugin = "io.spring.dependency-management")

  apply(plugin = "maven-publish")

  apply(plugin = "com.diffplug.spotless")

  spotless {
    format("misc") {
      target("*.java")
      trimTrailingWhitespace()
      indentWithTabs(2)
      endWithNewline()
    }
  }

  dependencies {
    implementation(l.spring.boot.autoconfigure)
    annotationProcessor(l.spring.boot.configureprocessor)
    implementation(l.bundles.kt)
    testImplementation(l.bundles.test.springKotlinJunit5)
    allAnnotationCompileOnly(l.lombok)
  }

  dependencyManagement {
    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${l.versions.spring.boot.get()}")
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:${l.versions.spring.cloud.get()}")
      mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${l.versions.spring.cloudAlibaba.get()}")
      mavenBom("org.springframework.modulith:spring-modulith-bom:${l.versions.spring.modulith.get()}")
      mavenBom("org.drools:drools-bom:${l.versions.drools.get()}")
    }
  }

  configurations {
    compileOnly {
      extendsFrom(configurations.annotationProcessor.get())
    }
  }

  kapt {
    keepJavacAnnotationProcessors = true
    //correctErrorTypes =true
    javacOptions { option("querydsl.entityAccessors", true) }
    arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
  }

  noArg {
    annotations(
      "jakarta.persistence.MappedSuperclass",
      "jakarta.persistence.Entity",
      "net.yan100.compose.core.annotations.OpenArg",
      "io.swagger.v3.oas.annotations.media.Schema"
    )
  }
  allOpen {
    annotations(
      "jakarta.persistence.MappedSuperclass",
      "jakarta.persistence.Entity",
      "net.yan100.compose.core.annotations.OpenArg",
      "io.swagger.v3.oas.annotations.media.Schema"
    )
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
  }

  kotlin {
//    sourceSets.all {
//      languageSettings {
//        version = "2.0"
//      }
//    }

    jvmToolchain(21)
  }

  tasks {
    withType<AbstractCopyTask> {
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    withType<JavaCompile>().configureEach {
      options.compilerArgs.add("--enable-preview")
    }


    withType<KotlinCompile> {
      kotlinOptions {
        freeCompilerArgs += listOf(
          "-Xjsr305=strict",
          "-Xjvm-default=all-compatibility",
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

    jar {
      archiveClassifier = null
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
}

tasks {
  wrapper {
    distribute(libs.versions.gradle.get(), "https://mirrors.cloud.tencent.com/gradle")
  }
}
