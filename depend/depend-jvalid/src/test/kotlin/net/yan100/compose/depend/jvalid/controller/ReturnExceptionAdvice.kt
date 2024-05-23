package net.yan100.compose.depend.jvalid.controller

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ReturnExceptionAdvice {
  @ResponseBody
  @ExceptionHandler(Exception::class)
  fun exception(ex: Exception): Exception {
    ex.printStackTrace()
    return ex
  }
}
