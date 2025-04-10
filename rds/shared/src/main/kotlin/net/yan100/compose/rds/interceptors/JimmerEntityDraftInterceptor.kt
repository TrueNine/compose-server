package net.yan100.compose.rds.interceptors

import net.yan100.compose.datetime
import net.yan100.compose.rds.entities.IJimmerEntity
import net.yan100.compose.rds.entities.IJimmerEntityDraft
import net.yan100.compose.rds.entities.IJimmerEntityProps
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.meta.TypedProp
import org.babyfish.jimmer.sql.DraftInterceptor

class JimmerEntityDraftInterceptor :
  DraftInterceptor<IJimmerEntity, IJimmerEntityDraft> {
  override fun dependencies(): Collection<TypedProp<IJimmerEntity, *>> {
    return listOf(
      IJimmerEntityProps.ID,
      IJimmerEntityProps.CRD,
      IJimmerEntityProps.MRD,
      IJimmerEntityProps.RLV,
      IJimmerEntityProps.LDF,
    )
  }

  override fun beforeSaveAll(
    items: Collection<DraftInterceptor.Item<IJimmerEntity, IJimmerEntityDraft>>
  ) {
    for (it in items) {
      beforeSave(it.draft, it.original)
    }
  }

  override fun beforeSave(draft: IJimmerEntityDraft, original: IJimmerEntity?) {
    if (!isLoaded(draft, IJimmerEntity::id)) {
      if (original === null) {
        draft.crd = datetime.now()
        draft.rlv = 0
      }
    } else {
      draft.mrd = datetime.now()
    }
  }
}
