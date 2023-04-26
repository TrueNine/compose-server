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
  @GetMapping("all")
  @Operation(summary = "分页查询所有数据")
  fun findAll(@Valid page: PagedRequestParam?): PagedResponseResult<T> = service.findAllByNotLogicDeleted(page ?: PagedWrapper.DEFAULT_MAX)

  @ResponseBody
  @GetMapping("byId/{id}")
  @Operation(summary = "根据id查询数据")
  fun findById(@PathVariable id: Long): T? = service.findById(id)

  @ResponseBody
  @GetMapping("count/all")
  @Operation(summary = "当前所有数据总数")
  fun countAll(): Long = service.countAllByNotLogicDeleted()

  @ResponseBody
  @GetMapping("existsById/{id}")
  @Operation(summary = "根据id查询某条数据是否存在")
  fun existsById(@PathVariable id: Long): Boolean = service.existsById(id)

}
