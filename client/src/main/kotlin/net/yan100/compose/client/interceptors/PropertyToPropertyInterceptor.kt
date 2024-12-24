package net.yan100.compose.client.interceptors

import net.yan100.compose.meta.client.ClientProp

abstract class PropertyToPropertyInterceptor : Interceptor<ClientProp, ClientProp> {
  override fun defaultProcess(source: ClientProp): ClientProp = source
}
