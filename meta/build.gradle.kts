plugins { `kotlin-convention` }

version = libs.versions.compose.asProvider().get()
java {
  val jv = JavaVersion.VERSION_17
  sourceCompatibility = jv
  targetCompatibility = jv
  toolchain { languageVersion.set(JavaLanguageVersion.of(jv.ordinal + 1)) }
  withSourcesJar()
}
dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.annotations)

  testImplementation(projects.testtoolkit)
}
