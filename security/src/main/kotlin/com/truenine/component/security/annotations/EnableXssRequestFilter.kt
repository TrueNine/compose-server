package com.truenine.component.security.annotations

import java.lang.annotation.Inherited

@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class EnableXssRequestFilter
