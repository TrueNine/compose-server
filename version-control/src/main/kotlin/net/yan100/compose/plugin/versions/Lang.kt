package net.yan100.compose.plugin.versions

object Lang {
  // https://bell-sw.com/
  val javaPlatform = org.gradle.api.JavaVersion.VERSION_21
  const val java = "21"

  // https://github.com/JetBrains/kotlin/releases
  const val kotlin = "1.9.20-Beta2"

  // https://mvnrepository.com/artifact/org.projectlombok/lombok
  const val lombok = "1.18.30"

  // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-reactor
  const val kotlinxCoroutine = "1.7.3"

  // https://mvnrepository.com/artifact/io.projectreactor.kotlin/reactor-kotlin-extensions
  const val reactorKotlinExtension = "1.2.2"

  /**
   * slf4j api
   * @see [maven](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)
   */
  const val slf4jApi = "2.0.9"
}
