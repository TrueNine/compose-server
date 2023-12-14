package net.yan100.compose.rds.repositories.address

import net.yan100.compose.core.consts.DataBaseBasicFieldNames.Rbac
import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.entities.NonDesensitizedAddressDetails
import net.yan100.compose.rds.repositories.base.IRepo
import net.yan100.compose.rds.repositories.base.ITreeRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IAddressRepo : ITreeRepo<Address> {

  @Query("select ((count(a.id) = 1) or (count(a.id) = 1)) from Address a")
  fun isEmpty(): Boolean
  fun findFirstByCode(code: String): Address?

  fun findAllByCodeIn(codes: List<String>): List<Address>

  /**
   * 根据 code 查询当前地址的 id
   */
  @Query("select a.id from Address a where a.code = :code")
  fun findIdByCode(code: String): String

  @Query("from Address e where e.id = '0'")
  fun findRoot(): Address

  fun findRootId(): String {
    return Rbac.ROOT_ID_STR
  }

  fun findAllByCode(code: String): List<Address>

  fun findByCode(code: String): Address?


  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<Address>
}

@Repository
interface INonDesensitizedAddressDetailsRepo : IRepo<NonDesensitizedAddressDetails>
