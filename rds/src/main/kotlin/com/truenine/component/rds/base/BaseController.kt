package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
abstract class BaseController<T : BaseEntity>(
  protected val service: BaseService<T>
) {
  @ResponseBody
  @GetMapping("meta/all")
  @Operation(summary = "【ǃ RDS】分页查询所有数据")
  fun findAllMeta(@Valid page: PagedRequestParam?): PagedResponseResult<T> = service.findAllByNotLogicDeleted(page ?: PagedWrapper.DEFAULT_MAX)

  @ResponseBody
  @GetMapping("meta/byId")
  @Operation(summary = "【ǃ RDS】根据id查询数据")
  fun findMetaById(id: Long): T? = service.findById(id)

  @ResponseBody
  @GetMapping("meta/count/all")
  @Operation(summary = "【ǃ RDS】当前所有数据总数")
  fun countAllMeta(): Long = service.countAllByNotLogicDeleted()

  @ResponseBody
  @GetMapping("meta/exists/byId")
  @Operation(summary = "【ǃ RDS】根据id查询某条数据是否存在")
  fun existsMetaById(id: Long): Boolean = service.existsById(id)

  @ResponseBody
  @PostMapping("meta")
  @Operation(
    summary = "【ǃ RDS】保存数据", description = """
    调用该接口，传入的实体id会被清除然后保存
  """
  )
  fun saveMeta(@RequestBody @Valid meta: T): T {

    return service.save(meta.apply { id = null })
  }

  @ResponseBody
  @PostMapping("meta/all")
  @Operation(
    summary = "【ǃ RDS】保存一组数据", description = """
    调用该接口，传入的每个实体id都会被清除
  """
  )
  fun saveAllMeta(@RequestBody metas: List<@Valid T>) = metas.map { it.id = null;it }.apply { service.saveAll(this) }

  @ResponseBody
  @PutMapping("meta/byId")
  @Operation(
    summary = "【ǃ RDS】根据实体id修改数据", description = """
    调用该接口，传入的实体必须存在id
  """
  )
  fun modifyMeta(@RequestBody @Valid meta: T) = meta.id?.apply { service.save(meta) }

  @ResponseBody
  @PutMapping("meta/byId/all")
  @Operation(
    summary = "【ǃ RDS】根据实体id修改全部数据", description = """
    调用该接口，传入的每个实体必须存在id
  """
  )
  fun modifyAllMeta(@RequestBody metas: List<@Valid T>) = metas.filter { it.id != null }.apply { service.saveAll(this) }

  @ResponseBody
  @DeleteMapping("meta/byId")
  @Operation(summary = "【ǃ RDS】根据id删除数据")
  fun deleteMetaById(id: Long) = Unit

  @ResponseBody
  @DeleteMapping("meta/logic/byId")
  @Operation(summary = "【ǃ RDS】根据id逻辑删除数据")
  fun logicDeleteMetaById(id: Long) = service.logicDeleteById(id)

  @ResponseBody
  @DeleteMapping("meta/logic/byId/all")
  @Operation(summary = "【ǃ RDS】根据id逻辑删除全部数据")
  fun logicDeleteAllMetaById(ids: List<Long>) = service.logicDeleteAllById(ids)

  @ResponseBody
  @DeleteMapping("meta/byId/all")
  @Operation(summary = "【ǃ RDS】根据一组id进行删除")
  fun deleteAllMetaByIds(ids: List<Long>) = Unit
}
