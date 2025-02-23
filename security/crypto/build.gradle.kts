plugins {
  `kotlinspring-convention`
}

version = libs.versions.compose.security.crypto.get()

dependencies {
  api(libs.org.springframework.security.spring.security.crypto)

  implementation(libs.org.bouncycastle.bcprov.jdk18on)
  implementation(projects.core)

  testImplementation(libs.com.fasterxml.jackson.core.jackson.databind)
  testImplementation(projects.testtoolkit)
}
