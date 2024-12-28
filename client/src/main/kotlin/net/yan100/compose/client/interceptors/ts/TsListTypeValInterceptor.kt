package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.TsPostReferenceInterceptor
import net.yan100.compose.meta.client.ClientType

open class TsListTypeValInterceptor : TsPostReferenceInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_TS_REFERENCES

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean {
    return false
  }

  override fun process(ctx: KtToTsContext, source: ClientType): TsTypeVal {
    return TsTypeVal.Array(
      TsGeneric.Used(
        used = TsTypeVal.Never,
        index = 0
      )
    )
  }
}
