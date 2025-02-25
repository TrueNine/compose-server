import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  `java-library`
  idea
  id("publish-convention")
  id("repositories-convention")
}

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

sourceSets {
  test {
    resources {
      project.rootProject.layout.projectDirectory.also { rootDir ->
        srcDir(rootDir.dir("common-test-resources").asFile.absolutePath)
      }
    }
  }
}

tasks.test {
  useJUnitPlatform()
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

fun loadEnv(): MutableMap<String, String> {
  val env = mutableMapOf<String, String>()
  val envPath = extra.takeIf { it.has("env.file") }?.get("env.file")?.toString() ?: ".env"
  logger.info("loadEnv envPath: $envPath")
  val envFile = file(envPath).takeIf { it.exists() } ?: rootProject
    .layout.projectDirectory.file(".env").asFile.takeIf { it.exists() }
  if (envFile == null) {
    logger.error("Error: path $envPath is not exists. add gradle.properties env.file path or add .env file to root project")
    return env.also {
      logger.error("Error: loadEnv is empty")
      it["ENV_FILE_EMPTY"] = "true"
    }
  }

  envFile.readLines().filter { it.isNotBlank() && !it.trimStart().startsWith("#") }
    .forEach {
      val parts = it.split("=".toRegex(), 2)
      if (parts.size >= 2) {
        env[parts[0].trim()] = parts[1].trim().removeSurrounding("\"")
      }
    }
  return env
}

fun configureTaskEnvironment(task: Task) {
  if (task !is JavaForkOptions) {
    logger.error("Error: task is not JavaForkOptions")
    return
  }
  task.doFirst {
    task.environment.putAll(loadEnv())
  }
}

tasks.withType<Test>().configureEach {
  configureTaskEnvironment(this)
}

tasks.withType<JavaExec>().configureEach {
  configureTaskEnvironment(this)
}
