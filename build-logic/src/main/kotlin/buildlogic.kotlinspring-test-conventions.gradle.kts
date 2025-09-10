import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  `java-library`
  id("buildlogic.repositories-conventions")
  id("buildlogic.jacoco-conventions")
  kotlin("jvm")
  kotlin("plugin.spring")
  kotlin("kapt")
}

group = libs.versions.group.get()
version = libs.versions.project.get()

configurations.all {
  resolutionStrategy {
    dependencySubstitution {
      substitute(module("com.querydsl:querydsl-jpa"))
        .using(module("com.querydsl:querydsl-jpa:5.1.0"))
        .withClassifier("jakarta")
      substitute(module("com.querydsl:querydsl-apt"))
        .using(module("com.querydsl:querydsl-apt:5.1.0"))
        .withClassifier("jakarta")
    }
  }
}

java {
  sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get().toInt())
  targetCompatibility = JavaVersion.toVersion(libs.versions.java.get().toInt())
  toolchain {
    languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
  }
}

dependencies {
  implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
  implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
  implementation(libs.org.springframework.boot.spring.boot.autoconfigure)
  kapt(libs.org.springframework.boot.spring.boot.configuration.processor)
  annotationProcessor(libs.org.springframework.boot.spring.boot.configuration.processor)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
    freeCompilerArgs = listOf(
      "-Xjsr305=strict", "-Xjvm-default=all"
    )
  }
  jvmToolchain(libs.versions.java.get().toInt())
}

// 确保 kotlin_module 文件被正确生成
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  compilerOptions {
    // 确保模块信息被正确生成
    javaParameters.set(true)
  }
}

tasks.withType<Jar> {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  
  // 配置 jar 任务包含 LICENSE 文件
  from(rootProject.file("LICENSE")) {
    into("META-INF")
  }
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter(libs.versions.org.junit.jupiter.get())
    }
  }
}

afterEvaluate {
  tasks.withType<Test>().configureEach {
    dependsOn(configurations.testRuntimeClasspath)

    // Configure test output for detailed logging
    testLogging {
      showStandardStreams = true
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      events("passed", "skipped", "failed", "started")
      showExceptions = true
      showCauses = true
      showStackTraces = true
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}