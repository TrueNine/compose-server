package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.AttachmentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository : BaseRepository<AttachmentEntity> {
  @Query(
    """
    select new kotlin.Pair(a.baseUrl,a.saveName)
    from AttachmentEntity a
    where a.id = :id
  """
  )
  fun findBaseUrlAndSaveNamePairById(id: Long): Pair<String, String>?

  @Query(
    """
    select a.baseUrl||a.saveName
    from AttachmentEntity a
    where a.id = :id
  """
  )
  fun findFullPathById(id: Long): String?

  @Query(
    """
    select a.baseUrl||a.saveName
    from AttachmentEntity a
    where a.metaName like concat(:metaName,'%%') 
    """
  )
  fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: Pageable): Page<String>

  fun existsByBaseUrl(baseUrl: String): Boolean

  fun findByBaseUrlStartingWith(baseUrl: String): AttachmentEntity?
}
