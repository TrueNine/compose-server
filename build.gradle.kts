import com.diffplug.spotless.LineEnding
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  java
  idea
  `maven-publish`
  signing
  alias(libs.plugins.org.springframework.boot)
  alias(libs.plugins.io.spring.dependencyManagement)
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.powerAssert)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.jpa)

  alias(libs.plugins.org.asciidoctor.jvm.convert)
  alias(libs.plugins.com.diffplug.spotless)
  alias(libs.plugins.com.github.benManes.versions)
  alias(libs.plugins.com.google.devtools.ksp)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.allopen)
}

val yunxiaoUrl = extra["url.yunxiao.1"].toString()
val yunxiaoUsername = extra["usr.yunxiao.1"].toString()
val yunxiaoPassword = extra["pwd.yunxiao.1"].toString()
val sonatypeUsername = extra["usr.sonatype.1"].toString()
val sonatypePassword = extra["pwd.sonatype.1"].toString()

val l = libs

project.version = libs.versions.compose.asProvider().get()

tasks {
  withType<ProcessAot> { enabled = false }
  withType<BootJar> { enabled = false }
}

data class Define(
  val url: String? = null,
  val username: String? = null,
  val password: String? = null
)


allprojects {
  project.group = l.versions.composeGroup.get()

  tasks {
    withType<ProcessAot> { enabled = false }
    withType<BootJar> { enabled = false }
  }
}

dependencies {
  implementation(platform(l.org.springframework.boot.springBootDependencies))
  implementation(platform(l.org.springframework.cloud.springCloudDependencies))
  implementation(platform(l.org.springframework.modulith.springModulithBom))
  implementation(platform(l.org.springframework.ai.springAiBom))
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "maven-publish")
  apply(plugin = "signing")
  apply(plugin = l.plugins.org.jetbrains.kotlin.jvm.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.kapt.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.spring.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.jpa.get().pluginId)
  apply(plugin = l.plugins.org.springframework.boot.get().pluginId)
  apply(plugin = l.plugins.io.spring.dependencyManagement.get().pluginId)
  apply(plugin = l.plugins.org.asciidoctor.jvm.convert.get().pluginId)

  extra["snippetsDir"] = file("build/generated-snippets")
  extra["springCloudVersion"] = l.versions.spring.cloud.get()
  extra["springAiVersion"] = l.versions.spring.ai.get()

  tasks {
    withType<ProcessAot> { enabled = false }
    withType<BootJar> { enabled = false }
  }
  dependencies {
    // 自动处理 spring 配置
    annotationProcessor(l.org.springframework.springBootConfigurationProcessor)
    kapt(l.org.springframework.springBootConfigurationProcessor)

    implementation(l.org.springframework.springBootConfigurationProcessor)
    implementation(l.org.springframework.boot.springBoot)
    implementation(l.org.springframework.boot.springBootAutoconfigure)

    implementation(l.bundles.kotlin)

    // junit 全平台 测试
    testRuntimeOnly(l.bundles.kotlinTestJunit5)
    testRuntimeOnly(l.bundles.junit5)
  }
  sourceSets {
    test {
      resources {
        project.rootProject.layout.projectDirectory.also { rootDir ->
          srcDir(rootDir.dir("common-test-resources").asFile.absolutePath)
        }
      }
    }
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withSourcesJar()
  }

  kotlin {
    compilerOptions {
      jvmTarget = JvmTarget.fromTarget(l.versions.java.get())
      freeCompilerArgs = listOf(
        "-Xjsr305=strict", "-Xjvm-default=all", "-verbose", "-Xjdk-release=${l.versions.java.get()}", "-jvm-target=${l.versions.java.get()}"
      )
    }

    jvmToolchain(l.versions.java.get().toInt())
  }

  tasks {
    withType<AbstractCopyTask> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
    test { useJUnitPlatform() }
    jar {
      archiveClassifier.set("")
    }
  }
  tasks.test { outputs.dir(project.extra["snippetsDir"]!!) }
  tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
  }
  publishing {
    repositories {
      mavenLocal()
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
  sql {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
    dbeaver().configFile(".compose-config/.spotless_format_config.properties")
  }
}

tasks.wrapper {
  distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-${l.versions.gradle.get()}-all.zip"
}
