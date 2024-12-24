package net.yan100.compose.client.interceptors

import net.yan100.compose.meta.client.ClientType

abstract class TypeToTypeInterceptor : Interceptor<ClientType, ClientType> {
  override fun defaultProcess(source: ClientType): ClientType = source
}
