package io.github.truenine.composeserver.security.annotations

import org.springframework.security.access.prepost.PreAuthorize
import java.lang.annotation.Inherited

@Inherited @MustBeDocumented @Target(AnnotationTarget.FUNCTION) @Retention(AnnotationRetention.RUNTIME) @PreAuthorize("permitAll()") annotation class AllowAll
