package net.yan100.compose.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun Resolver.getClassDeclarationByRuntimeName(
  name: String,
): KSClassDeclaration? {
  return getClassDeclarationByName(name.replace("\$", "."))
}
