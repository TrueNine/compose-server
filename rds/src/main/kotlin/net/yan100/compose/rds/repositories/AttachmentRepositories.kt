package net.yan100.compose.rds.repositories


import net.yan100.compose.rds.entities.Attachment
import net.yan100.compose.rds.entities.LinkedAttachment
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface IAttachmentRepo : IRepo<Attachment> {
  @Query(
    """
    select new kotlin.Pair(a.baseUrl,a.saveName)
    from Attachment a
    where a.id = :id
  """
  )
  fun findBaseUrlAndSaveNamePairById(id: Long): Pair<String, String>?

  @Query("""select a.metaName from Attachment a where a.id = :id""")
  fun findMetaNameById(id: String): String?

  @Query("""select a.saveName from Attachment a where a.id = :id""")
  fun findSaveNameById(id: String): String?

  /**
   * 根据id查找附件的全路径
   */
  @Query(
    """
    select b.baseUrl||a.metaName
    from Attachment a
    inner join Attachment b ON a.urlId = b.id
    where a.id = :id
"""
  )
  fun findFullPathById(@Param("id") id: String): String?

  @Query(
    """
    select a.baseUrl||a.metaName
    from Attachment a
    where a.metaName LIKE concat(:metaName,'%%') 
    """
  )
  fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: Pageable): Page<String>

  /**
   * ## 根据 baseUrl 查询其下的所有 附件
   */
  @Query(
    """
    from Attachment a
    inner join Attachment b ON a.urlId = b.id
    where b.attType = net.yan100.compose.rds.typing.AttachmentTyping.BASE_URL
    and b.baseUrl = :baseUrl
  """
  )
  fun findAllByParentBaseUrl(baseUrl: String, page: Pageable): Page<Attachment>

  fun existsByBaseUrl(baseUrl: String): Boolean

  /**
   * 根据id，查询 baseUrl符合条件的 baseUrl
   */
  fun findFirstByBaseUrl(baseUrl: String): Attachment?

  /**
   * 根据 baseUrl 查询其下的所有 附件
   */
  fun findAllByBaseUrlIn(baseUrls: List<String>): List<Attachment>
}


@Repository
interface ILinkedAttachmentRepo : IRepo<LinkedAttachment> {
  /**
   * ## 根据 baseUrl 查询其下的所有 附件
   */
  @Query(
    """
    from LinkedAttachment a
    inner join LinkedAttachment b 
    on a.urlId = b.id
    where b.attType = net.yan100.compose.rds.typing.AttachmentTyping.BASE_URL
    and b.baseUrl = :baseUrl
  """
  )
  fun findAllByParentBaseUrl(baseUrl: String, page: Pageable): Page<LinkedAttachment>
}
