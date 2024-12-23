package net.yan100.compose.client.interceptors

interface Interceptor<T : Any> {
  fun supported(source: T): Boolean
  fun process(source: T): T
}
