package io.github.truenine.composeserver.pay

import io.github.truenine.composeserver.pay.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class) @SpringBootApplication internal class PayEntrance
