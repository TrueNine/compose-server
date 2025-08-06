package io.github.truenine.composeserver.security.annotations

import java.lang.annotation.Inherited
import org.springframework.security.access.prepost.PreAuthorize

@Inherited @MustBeDocumented @Target(AnnotationTarget.FUNCTION) @Retention(AnnotationRetention.RUNTIME) @PreAuthorize("permitAll()") annotation class AllowAll
