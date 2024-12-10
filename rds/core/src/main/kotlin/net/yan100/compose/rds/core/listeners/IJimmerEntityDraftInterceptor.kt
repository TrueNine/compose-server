package net.yan100.compose.rds.core.listeners

import net.yan100.compose.core.datetime
import net.yan100.compose.rds.core.entities.IJimmerEntity
import net.yan100.compose.rds.core.entities.IJimmerEntityDraft
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor

class IJimmerEntityDraftInterceptor : DraftInterceptor<IJimmerEntity, IJimmerEntityDraft> {
  override fun beforeSave(draft: IJimmerEntityDraft, original: IJimmerEntity?) {
    if (!isLoaded(draft, IJimmerEntity::databaseMetadata)) {
      if (original === null) {
        draft.databaseMetadata {
          crd = datetime.now()
          mrd = null
          rlv = 0
          ldf = null
        }
      } else {
        draft.databaseMetadata {
          mrd = datetime.now()
          rlv?.also { rlv = it + 1 }
        }
      }
    }
  }
}
