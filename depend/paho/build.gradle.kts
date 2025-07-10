plugins {
  id("buildlogic.kotlinspring-conventions")
}

description = """
Eclipse Paho MQTT client integration for IoT and real-time messaging applications.
Provides Spring Integration MQTT support with reliable message delivery and connection management.
""".trimIndent()

dependencies {
  implementation(libs.org.springframework.integration.spring.integration.mqtt)
  implementation(libs.org.eclipse.paho.client.mqttv3)
  implementation(libs.com.fasterxml.jackson.core.jackson.databind)
  api(projects.shared)

  testImplementation(projects.testtoolkit)
}
