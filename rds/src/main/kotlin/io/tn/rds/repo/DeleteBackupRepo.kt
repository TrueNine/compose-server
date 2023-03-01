package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.DeleteBackupDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DeleteBackupRepo : BaseRepo<DeleteBackupDao, String> {
  @Query(
    """
    from DeleteBackupDao e
    where function('json_extract',e.delSerObj,'$.cct') between :before and :end
  """
  )
  fun findAllByCctBetween(before: LocalDateTime, end: LocalDateTime)
}
