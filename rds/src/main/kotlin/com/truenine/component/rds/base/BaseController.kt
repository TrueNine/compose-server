package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

abstract class BaseController<T : BaseEntity>(
  protected val service: BaseService<T>
) {
  @Operation(summary = "分页查询所有数据")
  @GetMapping("all")
  @ResponseBody
  fun findAll(@Valid @RequestBody page: PagedRequestParam?): PagedResponseResult<T> = service.findAllByNotLogicDeleted(page ?: PagedWrapper.DEFAULT_MAX)

  @Operation(summary = "查询当前总数")
  @GetMapping("count")
  @ResponseBody
  fun countAll(): Long = service.countAllByNotLogicDeleted()

  @Operation(summary = "根据id查询实体是否存在")
  @GetMapping("existsById")
  @ResponseBody
  fun existsById(id: Long): Boolean = service.existsById(id)
}
