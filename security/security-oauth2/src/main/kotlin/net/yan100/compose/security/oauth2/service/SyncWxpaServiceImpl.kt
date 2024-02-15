package net.yan100.compose.security.oauth2.service

import net.yan100.compose.security.oauth2.property.WxpaProperty
import org.springframework.stereotype.Service

@Service("SyncWxpaServiceImpl")
class SyncWxpaServiceImpl(
    private val wxpa: IWxpaService,
    private val p: WxpaProperty
) : IWxpaService by wxpa
