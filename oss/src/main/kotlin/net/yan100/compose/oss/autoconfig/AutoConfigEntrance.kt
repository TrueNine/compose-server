package net.yan100.compose.oss.autoconfig

import net.yan100.compose.oss.properties.AliCloudOssProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

/**
 * @author TrueNine
 * @since 2022-10-28
 */
@ComponentScan("io.tn.oss.autoconfig")
@Import(AliCloudOssProperties::class)
class AutoConfigEntrance
