package net.yan100.compose.rds.repositories

import java.time.Duration
import net.yan100.compose.Id
import net.yan100.compose.datetime
import net.yan100.compose.i64
import net.yan100.compose.minus
import net.yan100.compose.rds.entities.IJpaEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

/** # 审计查询接口 */
@NoRepositoryBean
interface IAuditRepository<T : IJpaEntity> : IBaseRepository<T> {
  /** ## 根据 id 查询乐观锁版本号 */
  @Query("select e.rlv from #{#entityName} e where e.id = :id")
  fun findRlvById(id: Id): i64

  /** ## 根据 id 查询行修改时间 */
  @Query("select e.mrd from #{#entityName} e where e.id = :id")
  fun findMrdById(id: Id): datetime?

  /** ## 根据 id 查询创建时间与当前时间间隔 */
  fun findCrdBetweenNowDurationById(id: Id): Duration =
    findDurationByIdAndCrdBetween(id, datetime.now())

  fun findDurationByIdAndCrdBetween(
    id: Id,
    dt: datetime = datetime.now(),
  ): Duration = dt - findCrdById(id)

  /** ## 根据 id 查询修改时间与当前时间间隔 */
  fun findMrdBetweenNowDurationById(id: Id): Duration? =
    findDurationByIdAndMrdBetween(id, datetime.now())

  fun findDurationByIdAndMrdBetween(
    id: Id,
    dt: datetime = datetime.now(),
  ): Duration? = findMrdById(id)?.let { dt - it }

  @Query("select e.crd from #{#entityName} e where e.id = :id")
  fun findCrdById(id: Id): datetime
}
