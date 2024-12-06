package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.core.RefId
import net.yan100.compose.core.i64
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.entities.IJimmerEntity
import net.yan100.compose.rds.core.typing.AttachmentTyping
import org.babyfish.jimmer.Formula
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.OneToOne

@Entity
interface Attachment : IJimmerEntity {
  val attType: AttachmentTyping
  val metaName: String?
  val saveName: String?
  val baseUrl: String?
  val baseUri: String?
  val urlName: String?
  val urlDoc: String?

  @Formula(dependencies = ["baseUrl", "baseUri"])
  val linkedUrl: String? get() = if (baseUrl.isNullOrBlank()) null else (baseUrl ?: "") + (baseUri ?: "")

  @OneToOne
  @JoinColumn(name = "url_id")
  val parentUrl: Attachment?

  @IdView("parentUrl")
  val urlId: RefId?

  val size: i64?
  val mimeType: string?
}
