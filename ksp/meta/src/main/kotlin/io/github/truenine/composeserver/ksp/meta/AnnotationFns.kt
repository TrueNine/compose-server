package io.github.truenine.composeserver.ksp.meta

import io.github.truenine.composeserver.ksp.meta.annotations.MetaName

fun Sequence<MetaName>.getFirstName(): String? {
  val f = firstOrNull()
  val value = f?.name
  val name = f?.value
  if (!value.isNullOrBlank()) return value
  if (!name.isNullOrBlank()) return name
  return null
}
