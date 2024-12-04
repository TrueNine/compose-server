package net.yan100.compose.rds.crud.repositories.jimmer

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IJimmerRepo
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.crud.dto.jimmer.attachment.LinkedAttachmentSpec
import net.yan100.compose.rds.crud.dto.jimmer.attachment.LinkedAttachmentView
import net.yan100.compose.rds.crud.entities.jimmer.Attachment
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IJimmerAttachmentRepo : IJimmerRepo<Attachment, RefId> {
  fun findFilesBy(
    spec: LinkedAttachmentSpec? = null,
  ): List<LinkedAttachmentView> {
    return sql.createQuery(Attachment::class) {
      where(spec?.run { copy(attType = AttachmentTyping.ATTACHMENT) })
      select(table.fetch(LinkedAttachmentView::class))
    }.execute()
  }

  fun findUrlsBy(
    spec: LinkedAttachmentSpec? = null,
  ): List<LinkedAttachmentView> {
    return sql.createQuery(Attachment::class) {
      where(spec?.run { copy(attType = AttachmentTyping.BASE_URL) })
      select(table.fetch(LinkedAttachmentView::class))
    }.execute()
  }
}
