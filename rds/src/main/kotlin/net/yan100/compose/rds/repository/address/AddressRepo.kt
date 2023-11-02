package net.yan100.compose.rds.repository.address

import net.yan100.compose.rds.base.TreeRepository
import net.yan100.compose.rds.entity.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AddressRepo : TreeRepository<Address> {

  fun findFirstByCode(code: String): Address?

  fun findAllByCodeIn(codes: List<String>): List<Address>



  @Query("from Address e where e.id = '0'")
  fun findRoot(): Address

  fun findAllByCode(code: String): List<Address>

  fun findByCode(code: String): Address?


  fun findByCodeAndName(
    code: String,
    name: String,
    p: Pageable
  ): Page<Address>
}

