package net.yan100.compose.rds.core

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.TODO as KotlinTodo

data class IDynamicFetchScope<R : Any>(
  private var nullResultThrow: Boolean = false,
  private var result: R? = null,
) {
  private var todoThrows = false

  /**
   * ## 用于在书写代码时提前标记类型
   * 并无实际作用
   */
  fun todo(resolver: () -> R?) {
    todoThrows = true
  }

  /**
   * ## 用于在书写代码时提前标记类型
   * 并无实际作用
   */
  fun todo(type: KClass<R>) {
    todoThrows = true
  }

  private var defaultResolver: () -> R? = { null }

  /**
   * ## 设置默认结果
   */
  infix fun defaultResult(resolver: () -> R) {
    defaultResolver = resolver
  }

  /**
   * ## 立即终结其他所有条件
   * 等价于 `true takeFinally {...}`，其更符合语境，一般于 逻辑最后进行终结调用
   */
  fun returns(resolver: () -> R?) {
    true takeFinally resolver
  }

  private var finalizer: Boolean = false
  private var onceTakeStopped: Boolean = false
  private var onceExecuteStopped: Boolean = false

  /**
   * ## 条件满足后执行逻辑
   * 如果条件满足，则会忽略所有结果取值以及执行逻辑
   */
  infix fun Boolean?.executeFinally(resolver: () -> Unit) {
    if (finalizer) return
    else if (this == true) {
      finalizer = true
      resolver()
    }
  }

  /**
   * ## 根据条件执行代码块
   */
  infix fun Boolean?.execute(scope: () -> Unit) {
    if (finalizer) return
    if (this == true) scope()
  }

  /**
   * ## 根据条件执行代码块，且只执行一次
   */
  infix fun Boolean?.executeOnce(scope: () -> Unit) {
    if (finalizer) return
    if (onceExecuteStopped) return
    if (this == true) {
      onceExecuteStopped = true
      scope()
    }
  }

  private val onceExecuteMap = ConcurrentHashMap<Any?, R?>()

  /**
   * ## 根据条件执行代码块，且只执行一次
   * @param serial 用于标记执行序列，相同序列的代码块只会执行一次
   */
  fun Boolean?.executeOnce(serial: Any?, scope: () -> Unit) {
    if (finalizer) return
    if (onceExecuteMap.containsKey(serial)) return
    else onceExecuteMap[serial] = null
  }


  /**
   * ## 根据条件取结果
   */
  infix fun Boolean?.take(resultResolver: () -> R?) {
    if (finalizer) return
    if (this == true) {
      result = resultResolver()
    }
  }

  infix fun Boolean?.takeImmediate(result: R?) = this take { result }

  /**
   * ## 根据条件取最前一个结果
   * 按照顺序，第一次条件满足即返回，后续条件不再判断且不再执行后续代码
   */
  infix fun Boolean?.takeOnce(resultResolver: () -> R) {
    if (finalizer) return
    if (onceTakeStopped) return
    if (this == true) {
      onceTakeStopped = true
      result = resultResolver()
    }
  }

  infix fun Boolean?.takeOnceImmediate(result: R) = this takeOnce { result }

  /**
   * ## 根据条件取结果断定
   * 如果条件满足，则后续所有给定条件都会抛弃
   *
   * 后续所有条件和取值将不会执行
   */
  infix fun Boolean?.takeFinally(resultResolver: () -> R?) {
    if (finalizer) return
    if (this == true) {
      finalizer = true
      onceTakeStopped = true
      result = resultResolver()
    }
  }

  infix fun Boolean?.takeFinallyImmediate(result: R?) = this takeFinally { result }

  val lastResult: R? get() = lastResult()
  fun lastResult(): R? {
    return if (todoThrows) KotlinTodo("类型定义在使用后需要清除，移除所有类型定义以解决此问题")
    else {
      val r = result ?: defaultResolver()
      if (r == null && nullResultThrow) error("没有获取到结果")
      r
    }
  }
}

inline fun <reified Dto : Any> IDynamicFetchScope<Dto>.todo() = todo(Dto::class)


/**
 * ## 获取视图对象
 *
 * 该函数用于根据指定的条件，获取一个 vo 或者执行特定代码。
 *
 * @param Dto 数据传输对象的类型，表示可以是任何类型
 * @param Vo 视图对象的类型，表示可以是任何类型
 * @param routeScope 路由范围，是一个函数，接受一个动态获取范围服务和一个数据传输对象作为参数，无返回值
 * @return 返回可能的视图对象，如果输入的Dto为null，则返回null
 *
 * 注：该函数使用了泛型，使其能够适用于不同的数据传输对象和视图对象类型
 * @param nullResultThrow 无结果是否抛出异常，默认为 `false`
 */
fun <Vo : Any, Dto : Any> Dto?.takeViewModel(nullResultThrow: Boolean = false, routeScope: IDynamicFetchScope<Vo>.(dto: Dto) -> Unit): Vo? {
  if (this == null) return null
  val scope = IDynamicFetchScope<Vo>(nullResultThrow = nullResultThrow)
  routeScope(scope, this)
  return scope.lastResult
}

/**
 * @see takeViewModel 设置了默认值
 */
fun <Vo : Any, Dto : Any> Dto?.takeViewModelOrThrow(routeScope: IDynamicFetchScope<Vo>.(dto: Dto) -> Unit): Vo? {
  if (this == null) return null
  val scope = IDynamicFetchScope<Vo>(nullResultThrow = true)
  routeScope(scope, this)
  return scope.lastResult
}

/**
 * @see takeViewModel 单纯执行逻辑而忽略结果
 */
fun <Dto : Any> Dto?.executeViewModel(routeScope: IDynamicFetchScope<Unit>.(dto: Dto) -> Unit) {
  if (this == null) return
  val scope = IDynamicFetchScope<Unit>(nullResultThrow = false)
  routeScope(scope, this)
  scope.lastResult()
}
