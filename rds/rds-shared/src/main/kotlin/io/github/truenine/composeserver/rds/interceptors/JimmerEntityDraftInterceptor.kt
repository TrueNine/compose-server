package io.github.truenine.composeserver.rds.interceptors

import io.github.truenine.composeserver.instant
import io.github.truenine.composeserver.rds.entities.IEntity
import io.github.truenine.composeserver.rds.entities.IEntityDraft
import io.github.truenine.composeserver.rds.entities.IEntityProps
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.meta.TypedProp
import org.babyfish.jimmer.sql.DraftInterceptor

class JimmerEntityDraftInterceptor : DraftInterceptor<IEntity, IEntityDraft> {
  override fun dependencies(): Collection<TypedProp<IEntity, *>> {
    return listOf(IEntityProps.ID, IEntityProps.CRD, IEntityProps.MRD, IEntityProps.RLV, IEntityProps.LDF)
  }

  override fun beforeSaveAll(items: Collection<DraftInterceptor.Item<IEntity, IEntityDraft>>) {
    for (it in items) {
      beforeSave(it.draft, it.original)
    }
  }

  override fun beforeSave(draft: IEntityDraft, original: IEntity?) {
    if (!isLoaded(draft, IEntity::id)) {
      if (original === null) {
        draft.crd = instant.now()
        draft.rlv = 0
      }
    } else {
      draft.mrd = instant.now()
    }
  }
}
