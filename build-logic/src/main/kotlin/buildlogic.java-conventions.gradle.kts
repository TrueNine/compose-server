plugins {
  `java-library`
  idea
  id("buildlogic.publish-conventions")
  id("buildlogic.repositories-conventions")
  id("buildlogic.jacoco-conventions")
}

group = "net.yan100.compose"
version = "4.0.0"

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
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
  toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
  withSourcesJar()
}

tasks.test {
  useJUnitPlatform()
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
