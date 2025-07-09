plugins { id("buildlogic.kotlinspring-conventions") }

version = libs.versions.compose.security.get()

dependencies {
  api(libs.org.springframework.security.spring.security.crypto)

  implementation(libs.org.bouncycastle.bcprov.jdk18on)
  api(projects.shared)

  testImplementation(libs.com.fasterxml.jackson.core.jackson.databind)
  testImplementation(projects.testtoolkit)
}
