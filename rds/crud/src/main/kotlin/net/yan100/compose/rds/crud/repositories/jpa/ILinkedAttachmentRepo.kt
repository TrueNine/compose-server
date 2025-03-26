package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.LinkedAttachment
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("ILinkedAttachmentRepository")
interface ILinkedAttachmentRepo : IRepo<LinkedAttachment> {
  /** ## 根据 baseUrl 查询其下的所有 附件 */
  @Query(
    """
    from LinkedAttachment a
    inner join LinkedAttachment b 
    on a.urlId = b.id
    where b.attType = net.yan100.compose.rds.core.typing.AttachmentTyping.BASE_URL
    and b.baseUrl = :baseUrl
  """
  )
  fun findAllByParentBaseUrl(
    baseUrl: String,
    page: Pageable,
  ): Page<LinkedAttachment>
}
