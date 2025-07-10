package net.yan100.compose.security.annotations

import java.lang.annotation.Inherited
import org.springframework.security.access.prepost.PreAuthorize

@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@PreAuthorize("isAuthenticated() && hasRole('ROOT')")
annotation class Root
