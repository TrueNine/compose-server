package io.github.truenine.composeserver.ksp.meta.annotations

import java.lang.annotation.Inherited

@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class MetaGeneratedInfo(val value: String = "")
