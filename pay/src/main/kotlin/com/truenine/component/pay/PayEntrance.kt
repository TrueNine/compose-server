package com.truenine.component.pay

import com.truenine.component.pay.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class)
@SpringBootApplication
open class PayEntrance


