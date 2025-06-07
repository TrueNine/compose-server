plugins {
  `kotlinspring-convention`
}

version = libs.versions.compose.depend.get()

dependencies {
  implementation(libs.org.springframework.integration.spring.integration.mqtt)
  implementation(libs.org.eclipse.paho.client.mqttv3)
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  api(projects.shared)

  testImplementation(projects.testtoolkit)
}
