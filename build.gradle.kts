import com.diffplug.spotless.LineEnding
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  java
  idea
  eclipse
  `eclipse-wtp`
  xcode
  `visual-studio`
  `maven-publish`
  signing
  alias(libs.plugins.com.github.benManes.versions)
  alias(libs.plugins.org.springframework.boot)
  alias(libs.plugins.org.hibernate.orm)
  alias(libs.plugins.io.spring.dependencyManagement)
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.powerAssert)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.noarg)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.allopen)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.lombok)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.jpa)
  alias(libs.plugins.com.diffplug.spotless)
}

val yunxiaoUrl = extra["yunxiaoUrl"].toString()
val yunxiaoUsername = extra["yunxiaoUsername"].toString()
val yunxiaoPassword = extra["yunxiaoPassword"].toString()
val sonatypeUsername = extra["sonatypeUsername"].toString()
val sonatypePassword = extra["sonatypePassword"].toString()

apply(plugin = libs.plugins.com.diffplug.spotless.get().pluginId)

val l = libs

project.version = libs.versions.compose.asProvider().get()

allprojects {
  project.group = l.versions.composeGroup.get()

  tasks {
    withType<ProcessAot> { enabled = false }
    withType<BootJar> { enabled = false }
  }
}

powerAssert { functions = listOf("kotlin.assert", "kotlin.test.assertTrue", "kotlin.test.assertEquals", "kotlin.test.assertNull") }

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "maven-publish")
  apply(plugin = "signing")
  apply(plugin = l.plugins.org.jetbrains.kotlin.jvm.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.kapt.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.lombok.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.noarg.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.allopen.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.spring.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.jpa.get().pluginId)
  apply(plugin = l.plugins.org.springframework.boot.get().pluginId)
  apply(plugin = l.plugins.org.hibernate.orm.get().pluginId)
  apply(plugin = l.plugins.io.spring.dependencyManagement.get().pluginId)
  apply(plugin = l.plugins.com.github.benManes.versions.get().pluginId)
  extra["springCloudVersion"] = l.versions.spring.cloud.get()

  dependencies {
    annotationProcessor(l.org.springframework.springBootConfigurationProcessor)

    annotationProcessor(l.org.projectlombok.lombok)
    compileOnly(l.org.projectlombok.lombok)

    implementation(l.bundles.kotlin)

    implementation(l.org.springframework.boot.springBootTestAutoconfigure)

    testImplementation(l.bundles.junit5)
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

  configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

  kapt {
    // correctErrorTypes =true
    keepJavacAnnotationProcessors = true
    javacOptions { option("querydsl.entityAccessors", true) }
    arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
  }

  noArg { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }
  allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withSourcesJar()
  }

  kotlin {
    compilerOptions {
      apiVersion = KotlinVersion.KOTLIN_1_9
      languageVersion = KotlinVersion.KOTLIN_1_9
      jvmTarget = JvmTarget.fromTarget(l.versions.java.get())
      freeCompilerArgs =
        listOf(
          "-Xjsr305=strict",
          "-Xjvm-default=all",
          "-verbose",
          "-Xjdk-release=${l.versions.java.get()}",
          "-jvm-target=${l.versions.java.get()}",
          "-Xextended-compiler-checks"
        )
    }

    jvmToolchain(21)
  }

  tasks {
    withType<AbstractCopyTask> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
    test { useJUnitPlatform() }
    jar { archiveClassifier.set("") }
  }

  publishing {
    repositories {
      maven(url = uri(yunxiaoUrl)) {
        credentials {
          username = yunxiaoUsername
          password = yunxiaoPassword
        }
      }
    }
  }
}

// https://github.com/diffplug/spotless/tree/main/plugin-gradle#quickstart
spotless {
  val license =
    rootProject.layout.projectDirectory
      .file("LICENSE")
      .asFile
      .readLines()
      .map { " * $it" }
      .toMutableList()
      .apply {
        addFirst("/*")
        addLast("*/")
      }
      .joinToString(separator = "\n")
  format("xml") {
    target("**/*.xml")
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    encoding = Charsets.UTF_8
  }
  kotlin {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    licenseHeader(license)
    target("**/**.kt")
    ktfmt().googleStyle().configure {
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setMaxWidth(160)
      it.setRemoveUnusedImport(true)
    }
  }
  kotlinGradle {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    target("**/**.kts")
    ktfmt().googleStyle().configure {
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setMaxWidth(160)
      it.setRemoveUnusedImport(true)
    }
  }
  sql {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
    dbeaver().configFile(".compose-config/.spotless_format_config.properties")
  }
  java {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    licenseHeader(license)
    target("**/**.java")
    importOrder()
    removeUnusedImports()
    googleJavaFormat().aosp().reflowLongStrings()
    formatAnnotations()
  }
}
