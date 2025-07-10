package net.yan100.compose.depend.servlet

import net.yan100.compose.depend.servlet.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class) @SpringBootApplication internal class DependServletEntrance
