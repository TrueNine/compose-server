plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies {
  implementation(projects.shared)
  implementation(projects.surveillance.surveillanceShared)

  implementation(libs.com.hikvision.ga.artemis.http.client)
  implementation(libs.com.hikvision.ga.opensource)
}
