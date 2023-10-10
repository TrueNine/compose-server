package io.tnt

import io.tnt.properties.MqttProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties(MqttProperties::class)
@SpringBootApplication
class ApplicationRunner

fun main(args: Array<String>) {
  SpringApplication.run(ApplicationRunner::class.java, *args)
}
