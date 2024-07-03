package net.yan100.compose.depend.jvalid.constraints

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [])
@Target(
  AnnotationTarget.TYPE,
  AnnotationTarget.ANNOTATION_CLASS,
  AnnotationTarget.FIELD,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.TYPE_PARAMETER,
  AnnotationTarget.VALUE_PARAMETER,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidChinaName(
  val message: String = "{net.yan100.compose.depend.jvalid.constraints.ValidChinaName.message}",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)
