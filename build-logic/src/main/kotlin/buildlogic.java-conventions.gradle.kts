val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  `java-library`
  id("buildlogic.publish-conventions")
  id("buildlogic.repositories-conventions")
  id("buildlogic.jacoco-conventions")
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
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
  }
}

tasks.withType<Jar> {
  archiveClassifier = ""
  // 确保 kotlin_module 文件被正确包含
  from(sourceSets.main.get().output)
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter(libs.versions.org.junit.jupiter.get())
    }
  }
}

// 修复 Gradle 9.0.0-rc-1 的隐式依赖问题
// 使用 afterEvaluate 确保在配置阶段之后执行
afterEvaluate {
  tasks.withType<Test>().configureEach {
    // 确保测试任务在当前项目的jar任务之后运行
    mustRunAfter(tasks.withType<Javadoc>())
    tasks.findByName("javadocJar")?.let { mustRunAfter(it) }
    tasks.findByName("sourcesJar")?.let { mustRunAfter(it) }
    
    // 解决跨项目依赖的隐式依赖问题
    dependsOn(configurations.testRuntimeClasspath)
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

tasks.withType<Wrapper> {
  distributionType = Wrapper.DistributionType.ALL
}
