plugins {
  id("buildlogic.kotlin-conventions")
}

java {
  val jv = JavaVersion.VERSION_17
  sourceCompatibility = jv
  targetCompatibility = jv
  toolchain { languageVersion.set(JavaLanguageVersion.of(jv.ordinal + 1)) }
  withSourcesJar()
}

dependencies {
  api(libs.com.google.devtools.ksp.symbol.processing.api)
  api(libs.com.squareup.kotlinpoet.jvm)
  api(libs.com.squareup.kotlinpoet.ksp)
  api(projects.meta)
}
