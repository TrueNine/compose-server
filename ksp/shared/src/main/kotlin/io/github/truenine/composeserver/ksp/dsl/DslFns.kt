package io.github.truenine.composeserver.ksp.dsl

import com.squareup.kotlinpoet.FileSpec

fun fileDsl(packageName: String, fileName: String, receiver: KFileSpecScope.() -> Unit): FileSpec {
  val fb = KFileSpecScope(packageName, fileName)
  receiver(fb)
  return fb.build()
}
