package io.github.truenine.composeserver.security.crypto.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan("io.github.truenine.composeserver.security.crypto.autoconfig") @Import(PasswordEncoderAutoconfiguration::class) class AutoConfigEntrance
