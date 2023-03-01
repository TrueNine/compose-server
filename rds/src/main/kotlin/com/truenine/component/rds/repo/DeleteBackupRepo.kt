package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.DeleteBackupDao
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
