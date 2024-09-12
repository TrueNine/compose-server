package net.yan100.compose.ksp

import com.google.devtools.ksp.symbol.KSDeclaration

val KSDeclaration.sName: String get() = simpleName.asString()
val KSDeclaration.shName: String get() = simpleName.getShortName()
