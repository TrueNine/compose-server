package net.yan100.compose.core.domain

/**
 * 策略模式基础接口
 *
 * @param <T> 选择类型
 * @author TrueNine
 * @since 2022-12-11 </T>
 */
@Deprecated("多此一举，当作范例", level = DeprecationLevel.ERROR)
interface IChoose<T> {
  /**
   * 查找第一个符合判断条件的类型
   *
   * @param chooses 策略组
   * @param type 类型
   * @param <R> 实现类
   * @return 实现 </R>
   */
  @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
  fun <R> firstOrNull(chooses: List<IChoose<T>>, type: T): R? {
    return chooses.firstOrNull { it.choose(type) } as? R?
  }

  /**
   * 获取一组实现
   *
   * @param chooses 被选择服务
   * @param type 选择类型
   * @param <R> 实现类
   * @return 选中的一组服务 </R>
   */
  @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
  fun <R> all(chooses: List<IChoose<T>>, type: T): List<R> {
    return chooses
      .filter { ele -> ele.choose(type) }
      .map { r: IChoose<T>? -> r as R }
  }

  /**
   * 类型判断
   *
   * @param type 类型
   * @return 是否为当前类型
   */
  fun choose(type: T): Boolean
}
