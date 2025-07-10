package io.github.truenine.composeserver.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.addFileCommentLine(format: String, vararg args: Any) = apply { addFileComment("$format\n", *args) }

fun FileSpec.Builder.addDebugInfoComment(declaration: KSDeclaration) = addFileCommentLine(declaration.debugInfo())
