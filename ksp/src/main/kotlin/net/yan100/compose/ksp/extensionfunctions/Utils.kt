/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.ksp.extensionfunctions

import com.google.devtools.ksp.ExceptionMessage
import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KSTypesNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@KspExperimental
inline fun <reified T : Annotation> KSAnnotation.toAnnotation(): T {
  return toAnnotation(T::class.java)
}

@KspExperimental
fun <T : Annotation> KSAnnotation.toAnnotation(annotationClass: KClass<T>): T {
  return toAnnotation(annotationClass.java)
}

@KspExperimental
@Suppress("UNCHECKED_CAST")
fun <T : Annotation> KSAnnotation.toAnnotation(annotationClass: Class<T>): T {
  return Proxy.newProxyInstance(annotationClass.classLoader, arrayOf(annotationClass), createInvocationHandler(annotationClass)) as T
}

@KspExperimental
@Suppress("TooGenericExceptionCaught")
private fun KSAnnotation.createInvocationHandler(clazz: Class<*>): InvocationHandler {
  val cache = ConcurrentHashMap<Pair<Class<*>, Any>, Any>(arguments.size)
  return InvocationHandler { proxy, method, _ ->
    if (method.name == "toString" && arguments.none { it.name?.asString() == "toString" }) {
      clazz.canonicalName +
        arguments
          .map { argument: KSValueArgument ->
            val methodName = argument.name?.asString()
            val value = proxy.javaClass.methods.find { m -> m.name == methodName }?.invoke(proxy)
            "$methodName=$value"
          }
          .toList()
    } else {
      val argument = arguments.first { it.name?.asString() == method.name }
      when (val result = argument.value ?: method.defaultValue) {
        is Proxy -> result
        is List<*> -> cache.getOrPut(Pair(method.returnType, result)) { result.asArray(method, clazz) }
        else -> {
          when {
            method.returnType.name == "byte" -> cache.getOrPut(Pair(method.returnType, result)) { result.asByte() }
            method.returnType.name == "short" -> cache.getOrPut(Pair(method.returnType, result)) { result.asShort() }
            method.returnType.name == "long" -> cache.getOrPut(Pair(method.returnType, result)) { result.asLong() }
            method.returnType.name == "float" -> cache.getOrPut(Pair(method.returnType, result)) { result.asFloat() }
            method.returnType.name == "double" -> cache.getOrPut(Pair(method.returnType, result)) { result.asDouble() }
            method.returnType.isEnum -> cache.getOrPut(Pair(method.returnType, result)) { result.asEnum(method.returnType) }
            method.returnType.isAnnotation -> cache.getOrPut(Pair(method.returnType, result)) { (result as KSAnnotation).asAnnotation(method.returnType) }
            method.returnType.isArray -> {
              check(result is Array<*>) { "unhandled value type, $ExceptionMessage" }
              val value = { result.asArray(method, clazz) }
              cache.getOrPut(Pair(method.returnType, value), value)
            }
            method.returnType.name == "java.lang.Class" -> {
              cache.getOrPut(Pair(method.returnType, result)) {
                when (result) {
                  is KSType -> result.asClass(clazz)
                  else -> Class.forName(result.javaClass.methods.first { it.name == "getCanonicalText" }.invoke(result, false) as String)
                }
              }
            }
            else -> result
          }
        }
      }
    }
  }
}

@KspExperimental
private fun KSAnnotation.asAnnotation(
  annotationInterface: Class<*>,
): Any {
  return Proxy.newProxyInstance(annotationInterface.classLoader, arrayOf(annotationInterface), this.createInvocationHandler(annotationInterface)) as Proxy
}

@KspExperimental
@Suppress("UNCHECKED_CAST")
private fun List<*>.asArray(method: Method, proxyClass: Class<*>) =
  when (method.returnType.componentType.name) {
    "boolean" -> (this as List<Boolean>).toBooleanArray()
    "byte" -> (this as List<Byte>).toByteArray()
    "short" -> (this as List<Short>).toShortArray()
    "char" -> (this as List<Char>).toCharArray()
    "double" -> (this as List<Double>).toDoubleArray()
    "float" -> (this as List<Float>).toFloatArray()
    "int" -> (this as List<Int>).toIntArray()
    "long" -> (this as List<Long>).toLongArray()
    "java.lang.Class" -> (this as List<KSType>).asClasses(proxyClass).toTypedArray()
    "java.lang.String" -> (this as List<String>).toTypedArray()
    else -> {
      when {
        method.returnType.componentType.isEnum -> toArray(method) { result -> result.asEnum(method.returnType.componentType) }
        method.returnType.componentType.isAnnotation -> toArray(method) { result -> (result as KSAnnotation).asAnnotation(method.returnType.componentType) }
        else -> error("Unable to process type ${method.returnType.componentType.name}")
      }
    }
  }

@Suppress("UNCHECKED_CAST")
private fun List<*>.toArray(method: Method, valueProvider: (Any) -> Any): Array<Any?> {
  val array: Array<Any?> = java.lang.reflect.Array.newInstance(method.returnType.componentType, this.size) as Array<Any?>
  for (r in indices) {
    array[r] = this[r]?.let { valueProvider.invoke(it) }
  }
  return array
}

@Suppress("UNCHECKED_CAST")
private fun <T> Any.asEnum(returnType: Class<T>): T =
  returnType
    .getDeclaredMethod("valueOf", String::class.java)
    .invoke(
      null,
      if (this is KSType) {
        this.declaration.simpleName.getShortName()
      } else {
        this.toString()
      }
    ) as T

private fun Any.asByte(): Byte = if (this is Int) this.toByte() else this as Byte

private fun Any.asShort(): Short = if (this is Int) this.toShort() else this as Short

private fun Any.asLong(): Long = if (this is Int) this.toLong() else this as Long

private fun Any.asFloat(): Float = if (this is Int) this.toFloat() else this as Float

private fun Any.asDouble(): Double = if (this is Int) this.toDouble() else this as Double

@KspExperimental
private fun KSType.asClass(proxyClass: Class<*>) =
  try {
    Class.forName(this.declaration.qualifiedName!!.asString(), true, proxyClass.classLoader)
  } catch (e: Exception) {
    throw KSTypeNotPresentException(this, e)
  }

@KspExperimental
private fun List<KSType>.asClasses(proxyClass: Class<*>) =
  try {
    this.map { type -> type.asClass(proxyClass) }
  } catch (e: Exception) {
    throw KSTypesNotPresentException(this, e)
  }

@KspExperimental private fun Any.asArray(method: Method, proxyClass: Class<*>) = listOf(this).asArray(method, proxyClass)
