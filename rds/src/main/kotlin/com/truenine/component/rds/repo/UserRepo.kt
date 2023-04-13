package com.truenine.component.rds.repo


import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : BaseRepo<UserEntity> {
  fun findByAccount(account: String): UserEntity?

  @Query(
      """
    select pwdEnc
    from UserEntity
    where account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  fun findAllByNickName(nickName: String): List<UserEntity>

  fun existsAllByAccount(account: String): Boolean
}
