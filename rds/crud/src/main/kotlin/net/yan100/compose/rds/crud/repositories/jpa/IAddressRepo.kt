package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IDbNames.Rbac
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.ITreeRepo
import net.yan100.compose.rds.crud.entities.jpa.Address
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Primary
@Repository
interface IAddressRepo : ITreeRepo<Address> {
  fun existsByCode(code: string): Boolean

  fun existsAllByCodeIn(codes: List<string>): Boolean

  fun countAllByCodeIn(codes: List<string>): Int

  fun findFirstByCodeAndLevel(code: string, level: Int): Address?

  /** ## 查询所有除了 村行政区以外的满编地址 */
  @Query(
    """
        from Address a
        where a.level < 5
        and length(a.code) = 12
    """
  )
  fun findAllByPadCode(): Set<Address>

  @Query("select ((count(a.id) = 1) or (count(a.id) = 1)) from Address a")
  fun isEmpty(): Boolean

  fun findFirstByCode(code: String): Address?

  fun findAllByCodeIn(codes: List<String>): List<Address>

  /** 根据 code 查询当前地址的 id */
  @Query("select a.id from Address a where a.code = :code")
  fun findIdByCode(code: String): String

  @Query("from Address e where e.id = :rootId")
  fun findRoot(rootId: RefId = Rbac.ROOT_ID): Address

  fun findRootId(): RefId {
    return Rbac.ROOT_ID
  }

  fun findAllByCode(code: String): List<Address>

  fun findByCode(code: String): Address?

  fun findByCodeAndName(code: String, name: String, p: Pageable): Page<Address>
}
