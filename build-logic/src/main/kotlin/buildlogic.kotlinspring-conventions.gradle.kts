val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
plugins {
  id("buildlogic.javaspring-conventions") 
  id("buildlogic.kotlin-conventions")
  kotlin("plugin.spring")
  kotlin("kapt")
}

dependencies {
  kapt(libs.org.springframework.boot.spring.boot.configuration.processor)
}

// 配置 jar 任务包含 LICENSE 文件
tasks.withType<Jar> {
  from(rootProject.file("LICENSE")) {
    into("META-INF")
  }
}
