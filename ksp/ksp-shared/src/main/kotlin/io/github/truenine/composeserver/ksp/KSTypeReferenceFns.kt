package io.github.truenine.composeserver.ksp

import com.google.devtools.ksp.symbol.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private val cache: ConcurrentMap<KSTypeReference, KSType> = ConcurrentHashMap()

/** Call [KSTypeReference.resolve] using a cache */
fun KSTypeReference.fastResolve(): KSType = cache.computeIfAbsent(this) { it.resolve() }

/** Equivalent to [KSTypeReference.resolve].[KSType.declaration] */
val KSTypeReference.resolveDeclaration: KSDeclaration
  get() = this.fastResolve().declaration
