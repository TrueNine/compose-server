package net.yan100.compose.rds.models.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size


@Schema(title = "登录账号")
interface LoginAccountRequestParam {
  @get:Schema(title = "账号")
  @get:NotBlank(message = "账号不能为空")
  val account: String

  @get:Schema(title = "密码")
  @get:NotBlank(message = "密码不能为空")
  val password: String
}


@Schema(title = "修改账号密码")
interface ModifyAccountPasswordRequestParam {
  @get:NotBlank(message = "账号不能为空")
  val account: String

  @get:NotBlank(message = "密码不能为空")
  @get:Pattern(
    regexp = net.yan100.compose.core.consts.Regexes.PASSWORD,
    message = "密码必须匹配规则为：" + net.yan100.compose.core.consts.Regexes.PASSWORD
  )
  var oldPassword: String

  @get:NotBlank(message = "新密码不能为空")
  @get:Pattern(
    regexp = net.yan100.compose.core.consts.Regexes.PASSWORD,
    message = "密码必须匹配规则为：" + net.yan100.compose.core.consts.Regexes.PASSWORD
  )
  var newPassword: String
}

@Schema(title = "账号注册")
interface RegisterAccountRequestParam {

  @get:Schema(title = "账号")
  @get:NotBlank(message = "账号不可为空")
  var account: String

  @get:NotBlank(message = "昵称不能为空")
  @get:Size(max = 128, min = 4, message = "昵称最长 128，最短 4")
  var nickName: String

  @get:Schema(title = "密码")
  @get:NotBlank(message = "密码不能为空")
  @get:Size(max = 100, min = 8, message = "密码最短8位，最长100")
  @get:Pattern(
    regexp = net.yan100.compose.core.consts.Regexes.PASSWORD,
    message = "密码必须匹配规则为：" + net.yan100.compose.core.consts.Regexes.PASSWORD
  )
  var password: String

  @get:Schema(title = "描述")
  var description: String?
}
