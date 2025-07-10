package net.yan100.compose.security.crypto.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan("net.yan100.compose.security.crypto.autoconfig") @Import(PasswordEncoderAutoconfiguration::class) class AutoConfigEntrance
