package net.yan100.compose.security.annotations

import java.lang.annotation.Inherited
import org.springframework.security.access.prepost.PreAuthorize

@Inherited
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("isAuthenticated() && hasAnyRole('ROOT','ADMIN')")
annotation class Admin
