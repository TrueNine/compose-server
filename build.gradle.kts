import com.diffplug.spotless.LineEnding
import java.nio.charset.StandardCharsets
import net.yan100.compose.plugin.*
import net.yan100.compose.plugin.consts.Repos.Credentials.yunXiaoPassword
import net.yan100.compose.plugin.consts.Repos.Credentials.yunXiaoUsername
import net.yan100.compose.plugin.consts.Repos.yunXiaoRelese
import net.yan100.compose.plugin.consts.Repos.yunXiaoSnapshot
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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
  alias(libs.plugins.spotless)
  id(libs.plugins.composeGradle.get().pluginId)
}

apply(plugin = libs.plugins.spotless.get().pluginId)

apply(plugin = libs.plugins.composeGradle.get().pluginId)

composeGradle {
  filler {
    license {
      author("TrueNine")
      website("github.com/TrueNine")
      email("truenine304520@gmail.com")
    }
  }
}

val l = libs

project.version = libs.versions.compose.get()

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
    encoding = StandardCharsets.UTF_8
  }
  kotlin {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    licenseHeader(license)
    target("**/**.kt")
    ktfmt().googleStyle()
  }
  kotlinGradle {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    target("**/**.kts")
    ktfmt().googleStyle()
  }
  sql {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
    dbeaver().configFile("dbeaver.properties")
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

allprojects {
  repositories {
    chinaRegionRepositories()
    aliYunXiao()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }

  project.group = "net.yan100.compose"
  project.version = l.versions.compose.get()

  tasks {
    withType<ProcessAot> { enabled = false }
    withType<BootJar> { enabled = false }
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "maven-publish")
  apply(plugin = l.plugins.ktJvm.get().pluginId)
  apply(plugin = l.plugins.ktKapt.get().pluginId)
  apply(plugin = l.plugins.ktLombok.get().pluginId)
  apply(plugin = l.plugins.ktNoArg.get().pluginId)
  apply(plugin = l.plugins.ktAllOpen.get().pluginId)
  apply(plugin = l.plugins.ktSpring.get().pluginId)
  apply(plugin = l.plugins.ktJpa.get().pluginId)
  apply(plugin = l.plugins.springBoot.get().pluginId)
  apply(plugin = l.plugins.hibernateOrm.get().pluginId)
  apply(plugin = l.plugins.springBootDependencyManagement.get().pluginId)
  apply(plugin = l.plugins.composeGradle.get().pluginId)

  extra["springCloudVersion"] = l.versions.spring.cloud.get()

  dependencies {
    annotationProcessor(l.spring.boot.configureprocessor)
    allAnnotationCompileOnly(l.lombok)
    implementation(l.bundles.kt)

    implementation(l.spring.boot.autoconfigure)

    testImplementation(l.bundles.test.springKotlinJunit5)
  }

  dependencyManagement {
    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${l.versions.spring.boot.get()}")
      mavenBom(
        "org.springframework.cloud:spring-cloud-dependencies:${l.versions.spring.cloud.get()}"
      )
      mavenBom(
        "com.alibaba.cloud:spring-cloud-alibaba-dependencies:${l.versions.spring.cloudAlibaba.get()}"
      )
      mavenBom(
        "org.springframework.modulith:spring-modulith-bom:${l.versions.spring.modulith.get()}"
      )
      mavenBom("org.drools:drools-bom:${l.versions.drools.get()}")
    }
  }

  configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

  kapt {
    keepJavacAnnotationProcessors = true
    // correctErrorTypes =true
    javacOptions { option("querydsl.entityAccessors", true) }
    arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
  }

  noArg {
    annotations(
      "jakarta.persistence.MappedSuperclass",
      "jakarta.persistence.Entity",
      "net.yan100.compose.core.annotations.OpenArg"
    )
  }
  allOpen {
    annotations(
      "jakarta.persistence.MappedSuperclass",
      "jakarta.persistence.Entity",
      "net.yan100.compose.core.annotations.OpenArg"
    )
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
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
    withType<AbstractCopyTask> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }

    withType<KotlinCompile> {
      kotlinOptions {
        freeCompilerArgs +=
          listOf(
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

    test { useJUnitPlatform() }

    compileJava {
      options.isFork = true
      options.forkOptions.memoryMaximumSize = "4G"
      options.forkOptions.memoryInitialSize = "2G"
    }

    jar { archiveClassifier.set("") }

    javadoc {
      if (JavaVersion.current().isJava9Compatible)
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
  }

  publishing {
    repositories {
      maven(
        url =
          uri(
            if (version.toString().uppercase().contains("SNAPSHOT")) yunXiaoSnapshot
            else yunXiaoRelese
          )
      ) {
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

rootProject.tasks {
  wrapper { distribute(libs.versions.gradle.get(), "https://mirrors.cloud.tencent.com/gradle") }
}
