import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("buildlogic.java-conventions")
  kotlin("jvm")
  id("org.jetbrains.dokka")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
  implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
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

// 配置 Dokka V2 文档生成
dokka {
  moduleName.set(project.name)
  
  dokkaSourceSets.configureEach {
    // 跳过空包
    skipEmptyPackages.set(true)
    
    // 配置外部文档链接
    externalDocumentationLinks.register("java") {
      url("https://docs.oracle.com/en/java/javase/21/docs/api/")
      packageListUrl("https://docs.oracle.com/en/java/javase/21/docs/api/element-list")
    }
    
    externalDocumentationLinks.register("kotlin") {
      url("https://kotlinlang.org/api/latest/jvm/stdlib/")
      packageListUrl("https://kotlinlang.org/api/latest/jvm/stdlib/package-list")
    }
    
    externalDocumentationLinks.register("spring") {
      url("https://docs.spring.io/spring-framework/docs/current/javadoc-api/")
      packageListUrl("https://docs.spring.io/spring-framework/docs/current/javadoc-api/package-list")
    }
  }
}

