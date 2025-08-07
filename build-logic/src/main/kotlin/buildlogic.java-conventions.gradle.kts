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
  withSourcesJar()
  toolchain {
    languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
  }
}

// Configure javadoc jar after all plugins are applied
afterEvaluate {
  // Only create standard javadoc jar if Dokka plugin is not applied
  // (Dokka will create its own javadoc jar via the publishing conventions)
  if (!plugins.hasPlugin("org.jetbrains.dokka")) {
    java {
      withJavadocJar()
    }
  }
}

tasks.withType<Jar> {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  // 只对主 jar 任务设置空的 classifier 和排除源码文件
  if (name == "jar") {
    archiveClassifier = ""
    // 确保主 jar 任务排除源码文件（只包含编译后的 .class 文件）
    exclude("**/*.kt")
  }

  // 所有 jar 任务都包含 LICENSE 文件
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
    mustRunAfter(tasks.withType<Javadoc>())
    tasks.findByName("javadocJar")?.let { mustRunAfter(it) }
    tasks.findByName("sourcesJar")?.let { mustRunAfter(it) }

    dependsOn(configurations.testRuntimeClasspath)

    // Configure JVM args to suppress warnings
    jvmArgs(
      "-XX:+EnableDynamicAgentLoading",
      "-Djdk.instrument.traceUsage=false",
      "-Xshare:off",
      "--add-opens=java.base/sun.misc=ALL-UNNAMED",
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--enable-native-access=ALL-UNNAMED",
      "-Dio.netty.tryReflectionSetAccessible=true",
      "-Dio.netty.noUnsafe=false",
      "-Dnet.bytebuddy.experimental=true",
      "-Dnet.bytebuddy.dump=${System.getProperty("java.io.tmpdir")}",
      "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED",
      "--add-exports=java.base/sun.misc=ALL-UNNAMED"
    )

    // Suppress specific warnings by redirecting stderr for test processes
    systemProperty("java.util.logging.config.file", "")
    systemProperty("sun.misc.unsafe.disableWarnings", "true")

    // Redirect stderr to filter out ByteBuddy warnings
    environment("JAVA_TOOL_OPTIONS", "-XX:+EnableDynamicAgentLoading -Djdk.instrument.traceUsage=false")

    // Configure test output to suppress warnings
    testLogging {
      showStandardStreams = false
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      events("passed", "skipped", "failed")
    }

    val commonProjectDeps = listOf("testtoolkit", "shared")

    listOf(
      configurations.testImplementation.get(),
      configurations.testRuntimeOnly.get(),
      configurations.testCompileOnly.get()
    ).forEach { config ->
      config.dependencies.forEach { dep ->
        if (commonProjectDeps.contains(dep.name)) {
          val depProject = project.findProject(":${dep.name}")
          if (depProject != null) {
            val sourcesJarTask = depProject.tasks.findByName("sourcesJar")
            val javadocJarTask = depProject.tasks.findByName("javadocJar")

            if (sourcesJarTask != null) {
              dependsOn(":${dep.name}:sourcesJar")
            }
            if (javadocJarTask != null) {
              dependsOn(":${dep.name}:javadocJar")
            }
          }
        }
      }
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-parameters")
}

tasks.withType<Wrapper> {
  distributionType = Wrapper.DistributionType.ALL
}
