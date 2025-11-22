package io.github.truenine.composeserver.domain

/**
 * Base interface for strategy pattern style selection.
 *
 * @param <T> Selection type
 * @author TrueNine
 * @since 2022-12-11 </T>
 */
@Deprecated("Redundant abstraction, kept only as an example", level = DeprecationLevel.ERROR)
interface IChoose<T> {
  /**
   * Find the first implementation that matches the given type.
   *
   * @param chooses Strategy implementations
   * @param type Selection type
   * @param <R> Implementation type
   * @return First matching implementation, or null if none
   */
  @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
  fun <R> firstOrNull(chooses: List<IChoose<T>>, type: T): R? {
    return chooses.firstOrNull { it.choose(type) } as? R?
  }

  /**
   * Get all implementations that match the given type.
   *
   * @param chooses Candidate services
   * @param type Selection type
   * @param <R> Implementation type
   * @return List of selected services
   */
  @Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
  fun <R> all(chooses: List<IChoose<T>>, type: T): List<R> {
    return chooses.filter { ele -> ele.choose(type) }.map { r: IChoose<T>? -> r as R }
  }

  /**
   * Type matching check.
   *
   * @param type Selection type
   * @return true if this instance matches the type
   */
  fun choose(type: T): Boolean
}
