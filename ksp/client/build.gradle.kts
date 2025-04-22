plugins { `kotlin-convention` }

version = libs.versions.compose.build.get()
java {
  val jv = JavaVersion.VERSION_17
  sourceCompatibility = jv
  targetCompatibility = jv
  toolchain { languageVersion.set(JavaLanguageVersion.of(jv.ordinal + 1)) }
  withSourcesJar()
}
dependencies {
  compileOnly(libs.com.google.devtools.ksp.symbol.processing.api)

  implementation(libs.com.squareup.kotlinpoet.jvm)
  implementation(libs.com.squareup.kotlinpoet.ksp)

  api(projects.shared)
  implementation(projects.meta)
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  implementation(libs.com.fasterxml.jackson.module.jackson.module.kotlin)

  implementation(projects.ksp.kspShared)
}
