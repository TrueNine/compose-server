package net.yan100.compose.client.interceptors

interface Interceptor<S : Any, T : Any> {
  fun supported(source: S): Boolean
  fun process(source: S): T
  fun defaultProcess(source: S): T
}
