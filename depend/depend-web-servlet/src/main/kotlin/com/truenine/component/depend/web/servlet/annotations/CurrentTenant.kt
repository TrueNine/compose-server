package com.truenine.component.depend.web.servlet.annotations


import java.lang.annotation.Inherited

@Deprecated(message = "暂时不考虑租户设计", level = DeprecationLevel.WARNING)
@MustBeDocumented
@Inherited
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentTenant
