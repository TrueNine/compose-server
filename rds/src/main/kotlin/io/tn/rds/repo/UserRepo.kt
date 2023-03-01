package io.tn.rds.repo


import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.UserDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : BaseRepo<UserDao, String> {
  fun findByAccount(account: String): io.tn.rds.dao.UserDao?

  @Query(
    """
    select pwdEnc
    from UserDao
    where account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  fun findAllByNickName(nickName: String): List<io.tn.rds.dao.UserDao>

  fun existsAllByAccount(account: String): Boolean
}
