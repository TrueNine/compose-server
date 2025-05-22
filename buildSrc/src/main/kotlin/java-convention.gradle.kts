import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  `java-library`
  idea
  id("publish-convention")
  id("repositories-convention")
}

group = libs.versions.group.get()

configurations.all {
  resolutionStrategy {
    dependencySubstitution {
      val jpa = libs.com.querydsl.querydsl.jpa.get()
      val apt = libs.com.querydsl.querydsl.apt.get()
      substitute(module(jpa.module.toString()))
        .using(
          module(
            jpa.module.toString() + ":" + jpa.version
          )
        )
        .withClassifier("jakarta")
      substitute(module(apt.module.toString()))
        .using(
          module(
            apt.module.toString() + ":" + apt.version
          )
        )
        .withClassifier("jakarta")
    }
  }
}

java {
  sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
  targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
  toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get())) }
  withSourcesJar()
}

tasks.test {
  useJUnitPlatform()
  // 允许 java agent 动态代理
  jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
}

tasks.jar {
  archiveClassifier = ""
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

tasks.javadoc {
  enabled = false
}
