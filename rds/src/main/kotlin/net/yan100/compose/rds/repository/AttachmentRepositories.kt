package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.Attachment
import net.yan100.compose.rds.entity.LinkedAttachment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepo : BaseRepository<Attachment> {
  @Query(
    """
    SELECT new kotlin.Pair(a.baseUrl,a.saveName)
    FROM Attachment a
    WHERE a.id = :id
  """
  )
  fun findBaseUrlAndSaveNamePairById(id: Long): Pair<String, String>?

  /**
   * 根据id查找附件的全路径
   */
  @Query(
    """
    SELECT b.baseUrl||a.metaName
    FROM Attachment a
    INNER JOIN Attachment b ON a.urlId = b.id
    WHERE a.id = :id
"""
  )
  fun findFullPathById(@Param("id") id: String): String?

  @Query(
    """
    SELECT a.baseUrl||a.metaName
    FROM Attachment a
    WHERE a.metaName LIKE concat(:metaName,'%%') 
    """
  )
  fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: Pageable): Page<String>

  fun existsByBaseUrl(baseUrl: String): Boolean

  /**
   * 根据id，查询 baseUrl符合条件的 baseUrl
   */
  fun findFirstByBaseUrlStartingWith(baseUrl: String): Attachment?
}


@Repository
interface LinkedAttachmentRepo : BaseRepository<LinkedAttachment> {

}
