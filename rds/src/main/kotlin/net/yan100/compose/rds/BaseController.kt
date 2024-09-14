/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.hasText
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.entities.withNew
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Validated
@Deprecated("Deprecated, use BaseController instead")
abstract class BaseController<T : IEntity>(protected val service: ICrud<T>) {
  @ResponseBody
  // @GetMapping("meta/all")
  @Operation(summary = "【ǃ RDS】分页查询所有数据")
  fun findAllMeta(@Valid page: Pq?): Pr<T> {
    return service.fetchAllByNotShadowRemoved(page ?: Pq.DEFAULT_MAX)
  }

  @ResponseBody
  // @GetMapping("meta/byId")
  @Operation(summary = "【ǃ RDS】根据id查询数据")
  fun findMetaById(id: String): T? = service.fetchById(id)

  @ResponseBody
  // @GetMapping("meta/count/all")
  @Operation(summary = "【ǃ RDS】当前所有数据总数")
  fun countAllMeta(): Long = service.lenAllByNotShadowRemoved()

  @ResponseBody
  // @GetMapping("meta/exists/byId")
  @Operation(summary = "【ǃ RDS】根据id查询某条数据是否存在")
  fun existsMetaById(id: String): Boolean = service.foundById(id)

  @ResponseBody
  // @PostMapping("meta")
  @Operation(
    summary = "【ǃ RDS】保存数据",
    description =
    """
    调用该接口，传入的实体id会被清除然后保存
  """,
  )
  fun saveMeta(@RequestBody @Valid meta: T): T {
    return service.post(meta.withNew())
  }

  @ResponseBody
  // @PostMapping("meta/all")
  @Operation(
    summary = "【ǃ RDS】保存一组数据",
    description =
    """
    调用该接口，传入的每个实体id都会被清除
  """,
  )
  fun saveAllMeta(@RequestBody metas: List<@Valid T>) = metas.map { it.withNew() }.apply { service.postAll(this) }

  @ResponseBody
  // @PutMapping("meta/byId")
  @Operation(
    summary = "【ǃ RDS】根据实体id修改数据",
    description =
    """
    调用该接口，传入的实体必须存在id
  """,
  )
  fun modifyMeta(@RequestBody @Valid meta: T) = meta.id.apply { service.post(meta) }

  @ResponseBody
  // @PutMapping("meta/byId/all")
  @Operation(
    summary = "【ǃ RDS】根据实体id修改全部数据",
    description =
    """
    调用该接口，传入的每个实体必须存在id
  """,
  )
  fun modifyAllMeta(@RequestBody metas: List<@Valid T>) = metas.filter { it.id.hasText() }.apply { service.postAll(this) }

  @ResponseBody
  // @DeleteMapping("meta/byId")
  @Operation(summary = "【ǃ RDS】根据id删除数据")
  fun deleteMetaById(id: Long) = Unit

  @ResponseBody
  // @DeleteMapping("meta/logic/byId")
  @Operation(summary = "【ǃ RDS】根据id逻辑删除数据")
  fun logicDeleteMetaById(id: String) = service.shadowRemoveById(id)

  @ResponseBody
  // @DeleteMapping("meta/logic/byId/all")
  @Operation(summary = "【ǃ RDS】根据id逻辑删除全部数据")
  fun logicDeleteAllMetaById(ids: List<String>) = service.shadowRemoveAllById(ids)

  @ResponseBody
  // @DeleteMapping("meta/byId/all")
  @Operation(summary = "【ǃ RDS】根据一组id进行删除")
  fun deleteAllMetaByIds(ids: List<String>) = Unit
}
