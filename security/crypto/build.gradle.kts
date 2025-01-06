plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeSecurityCrypto.get()

dependencies {
  api(libs.org.springframework.security.springSecurityCrypto)

  implementation(libs.org.bouncycastle.bcprovJdk18on)
  implementation(projects.core)

  testImplementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  testImplementation(projects.testToolkit)
}
