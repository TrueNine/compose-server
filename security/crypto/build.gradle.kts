plugins { id("buildlogic.kotlinspring-conventions") }

description = """
Cryptographic utilities and security functions using Spring Security and BouncyCastle.
Provides encryption/decryption, hashing, digital signatures, and secure random number generation.
""".trimIndent()

dependencies {
  api(libs.org.springframework.security.spring.security.crypto)

  implementation(libs.org.bouncycastle.bcprov.jdk18on)
  api(projects.shared)

  testImplementation(libs.com.fasterxml.jackson.core.jackson.databind)
  testImplementation(projects.testtoolkit)
}
