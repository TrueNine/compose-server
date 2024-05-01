package net.yan100.compose.ksp.ksp.dsl

interface KDocAdaptor<BuilderTypeVar> {
  fun doc(format: String, args: Array<out Any>, fn: (String, Array<out Any>) -> BuilderTypeVar): BuilderTypeVar {
    return fn(format, args)
  }
}
