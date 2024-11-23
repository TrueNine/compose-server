package net.yan100.compose.rds.core.service

import kotlin.reflect.KClass

data class IDynamicFetchScopeService<R : Any>(
  private var result: R? = null,
  private var locked: Boolean = false,
  private var stopped: Boolean = false
) {
  /**
   * ## 用于在书写代码时提前标记类型
   * 并无实际作用
   */
  fun typeOf(resolver: () -> R?) {}
  private var isStopping: Boolean
    get() = stopped || locked
    set(value) {
      stopped = value
      locked = value
    }

  /**
   * ## 用于在书写代码时提前标记类型
   * 并无实际作用
   */
  fun typeOfWithType(resolver: () -> KClass<R>?) {}

  /**
   * ## 根据条件给出结果
   * 按照书写顺序，第一个条件满足即返回，后续条件不再判断且不再执行后续代码
   */
  infix fun Boolean?.toBe(resultResolver: () -> R) {
    if (isStopping) return
    if (this == true) {
      isStopping = true
      result = resultResolver()
    }
  }

  internal val lastResult: R?
    get() {
      isStopping = true
      return result
    }
}

/**
 * ## 执行路由视图模型的函数
 *
 * 该函数主要用于在给定的路由范围内，根据提供的数据传输对象（Dto），生成并处理视图对象（Vo）
 * 它允许在动态获取数据的场景中，以一种类型安全和灵活的方式执行路由相关的操作
 *
 * @param Dto 数据传输对象的类型，表示可以是任何类型
 * @param Vo 视图对象的类型，表示可以是任何类型
 * @param routeScope 路由范围，是一个函数，接受一个动态获取范围服务和一个数据传输对象作为参数，无返回值
 * @return 返回可能的视图对象，如果输入的Dto为null，则返回null
 *
 * 注：该函数使用了泛型，使其能够适用于不同的数据传输对象和视图对象类型
 */
fun <Dto : Any, Vo : Any> Dto?.routeExecuteViewModel(routeScope: IDynamicFetchScopeService<Vo>.(dto: Dto) -> Unit): Vo? {
  if (this == null) return null
  val scope = IDynamicFetchScopeService<Vo>()
  routeScope(scope, this)
  return scope.lastResult
}
