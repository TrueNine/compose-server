package com.truenine.component.rds.repo


import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.UserDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : BaseRepo<UserDao, String> {
  fun findByAccount(account: String): UserDao?

  @Query(
    """
    select pwdEnc
    from UserDao
    where account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  fun findAllByNickName(nickName: String): List<UserDao>

  fun existsAllByAccount(account: String): Boolean
}
