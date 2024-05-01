package net.yan100.compose.ksp.ksp.dsl

interface StandardBuilderAdaptor<T, R> {
  fun build(): R
  val builder: T
}
