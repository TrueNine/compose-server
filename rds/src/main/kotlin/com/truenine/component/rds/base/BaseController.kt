package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody


abstract class BaseController<T : BaseEntity>(
  protected val service: BaseService<T>
) {
  @ResponseBody
  @GetMapping("items/all")
  @Operation(summary = "分页查询所有数据")
  fun findAll(@Valid page: PagedRequestParam?): PagedResponseResult<T> = service.findAllByNotLogicDeleted(page ?: PagedWrapper.DEFAULT_MAX)

  @ResponseBody
  @GetMapping("items/{id}")
  @Operation(summary = "根据id查询单条数据")
  fun findById(@PathVariable id: Long): T? = service.findById(id)

  @ResponseBody
  @GetMapping("items/countAll")
  @Operation(summary = "当前数据总数")
  fun countAll(): Long = service.countAllByNotLogicDeleted()

  @ResponseBody
  @GetMapping("items/{id}/exists")
  @Operation(summary = "根据id查询单条数据是否存在")
  fun existsById(@PathVariable id: Long): Boolean = service.existsById(id)
}
