package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.Dept
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface DeptRepo : BaseRepository<Dept> {
  @Query("""
    FROM Dept d
    LEFT JOIN UserDept u ON u.deptId = d.id
    WHERE u.userId = :userId
  """)
  fun findAllByUserId(userId: String): List<Dept>
}
