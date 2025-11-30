package io.github.truenine.composeserver.security.annotations

import org.springframework.security.access.prepost.PreAuthorize
import java.lang.annotation.Inherited

@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@PreAuthorize("isAuthenticated() && hasRole('ROOT')")
annotation class Root
