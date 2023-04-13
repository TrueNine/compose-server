package com.truenine.component.rds.base

import com.truenine.component.rds.util.PagedWrapper
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

abstract class BaseController<T : BaseEntity>(
  private val service: BaseService<T>
) {
  @Operation(summary = "分页查询所有")
  @GetMapping("all")
  @ResponseBody
  fun findAll(@Valid page: PagedRequestParam?): PagedResponseResult<T> = service.findAll(page ?: PagedWrapper.DEFAULT_MAX)

  @Operation(summary = "查询当前总数")
  @GetMapping("count")
  @ResponseBody
  fun countAll(): Long = service.countAll()

  @Operation(summary = "根据id查询实体是否存在")
  @GetMapping("existsById")
  @ResponseBody
  fun existsById(id: Long): Boolean = service.existsById(id)

  @ResponseBody
  protected fun logicDeleteById(id: Long) = service.logicDeleteById(id)

  @ResponseBody
  protected fun deleteAllById(ids: List<Long>) = service.deleteAllById(ids)

  @ResponseBody
  protected fun save(e: T) = service.save(e)

  @ResponseBody
  protected fun saveAll(es: List<T>) = service.saveAll(es)
}
