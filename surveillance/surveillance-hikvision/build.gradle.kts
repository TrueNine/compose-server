plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies {
  implementation(projects.shared)
  implementation(projects.surveillance.surveillanceShared)

  implementation(libs.com.hikvision.ga.artemis.http.client)
}

description =
  "Hikvision surveillance device integration module. " + "Provides SDK wrapper and API clients for Hikvision cameras and surveillance equipment management."
