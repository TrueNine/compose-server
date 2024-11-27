import com.diffplug.spotless.LineEnding
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  java
  `maven-publish`
  signing
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  //alias(libs.plugins.io.spring.dependencyManagement)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)
  alias(libs.plugins.org.jetbrains.kotlin.plugin.jpa)
  alias(libs.plugins.com.diffplug.spotless)
  alias(libs.plugins.com.github.benManes.versions)
}

val yunxiaoUrl = extra["url.yunxiao.1"].toString()
val yunxiaoUsername = extra["usr.yunxiao.1"].toString()
val yunxiaoPassword = extra["pwd.yunxiao.1"].toString()
val sonatypeUsername = extra["usr.sonatype.1"].toString()
val sonatypePassword = extra["pwd.sonatype.1"].toString()

val l = libs

subprojects {
  apply(plugin = "java")
  apply(plugin = "maven-publish")
  apply(plugin = "signing")
  apply(plugin = l.plugins.org.jetbrains.kotlin.jvm.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.kapt.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.spring.get().pluginId)
  apply(plugin = l.plugins.org.jetbrains.kotlin.plugin.jpa.get().pluginId)

  project.group = l.versions.composeGroup.get()

  extra["snippetsDir"] = file("build/generated-snippets")
  extra["springCloudVersion"] = l.versions.springCloud.get()
  extra["springAiVersion"] = l.versions.springAi.get()

  dependencies {
    //implementation(platform(l.org.springframework.boot.springBootDependencies))
    //implementation(platform(l.org.springframework.cloud.springCloudDependencies))
    //implementation(platform(l.org.springframework.modulith.springModulithBom))
    //implementation(platform(l.org.springframework.ai.springAiBom))

    // junit 全平台 测试
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
        "-Xjsr305=strict",
        "-Xjvm-default=all",
        "-verbose",
        "-Xjdk-release=${l.versions.java.get()}",
        "-jvm-target=${l.versions.java.get()}"
      )
    }
    jvmToolchain(l.versions.java.get().toInt())
  }

  kapt {
    correctErrorTypes = true
    keepJavacAnnotationProcessors = true
  }

  tasks {
    //withType<AbstractCopyTask> { duplicatesStrategy = DuplicatesStrategy.INCLUDE }
    test { useJUnitPlatform() }
    jar {
      archiveClassifier = ""
    }
  }
  tasks.test { outputs.dir(project.extra["snippetsDir"]!!) }
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

  afterEvaluate {
    publishing.publications?.withType<MavenPublication>()?.forEach { pub ->
      pub.pom {
        name = "${rootProject.name}-${project.name}"
        description = project.description
        url = "https://github.com/TrueNine/compose-server"
        licenses {
          license {
            name = "The private license of TrueNine"
            url = "https://github.com/TrueNine/compose-server/blob/main/LICENSE"
          }
        }
        developers {
          developer {
            id = "TrueNine"
            name = "赵日天"
            email = "truenine304520@gmail.com"
          }
        }
        scm {
          connection = "scm:git:git://github.com/TrueNine/compose-server.git"
          developerConnection = "scm:git:ssh://github.com:/TrueNine/compose-server.git"
          url = "https://github.com/TrueNine/compose-server"
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
