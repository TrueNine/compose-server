package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private val cache: ConcurrentMap<KSTypeReference, KSType> = ConcurrentHashMap()

/** 利用缓存调用[KSTypeReference.resolve] */
fun KSTypeReference.fastResolve(): KSType =
  cache.computeIfAbsent(this) { it.resolve() }

/** 作用等效于 [KSTypeReference.resolve].[KSType.declaration] */
val KSTypeReference.resolveDeclaration: KSDeclaration
  get() = this.fastResolve().declaration
