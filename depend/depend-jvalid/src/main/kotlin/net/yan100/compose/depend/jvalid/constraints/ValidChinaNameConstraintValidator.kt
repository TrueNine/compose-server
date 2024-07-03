package net.yan100.compose.depend.jvalid.constraints

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

open class ValidChinaNameConstraintValidator : ConstraintValidator<ValidChinaName, String> {
  override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
    TODO("Not yet implemented")
  }
}
