package net.yan100.compose.rds.entities.attachment

import jakarta.persistence.criteria.Predicate
import net.yan100.compose.rds.core.typing.AttachmentTyping
import org.springframework.data.jpa.domain.Specification

fun <T> LinkedAttachment.toSpec(): Specification<T> = Specification { root, _, builder ->
  val p = mutableListOf<Predicate>()
  attType.let { p += builder.equal(root.get<AttachmentTyping>(Attachment.ATT_TYPE), attType) }
  builder.and(*p.toTypedArray())
}
