package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.jpa.Dept
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("IDeptRepository")
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
