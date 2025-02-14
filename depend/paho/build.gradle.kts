plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeDependPaho.get()

dependencies {
  implementation(libs.org.springframework.integration.springIntegrationMqtt)
  implementation(libs.org.eclipse.paho.client.mqttv3)
  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)
  implementation(projects.core)

  testImplementation(projects.testToolkit)
}
