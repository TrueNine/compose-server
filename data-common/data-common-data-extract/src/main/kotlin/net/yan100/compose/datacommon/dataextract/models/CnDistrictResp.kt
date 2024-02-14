package net.yan100.compose.datacommon.dataextract.models

import kotlin.properties.Delegates


class CnDistrictResp {
    lateinit var code: CnDistrictCode
    lateinit var name: String
    lateinit var yearVersion: String
    var leaf by Delegates.notNull<Boolean>()
    var level by Delegates.notNull<Int>()
}
