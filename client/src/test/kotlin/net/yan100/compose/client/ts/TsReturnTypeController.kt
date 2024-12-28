package net.yan100.compose.client.ts

import net.yan100.compose.client.jimmer.JimmerEntity
import net.yan100.compose.client.jimmer.by
import net.yan100.compose.core.domain.IPage
import net.yan100.compose.core.domain.IPageParam
import net.yan100.compose.core.typing.ISO4217
import net.yan100.compose.core.typing.StringTyping
import net.yan100.compose.core.typing.UserAgents
import net.yan100.compose.meta.annotations.client.Api
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.web.bind.annotation.*

/**
 * controller 顶层注释
 */
@Api
@RequestMapping("v1/tsReturnType")
@RestController
class TsReturnTypeController {
  companion object {
    val FETCH_ADMIN = newFetcher(
      JimmerEntity::class
    ).by { name() }
  }

  class ExtendsType<T>(
    val generic: List<T>,
    val mapEntry: Map.Entry<String, T>,
    val singleGeneric: T?,
    override val value: String,
    private val thisMap: Map<String, Boolean>
  ) : StringTyping, Map<String, Boolean?> by thisMap, IPage<String> {
    override var pageParam: IPageParam?
      get() = null
      set(value) {}
    override var d: Collection<String>
      get() = emptyList()
      set(value) {}
    override var o: Long?
      get() = null
      set(value) {}
    override var p: Int
      get() = TODO("Not yet implemented")
      set(value) {}
    override var t: Long
      get() = TODO("Not yet implemented")
      set(value) {}
  }

  /**
   * 返回一个继承对象
   */
  @Api
  @GetMapping("returnExtendsClasses")
  fun returnExtendsClasses(): ExtendsType<Any?>? {
    return null
  }

  @Deprecated("该类已被弃用")
  data class DeprecationDataClass(val a: String) {
    data class DoubleDataClass(
      val b: String
    )
  }

  @Api
  @GetMapping("outputFetchBy")
  fun outputFetchBy(): JimmerEntity {
    return JimmerEntity { }
  }

  @Api
  @GetMapping("inputAndOutputDeprecation")
  fun inputAndOutputDeprecation(
    dep: DeprecationDataClass
  ): DeprecationDataClass.DoubleDataClass? {
    return null
  }

  @Api
  @DeleteMapping("/a")
  fun resolveEnums(
    enum: UserAgents,
  ): Map<String, UserAgents?> {
    return mapOf("enum" to enum)
  }

  @Api
  @GetMapping("abc")
  fun returnNullableEmptyEnum(): EmptyEnum? {
    return null
  }

  @Api
  @GetMapping("inputNullInt")
  fun inputNullInt(nullInt: Int?) {

  }

  /**
   * 返回一个数组
   * @param a 数组
   */
  @Api
  @GetMapping("a")
  fun a(a: Int): List<Int> {
    return listOf(1)
  }

  @Api
  @PostMapping("returnEnum")
  fun returnEnum(): ISO4217? {
    return null
  }

  data class GenericClass<A, B, C : Map<A, B>, D : List<A>>(
    val aa: A, val bb: B, val cc: C, val dd: D, val a: String?, val b: List<String>?, val c: Map<*, Map<String, List<String>>>?
  )

  @Api
  @PatchMapping("outputGenericClass")
  fun outputGenericClass(): GenericClass<Int, String, Map<Int, String>, List<Int>>? {
    return null
  }
}
