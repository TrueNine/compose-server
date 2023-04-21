package com.truenine.component.rds.repository


import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : BaseRepository<UserEntity> {
  fun findByAccount(account: String): UserEntity?

  @Query("""
    select u.id
    from UserEntity u
    where u.account = :account
  """)
  fun findIdByAccount(account: String):Long

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

  @Query("update UserEntity u set u.banTime = :banTime where u.account = :account")
  @Modifying
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)
}
