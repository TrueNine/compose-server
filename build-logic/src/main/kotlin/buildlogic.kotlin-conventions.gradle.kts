import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("buildlogic.java-conventions")
  kotlin("jvm")
}

dependencies {
  implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
  implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
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


// 配置 jar 任务包含 LICENSE 文件
tasks.withType<Jar> {
  from(rootProject.file("LICENSE")) {
    into("META-INF")
  }
}

