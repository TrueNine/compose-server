package net.yan100.compose.client.interceptors

import net.yan100.compose.client.interceptors.kt.JavaInternalInterceptor
import net.yan100.compose.client.interceptors.ts.*

typealias InterceptorA = Interceptor<*, *, *>

// client type to client type
val standardClientTypeToClientTypeInterceptors: List<InterceptorA> =
  listOf(JavaInternalInterceptor())

// clientTypeName to clientTypeName
val standardClientTypeToTsScopeInterceptors =
  listOf(
    TsBuiltinInterceptor(),
    TsStaticInterceptor(),
    TsEnumInterceptor(),
    TsTypeAliasInterceptor(),
    TsJimmerInterceptor(),
    //
    TsSpringMvcInterceptor(),
    TsBuiltinTypeValInterceptor(),
    TsTypeValPreReferenceInterceptor(),
    TsMapEntryInterceptor(),
    TsListTypeValInterceptor(),
    TsMapPostInterceptor(),
    // post scope
    TsPostScopeUseGenericInterceptor(),
  )

val standardNameInterceptors =
  listOf(QualifierNameInterceptor.KotlinNameToJavaNameInterceptor())

// clientType to tsType
val standardInterceptors: List<InterceptorA> =
  listOf(
      standardClientTypeToClientTypeInterceptors,
      standardNameInterceptors,
      standardClientTypeToTsScopeInterceptors,
    )
    .flatten()
