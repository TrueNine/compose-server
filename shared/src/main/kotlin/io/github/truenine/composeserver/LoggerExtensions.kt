package io.github.truenine.composeserver

import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Type alias for SLF4J Logger to provide a more descriptive name in the system context.
 *
 * This alias helps distinguish system logging from other types of logging and provides a consistent naming convention across the application.
 */
typealias SystemLogger = Logger

/**
 * SLF4J logger bridge adapter for Kotlin.
 *
 * This object provides a bridge between Kotlin classes and SLF4J logging framework, offering convenient methods to obtain logger instances for Kotlin classes.
 *
 * **Note:** This adapter is deprecated in favor of the top-level slf4j functions which provide a more idiomatic Kotlin approach to logger creation.
 *
 * @see slf4j
 * @author TrueNine
 * @since 2023-02-19
 */
@Deprecated(message = "Deprecated, use top-level slf4j functions for logger implementation instead", replaceWith = ReplaceWith("slf4j<T>()"))
object Slf4jKotlinAdaptor {
  /**
   * Creates a logger instance for the specified Kotlin class.
   *
   * @param kClazz The Kotlin class for which to create the logger
   * @return A [SystemLogger] instance configured for the specified class
   */
  private fun getLog(kClazz: KClass<*>): SystemLogger = LoggerFactory.getLogger(kClazz.java)

  /**
   * Creates a logger instance for the class of the provided object.
   *
   * This method extracts the class information from any object and creates an appropriate logger instance for that class.
   *
   * @param anyWay Any object whose class will be used for logger creation
   * @return A [SystemLogger] instance configured for the object's class
   */
  fun getLog(anyWay: Any): SystemLogger = getLog(anyWay::class)
}

/**
 * Creates a SLF4J logger instance for the specified Java class.
 *
 * This function provides a direct way to create logger instances using Java Class objects, which is useful when working with Java interoperability or when you
 * have a Class reference.
 *
 * @param clz The Java class for which to create the logger
 * @return A [SystemLogger] instance configured for the specified class
 * @sample
 *
 * ```kotlin
 * val logger = slf4j(MyClass::class.java)
 * logger.info("Hello from MyClass")
 * ```
 */
@Deprecated(
  message = "Deprecated, use top-level slf4j functions for logger implementation instead",
  replaceWith = ReplaceWith("slf4j<T>()", imports = ["io.github.truenine.composeserver.logger"]),
)
fun slf4j(clz: Class<*>): SystemLogger = LoggerFactory.getLogger(clz)

fun logger(clz: Class<*>): SystemLogger = LoggerFactory.getLogger(clz)

/**
 * Creates a SLF4J logger instance for the specified Kotlin class.
 *
 * This function accepts a Kotlin class reference and creates an appropriate logger instance. If no class is specified, it defaults to using the SystemLogger
 * class itself.
 *
 * @param kClass The Kotlin class for which to create the logger (defaults to SystemLogger::class)
 * @return A [SystemLogger] instance configured for the specified class
 * @sample
 *
 * ```kotlin
 * val logger = slf4j(MyClass::class)
 * logger.info("Hello from MyClass")
 * ```
 */
@Deprecated(
  message = "Deprecated, use inline logger<T>() instead. Replace slf4j(YourClass::class) with logger<YourClass>()",
  replaceWith = ReplaceWith("logger<T>()", imports = ["io.github.truenine.composeserver.logger"]),
)
fun slf4j(kClass: KClass<*> = SystemLogger::class): SystemLogger = slf4j(kClass.java)

fun logger(kClass: KClass<*> = SystemLogger::class): SystemLogger = logger(kClass.java)

/**
 * Creates a SLF4J logger instance for the reified type parameter.
 *
 * This inline function uses Kotlin's reified type parameters to automatically determine the class for which to create the logger. This is the most convenient
 * way to create loggers in Kotlin as it requires no explicit class parameters.
 *
 * @param T The type for which to create the logger (automatically inferred)
 * @return A [SystemLogger] instance configured for type T
 * @sample
 *
 * ```kotlin
 * class MyService {
 *     private val logger = slf4j<MyService>()
 *
 *     fun doSomething() {
 *         logger.info("Doing something...")
 *     }
 * }
 * ```
 */
@Deprecated(
  message = "Deprecated, use logger<T>() instead for consistent naming",
  replaceWith = ReplaceWith("logger<T>()", imports = ["io.github.truenine.composeserver.logger"]),
)
inline fun <reified T : Any> slf4j(): SystemLogger = slf4j(T::class)

/**
 * Creates a SLF4J logger instance for the reified type parameter.
 *
 * This function is an alias for [slf4j] with reified type parameters, providing an alternative naming convention that some developers may prefer. It offers the
 * same functionality as the slf4j<T>() function.
 *
 * @param T The type for which to create the logger (automatically inferred)
 * @return A [SystemLogger] instance configured for type T
 * @sample
 *
 * ```kotlin
 * class MyController {
 *     private val log = logger<MyController>()
 *
 *     fun handleRequest() {
 *         log.debug("Handling request...")
 *     }
 * }
 * ```
 */
inline fun <reified T : Any> logger(): SystemLogger = logger(T::class)
