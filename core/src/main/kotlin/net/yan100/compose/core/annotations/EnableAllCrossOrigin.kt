package net.yan100.compose.core.annotations

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Import
import org.springframework.web.cors.CorsConfiguration
import java.lang.annotation.Inherited

/**
 * 开启全部跨域
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
  CorsConfiguration::class
)
@ConditionalOnWebApplication
annotation class EnableAllCrossOrigin
