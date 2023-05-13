package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.UserGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository : BaseRepository<UserGroupEntity> {
  @Query(
    """
    from UserGroupEntity ug
    left join UserGroupUserEntity ugu
    on ug.id = ugu.userGroupId
    where ug.userId = :userId
    or ugu.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): MutableList<UserGroupEntity>

  @Query(
    """
    from UserGroupEntity ug
    left join UserGroupUserEntity ugu on ug.id = ugu.userGroupId
    left join UserEntity u on u.id = ugu.userId
    where u.account = :account
  """
  )
  fun findAllByUserAccount(account: String): MutableList<UserGroupEntity>


  fun existsByIdAndUserId(id: String, userId: String): Boolean
}
