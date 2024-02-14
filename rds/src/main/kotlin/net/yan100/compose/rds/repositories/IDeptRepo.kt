package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.Dept
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface IDeptRepo : IRepo<Dept> {
    @Query(
        """
    FROM Dept d
    LEFT JOIN UserDept u ON u.deptId = d.id
    WHERE u.userId = :userId
  """
    )
    fun findAllByUserId(userId: String): List<Dept>
}
