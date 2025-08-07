package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.depend.servlet.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class) @SpringBootApplication internal class DependServletEntrance
